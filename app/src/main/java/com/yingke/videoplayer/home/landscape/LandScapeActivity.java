package com.yingke.videoplayer.home.landscape;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.base.BaseActivity;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.util.SinglePlayerManager;
import com.yingke.videoplayer.tiktok.PagerLayoutManager;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.StringUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

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

    // 视频父容器
    private ViewParent mPlayerParent;
    // 当前横屏的播放器
    private BaseListVideoView mCurrentLandVideoView;

    // 列表
    private RecyclerView mLandRecyclerView;
    // 适配器
    private LandListVideoAdapter mLandListVideoAdapter;


    private void initRecyclerView() {
        PlayerLog.e(TAG, "initRecyclerView: ");
        mLandRecyclerView = new RecyclerView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLandRecyclerView.setLayoutParams(params);

        PagerLayoutManager layoutManager = new PagerLayoutManager(this, OrientationHelper.VERTICAL);
        mLandRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setListener(this);

        mLandListVideoAdapter = new LandListVideoAdapter(this);
        mLandRecyclerView.setAdapter(mLandListVideoAdapter);
    }

    private void initDatas(){
        PlayerLog.e(TAG, "initDatas: ");
        // 模拟网络数据
        String videoListJson = StringUtil.getJsonData(this, "list_rec_video.json");
        if (!TextUtils.isEmpty(videoListJson)) {
            List<ListVideoData> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListVideoData>>() {}.getType());
            for (ListVideoData data : listVideoData) {
                // 设置首播类型
                data.setCurrentType(data.getFirstType());

                File thumbFile = FileUtil.getVideoThumbFile(this, EncryptUtils.md5String(data.getUrl()));
                if (thumbFile.exists()) {
                    data.setThumbPath(thumbFile.getAbsolutePath());
                }

                File thumbAdFile = FileUtil.getVideoThumbFile(this, EncryptUtils.md5String(data.getAdUrl()));
                if (thumbAdFile.exists()) {
                    data.setAdThumbPath(thumbAdFile.getAbsolutePath());
                }
            }
            mLandListVideoAdapter.addAllDatas(listVideoData);
        }
    }


    /**
     * 进入横屏
     * @param baseListVideoView 当前播放器
     */
    public void enterFullScreen(BaseListVideoView baseListVideoView) {
        PlayerLog.e(TAG, "enterFullScreen: ");

        // 禁止画中画
        SinglePlayerManager.getInstance().enablePip(false);


        mCurrentLandVideoView = baseListVideoView;
        mPlayerParent = baseListVideoView.getParent();

        // 移除竖屏播放器
        SinglePlayerManager.getInstance().removePlayerNotRelease();


        ViewGroup contentView = findViewById(android.R.id.content);
        initRecyclerView();
        initDatas();
        contentView.addView(mLandRecyclerView);

        // 更新成当前列表
        SinglePlayerManager.getInstance().attachRecycleView(mLandRecyclerView);

//
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//
//        mPlayerParent = mCurrentLandVideoView.getParent();
//        if (mPlayerParent instanceof ViewGroup) {
//            ((ViewGroup) mPlayerParent).removeView(mCurrentLandVideoView);
//        }
//        contentView.addView(mCurrentLandVideoView, params);
    }

    /**
     * 进入竖屏
     */
    public void exitFullScreen() {
        PlayerLog.e(TAG, "enterFullScreen: ");
        if (mCurrentLandVideoView != null) {

        }


//        if (mPlayerParent instanceof ViewGroup) {
//            ViewGroup contentView = findViewById(android.R.id.content);
//            contentView.removeView(baseListVideoView);
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//
//            ((ViewGroup) mPlayerParent).addView(baseListVideoView, params);
//        }
    }


    @Override
    public void onFirstPageAttached(View itemView) {
        PlayerLog.e(TAG, "onFirstPageAttached: position = " + 0);
        LandListVideoVH landListVideoVH = getLandListVideoVH(itemView);
        landListVideoVH.attachVideoPlayer(mCurrentLandVideoView);

    }

    @Override
    public void onPageSelected(int position, View itemView, boolean isBottom) {
        PlayerLog.e(TAG, "onPageSelected: position = " + position);
    }

    @Override
    public void onPageDetached(boolean isNext, int position, View itemView) {
        PlayerLog.e(TAG, "onPageDetached: position = " + position);
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



}
