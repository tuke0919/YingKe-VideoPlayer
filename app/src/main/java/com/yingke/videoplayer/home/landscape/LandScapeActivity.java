package com.yingke.videoplayer.home.landscape;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.controller.MediaController;
import com.yingke.videoplayer.base.BaseActivity;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.frag.HomeFragment;
import com.yingke.videoplayer.home.frag.RecommendFragment;
import com.yingke.videoplayer.home.player.ListIjkVideoView;
import com.yingke.videoplayer.home.util.SinglePlayerManager;
import com.yingke.videoplayer.tiktok.PagerLayoutManager;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.StringUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.yingke.videoplayer.main.MainActivity.TAB_HOME;

/**
 * 功能：视频播放器 横屏播放器
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-29
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class LandScapeActivity extends BaseActivity implements PagerLayoutManager.OnPageChangedListener{

    public static final String TAG = "LandScapeActivity";


    // 竖屏时的位置
    private int mOldPortPosition = -1;
    // 列表
    private RecyclerView mLandRecyclerView;
    // 适配器
    private LandListVideoAdapter mLandListVideoAdapter;
    // 横屏播放数据
    private IVideoBean mLandVideoBean;
    // 当前横屏的播放器
    private BaseListVideoView mCurrentLandVideoView;

    // for single
    // 父容器
    protected ViewParent mPlayerParent;
    // 单个视屏全屏
    protected boolean mIsFullScreenSingle = false;

    // 是否全屏状态
    protected boolean mIsFullScreen = false;


    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        PlayerLog.e(TAG, "initRecyclerView: ");
        mLandRecyclerView = new RecyclerView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLandRecyclerView.setLayoutParams(params);

        PagerLayoutManager layoutManager = new PagerLayoutManager(this, OrientationHelper.VERTICAL);
        mLandRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setListener(this);

        mLandListVideoAdapter = new LandListVideoAdapter(this);
        mLandListVideoAdapter.setListener(new LandListVideoAdapter.OnBackListener() {
            @Override
            public void onBack() {
                exitFullScreen();
            }
        });
        mLandRecyclerView.setAdapter(mLandListVideoAdapter);

    }

    /**
     * 初始化数据 实际项目中，此处可添加网络数据
     */
    private void initDatas(){
        PlayerLog.e(TAG, "initDatas: ");
        // 模拟网络数据
        String videoListJson = StringUtil.getJsonData(this, "list_rec_land_video.json");
        if (!TextUtils.isEmpty(videoListJson)) {
            List<ListVideoData> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListVideoData>>() {}.getType());
            for (ListVideoData data : listVideoData) {
                // 设置首播类型
                data.setCurrentType(data.getFirstType());

                File thumbFile = FileUtil.getVideoThumbFile(this,  data.getUrl(), EncryptUtils.LAND_REC_VIDEO);
                if (thumbFile.exists()) {
                    data.setThumbPath(thumbFile.getAbsolutePath());
                }

                File thumbAdFile = FileUtil.getVideoThumbFile(this,  data.getUrl(), EncryptUtils.AD_REC_VIDEO);
                if (thumbAdFile.exists()) {
                    data.setAdThumbPath(thumbAdFile.getAbsolutePath());
                }
            }
            // 第一个视频是竖屏过来的
            ListVideoData portCurrentVideoData = (ListVideoData) SinglePlayerManager.getInstance().getCurrentVideoBean();
            if (portCurrentVideoData != null) {
                int index = listVideoData.indexOf(portCurrentVideoData);
                if (index != -1) {
                    // 去掉重复
                    listVideoData.remove(index);
                }
                listVideoData.add(0, portCurrentVideoData);
            }
            mLandListVideoAdapter.addAllDatas(listVideoData);
        }
    }


    /**
     * 进入横屏
     * @param portListVideoView 当前播放器，此时还是竖屏播放器
     */
    public void enterFullScreen(BaseListVideoView portListVideoView) {
        PlayerLog.e(TAG, "enterFullScreen: ");
        mIsFullScreen = true;
       if (mIsFullScreenSingle) {
           enterFullScreenForSingle(portListVideoView);
       } else {
           enterFullScreenForList(portListVideoView);
       }
    }


    /**
     * 进入全屏 仅单个视频
     * @param portListVideoView
     */
    public void enterFullScreenForSingle(BaseListVideoView portListVideoView) {
        ViewGroup contentView = findViewById(android.R.id.content);

        mCurrentLandVideoView = portListVideoView;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mPlayerParent = portListVideoView.getParent();
        if (mPlayerParent instanceof ViewGroup) {
            ((ViewGroup) mPlayerParent).removeView(portListVideoView);
        }
        contentView.addView(portListVideoView, params);
    }

    /**
     * 进入全屏 横屏视频列表
     * @param portListVideoView
     */
    public void enterFullScreenForList(BaseListVideoView portListVideoView) {
        // 禁止画中画
        SinglePlayerManager.getInstance().enablePip(false);

        Fragment bottomFragment = getSupportFragmentManager().findFragmentByTag(TAB_HOME);
        if (bottomFragment instanceof HomeFragment) {
            Fragment currentFragment = ((HomeFragment) bottomFragment).getCurrentFragment();
            if (currentFragment instanceof RecommendFragment) {
                // 点击竖屏视频位置
                mOldPortPosition = ((RecommendFragment) currentFragment).getPortPosition();
            }
        }

        PlayerLog.e(TAG, "enterFullScreen: mOldPortPosition = " + mOldPortPosition);

        mCurrentLandVideoView = portListVideoView;
        // 移除竖屏播放器
        SinglePlayerManager.getInstance().removePlayerNotRelease();

        // 添加列表
        ViewGroup contentView = findViewById(android.R.id.content);
        initRecyclerView();
        initDatas();
        contentView.addView(mLandRecyclerView);

        // 更新成当前列表
        SinglePlayerManager.getInstance().attachRecycleView(mLandRecyclerView);
    }


    /**
     * 进入竖屏
     */
    public void exitFullScreen() {
        PlayerLog.e(TAG, "exitFullScreen: ");
        mIsFullScreen = false;
        if (mIsFullScreenSingle){
            exitFullScreenForSingle();
        } else {
            exitFullScreenForList();
        }
    }

    /**
     * 退出全屏 仅单个视频
     */
    public void exitFullScreenForSingle() {
        if (mPlayerParent instanceof ViewGroup) {
            ViewGroup contentView = findViewById(android.R.id.content);
            contentView.removeView(mCurrentLandVideoView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            ((ViewGroup) mPlayerParent).addView(mCurrentLandVideoView, params);

            mPlayerParent = null;
            mCurrentLandVideoView = null;
        }
    }

    /**
     * 退出全屏 横屏视频列表
     */
    public void exitFullScreenForList() {
        if (mCurrentLandVideoView != null) {

            ViewParent landVideoParent = mCurrentLandVideoView.getParent();
            if (landVideoParent instanceof ViewGroup) {
                ((ViewGroup) landVideoParent).removeView(mCurrentLandVideoView);
            }

            // 发送事件 竖屏列表更新更新数据
            EventBus.getDefault().post(new LandScapeVideoEvent(mOldPortPosition, mLandVideoBean, mCurrentLandVideoView));

            // 销毁横屏列表
            ViewGroup contentView = findViewById(android.R.id.content);
            contentView.removeView(mLandRecyclerView);

            mCurrentLandVideoView = null;
            mLandRecyclerView = null;
            mLandListVideoAdapter = null;
            mLandVideoBean = null;
            mCurrentLandPosition = -1;
        }
    }


    // 当前位置
    private int mCurrentLandPosition = -1;


    @Override
    public void onFirstPageAttached(View itemView) {
        PlayerLog.e(TAG, "onFirstPageAttached: position = " + 0);
        mCurrentLandPosition = 0;
        mLandVideoBean = mLandListVideoAdapter.getItem(0);

        ViewParent landVideoParent = mCurrentLandVideoView.getParent();
        if (landVideoParent instanceof ViewGroup) {
            ((ViewGroup) landVideoParent).removeView(mCurrentLandVideoView);
        }

        LandListVideoVH landListVideoVH = getLandListVideoVH(itemView);
        landListVideoVH.attachVideoPlayer(mCurrentLandVideoView);

    }

    @Override
    public void onPageSelected(int position, View itemView, boolean isBottom) {
        PlayerLog.e(TAG, "onPageSelected: position = " + position);

        if (mCurrentLandPosition != position) {
            mCurrentLandPosition = position;
            mLandVideoBean = mLandListVideoAdapter.getItem(position);
            if (mLandVideoBean != null) {
                // 新生成播放器
                ListIjkVideoView landIjkVideoView = new ListIjkVideoView(this);
                landIjkVideoView.setTag("land_ijkVideoView");
                landIjkVideoView.setFullScreenStatus(true);

                mCurrentLandVideoView = landIjkVideoView;
                // 绑定播放器
                LandListVideoVH landListVideoVH = getLandListVideoVH(itemView);
                landListVideoVH.attachVideoPlayer(mCurrentLandVideoView);
                // 绑定播放器 到管理器 同时释放上一个播放器
                SinglePlayerManager.getInstance().attachVideoPlayer(mLandVideoBean, landIjkVideoView);
                // 设置数据
                landIjkVideoView.setVideoOnline(mLandVideoBean);

                // 发送数据更新竖屏列表的数据
                EventBus.getDefault().post(new LandScapeVideoEvent(mOldPortPosition, mLandVideoBean, null));
            }
        }

    }

    @Override
    public void onPageDetached(boolean isNext, int position, View itemView) {
        PlayerLog.e(TAG, "onPageDetached: position = " + position);
        if (mCurrentLandPosition == position) {
            // 释放上一个播放器
            SinglePlayerManager.getInstance().releaseVideoPlayer();
        }
    }


    /**
     *
     * @param itemView Holder根布局
     * @return
     */
    public LandListVideoVH getLandListVideoVH(View itemView) {
        RecyclerView.ViewHolder holder = mLandRecyclerView.findContainingViewHolder(itemView);
        if (holder instanceof LandListVideoAdapter.LandVideoViewHolder) {
            LandListVideoVH listVideoVH = ((LandListVideoAdapter.LandVideoViewHolder) holder).getLandListVideoVH();
            return listVideoVH;
        }
        return null;
    }


    /**
     * 横屏发送给竖屏的数据
     */
    public class LandScapeVideoEvent {
        private int mOldPosition;
        private IVideoBean mLandVideoBean;
        private BaseListVideoView mLandVideoView;

        public LandScapeVideoEvent(int oldPosition, IVideoBean landVideoBean, BaseListVideoView landVideoView) {
            mOldPosition = oldPosition;
            mLandVideoBean = landVideoBean;
            mLandVideoView = landVideoView;
        }

        public int getOldPosition() {
            return mOldPosition;
        }

        public IVideoBean getLandVideoBean() {
            return mLandVideoBean;
        }

        public BaseListVideoView getLandVideoView() {
            return mLandVideoView;
        }
    }

    /**
     * 显示更多
     */
    public void showMore() {
        PlayerLog.e(TAG, "showMore: ");
//        HalfTransDialog dialogFragment = new HalfTransDialog(this);
//        dialogFragment.show();
        HalfTransDialogFragment dialogFragment = new HalfTransDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "tag");
    }


    @Override
    public void onBackPressed() {
        if (mIsFullScreen && mCurrentLandVideoView != null) {
            // 退出全屏
            MediaController controller = mCurrentLandVideoView.getControllerView();
            if (controller != null) {
                mCurrentLandVideoView.getControllerView().setRequestedOrientation();
            }
        } else {
            super.onBackPressed();
        }
    }
}
