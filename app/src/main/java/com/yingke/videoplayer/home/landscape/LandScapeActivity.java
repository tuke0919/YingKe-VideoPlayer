package com.yingke.videoplayer.home.landscape;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.util.PlayerLog;
import com.yingke.player.java.controller.MediaController;
import com.yingke.videoplayer.base.BaseActivity;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.frag.HomeFragment;
import com.yingke.videoplayer.home.frag.RecommendFragment;
import com.yingke.videoplayer.home.landscape.dialog.MoreDialog;
import com.yingke.videoplayer.home.landscape.dialog.ResolutionDialog;
import com.yingke.videoplayer.home.player.ListIjkVideoView;
import com.yingke.videoplayer.home.util.SinglePlayerManager;
import com.yingke.videoplayer.tiktok.PagerLayoutManager;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.StringUtil;
import com.yingke.videoplayer.util.ToastUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.yingke.videoplayer.home.landscape.LandScapeActivity.LandScapeVideoEvent.EVENT_EXIT_FULLSCREEN;
import static com.yingke.videoplayer.home.landscape.LandScapeActivity.LandScapeVideoEvent.EVENT_UPDATE_SELECTED;
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
public class LandScapeActivity extends BaseActivity implements
        PagerLayoutManager.OnPageChangedListener,
        MoreDialog.OnMoreListener,
        ResolutionDialog.OnResolutionListener{

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
    // 单个视频/视频列表
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
        beforeEnterFullScreen();

        mIsFullScreen = true;
       if (mIsFullScreenSingle) {
           enterFullScreenForSingle(portListVideoView);
       } else {
           enterFullScreenForList(portListVideoView);
       }
    }

    /**
     * 渐入全屏前设置数据
     */
    protected void beforeEnterFullScreen() {

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
        // 设置倍速
        setSpeedPos(mCurrentLandVideoView.getControllerView().getSpeedPos());
    }

    /**
     * 进入全屏 横屏视频列表
     * @param portListVideoView
     */
    public void enterFullScreenForList(BaseListVideoView portListVideoView) {
        // 禁止画中画
        SinglePlayerManager.getInstance().enablePip(false);

        // 获取原来点击的位置，TODO 不应该有具体Fragment
//        Fragment bottomFragment = getSupportFragmentManager().findFragmentByTag(TAB_HOME);
//        if (bottomFragment instanceof HomeFragment) {
//            Fragment currentFragment = ((HomeFragment) bottomFragment).getCurrentFragment();
//            if (currentFragment instanceof RecommendFragment) {
//                // 点击竖屏视频位置
//                mOldPortPosition = ((RecommendFragment) currentFragment).getPortPosition();
//            }
//        }

        PlayerLog.e(TAG, "enterFullScreen: mOldPortPosition = " + mOldPortPosition);

        mCurrentLandVideoView = portListVideoView;
        // 点击竖屏视频位置
        mOldPortPosition = RecommendFragment.getPortPosition();
        // 移除竖屏播放器
        SinglePlayerManager.getInstance().removePlayerNotRelease();

        // 添加列表
        ViewGroup contentView = findViewById(android.R.id.content);
        initRecyclerView();
        initDatas();
        contentView.addView(mLandRecyclerView);


        IVideoBean videoBean = mCurrentLandVideoView.getIjkVideoView().getVideoBean();

        // 更新成当前列表
        SinglePlayerManager.getInstance().attachVideoPlayer(videoBean, mCurrentLandVideoView);
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

        afterExitFullScreen();
    }

    /**
     * 退出全屏后 设置
     */
    protected void afterExitFullScreen() {

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

            // 原播放器Parent，重新添加
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
            EventBus.getDefault().post(new LandScapeVideoEvent(EVENT_EXIT_FULLSCREEN, mOldPortPosition, mLandVideoBean, mCurrentLandVideoView));

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

        // 设置倍速
        setSpeedPos(mCurrentLandVideoView.getControllerView().getSpeedPos());

    }

    @Override
    public void onPageSelected(int position, View itemView, boolean isBottom) {
        PlayerLog.e(TAG, "onPageSelected: position = " + position);

        if (mCurrentLandPosition != position) {
            mCurrentLandPosition = position;
            isMirrorEnable = false;
            mLandVideoBean = mLandListVideoAdapter.getItem(position);
            if (mLandVideoBean != null) {
                // 新生成播放器
                ListIjkVideoView landIjkVideoView = new ListIjkVideoView(this);
                landIjkVideoView.setTag("land_ijkVideoView");
                landIjkVideoView.setFullScreenStatus(true);

                mCurrentLandVideoView = landIjkVideoView;
                // 设置当前倍速
                mCurrentLandVideoView.getControllerView().setSpeed(getSpeedPos());

                // 绑定播放器
                LandListVideoVH landListVideoVH = getLandListVideoVH(itemView);
                landListVideoVH.attachVideoPlayer(mCurrentLandVideoView);
                // 绑定播放器 到管理器 同时释放上一个播放器
                SinglePlayerManager.getInstance().attachVideoPlayer(mLandVideoBean, landIjkVideoView);
                // 设置数据
                landIjkVideoView.setVideoOnline(mLandVideoBean);

                // 发送数据更新竖屏列表的数据
                EventBus.getDefault().post(new LandScapeVideoEvent(EVENT_UPDATE_SELECTED, mOldPortPosition, mLandVideoBean, null));
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

        // 更新 已选择Item
        public static final int EVENT_UPDATE_SELECTED = 0;
        // 退出全屏
        public static final int EVENT_EXIT_FULLSCREEN = 1;

        private int mEventType;
        private int mOldPortPosition;
        private IVideoBean mLandVideoBean;
        private BaseListVideoView mLandVideoView;

        public LandScapeVideoEvent(int eventType,
                                   int oldPortPosition,
                                   IVideoBean landVideoBean,
                                   BaseListVideoView landVideoView) {

            mEventType = eventType;
            mOldPortPosition = oldPortPosition;
            mLandVideoBean = landVideoBean;
            mLandVideoView = landVideoView;
        }

        public int getEventType() {
            return mEventType;
        }

        public int getOldPortPosition() {
            return mOldPortPosition;
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
    public void showMoreView() {
        PlayerLog.e(TAG, "showMoreView: ");
        mCurrentLandVideoView.getControllerView().hide();
        MoreDialog dialog = new MoreDialog(this);
        // 更新倍速
        dialog.updateSpeed(getSpeedPos());
        // 更新屏幕缩放
        int screenScale = mCurrentLandVideoView.getIjkVideoView().getCurrentScreenScale();
        dialog.updateScreenScale(screenScale);
        // 设置监听
        dialog.setListener(this);
        dialog.show();
    }



    @Override
    public void onLandShare() {
        ToastUtil.showToast("分享");
    }

    @Override
    public void onLandCollect() {
        ToastUtil.showToast("收藏");
    }

    @Override
    public void onLandCache() {
        ToastUtil.showToast("缓存");
    }

    @Override
    public void onLandNoInterest() {
        ToastUtil.showToast("不感兴趣");
    }

    @Override
    public void onLandJubao() {
        ToastUtil.showToast("举报");
    }

    @Override
    public void onLandPlayBack() {
        ToastUtil.showToast("后台播放");
    }

    @Override
    public void onLandScreenMode(int screenMode) {
       if (mCurrentLandVideoView != null) {
          mCurrentLandVideoView.setScreenScale(screenMode);
       }
    }

    boolean isMirrorEnable = false;
    @Override
    public void onLandMirror() {
        if (mCurrentLandVideoView != null) {
            isMirrorEnable = !isMirrorEnable ;
            mCurrentLandVideoView.setMirrorRotation(isMirrorEnable);
        }
    }

    public int mSpeedPos = 1;

    public int getSpeedPos() {
        return mSpeedPos;
    }

    public void setSpeedPos(int mSpeedPos) {
        this.mSpeedPos = mSpeedPos;
    }

    @Override
    public void onSpeed(int speedPos) {
        setSpeedPos(speedPos);
        mCurrentLandVideoView.getControllerView().setSpeed(speedPos);
    }


    /**
     * 显示分辨率
     */
    public void showResolution() {
        // 暂时没有多个分辨率的资源
        PlayerLog.e(TAG, "showMoreView: ");
        mCurrentLandVideoView.getControllerView().hide();

        ResolutionDialog dialog = new ResolutionDialog(this);
        // 清晰度位置
        int resolutionPos = mCurrentLandVideoView.getControllerView().getResolutionPos();
        dialog.updateResolution(resolutionPos);
        dialog.setListener(this);
        dialog.show();
    }

    @Override
    public void OnResolutionSelected(int pos) {
        mCurrentLandVideoView.getControllerView().setResolution(pos);
    }

    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen && mCurrentLandVideoView != null) {
            // 退出全屏
            MediaController controller = mCurrentLandVideoView.getControllerView();
            if (controller != null) {
                mCurrentLandVideoView.getControllerView().setRequestedOrientation();
            }
            return;
        } else {
            onBack();
        }
    }


    public void onBack(){
        super.onBackPressed();
    }

}
