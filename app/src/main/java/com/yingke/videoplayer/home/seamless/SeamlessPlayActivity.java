package com.yingke.videoplayer.home.seamless;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.util.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseActivity;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.landscape.LandScapeActivity;
import com.yingke.videoplayer.home.util.SinglePlayerManager;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;


/**
 * 无缝播放
 */
public class SeamlessPlayActivity extends LandScapeActivity {

    public static boolean SEAMLESS_PLAYING = false;
    // 视频播放器
    private FrameLayout mVideoContainer;
    private FrameLayout mVideoViewContainer;
    private RelativeLayout mCoverView;
    private SimpleDraweeView mCoverImage;
    private TextView mCoverTitle;
    private ImageView mCoverPlay;

    // 其他信息
    private FrameLayout mInfoContainer;
    private TextView mAuthorName ;
    private TextView mVideoTitle ;

    // 当前播放数据
    private ListVideoData mListVideoData;

    public static void start(Context context, Pair shareElements){
        Intent intent = new Intent(context, SeamlessPlayActivity.class);
        intent.putExtra("transitionName", (String) shareElements.second);
        transitionToActivity((Activity) context, intent, shareElements);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(true);
        setContentView(R.layout.activity_seamless_play);
        initView();
        setupWindowAnimations();
        initData();
    }

    private void initView() {
        mVideoContainer = findViewById(R.id.video_container);
        mVideoViewContainer = findViewById(R.id.video_view_container);
        mCoverView = findViewById(R.id.cover_view);
        mCoverImage = findViewById(R.id.cover_image);
        mCoverTitle = findViewById(R.id.cover_title);
        mCoverPlay = findViewById(R.id.cover_play);

        mInfoContainer = findViewById(R.id.info_container);
        mAuthorName = findViewById(R.id.seamless_author);
        mVideoTitle = findViewById(R.id.seamless_title);
    }

    private void initData() {
        mVideoViewContainer.removeAllViews();
        mCoverView.setVisibility(View.GONE);
        // 添加播放器
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        BaseListVideoView listVideoView = SinglePlayerManager.getInstance().getCurrentListVideoView();
        ViewParent viewParent = listVideoView.getParent();
        if (viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).removeView(listVideoView);
        }
        mVideoViewContainer.addView(listVideoView, 0, params);
        // 保存当前播放数据
        mListVideoData = (ListVideoData) SinglePlayerManager.getInstance().getCurrentVideoBean();

        mAuthorName.setText(mListVideoData.getAuthorName());
        mVideoTitle.setText(mListVideoData.getTitle());
        // 仅重置数据
        SinglePlayerManager.getInstance().resetData();

    }

    private void setupWindowAnimations() {
        if (isUpperV21()) {
            // 设置整个布局View 进入过渡
            Fade fade = new Fade();
            fade.setDuration(300);
            getWindow().setEnterTransition(fade);

            // 设置transitionName
            String transitionName = getIntent().getStringExtra("transitionName");
            mVideoContainer.setTransitionName(transitionName);

            // 设置共享元素 进入过渡
            getWindow().setSharedElementEnterTransition(new ChangeBounds());
            getWindow().setAllowEnterTransitionOverlap(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 添加事件
        boolean isRegistered =  EventBus.getDefault().isRegistered(this);
        if (!isRegistered) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void beforeEnterFullScreen() {
        super.beforeEnterFullScreen();
        mIsFullScreenSingle = false;
    }

    @Override
    protected void afterExitFullScreen() {
        super.afterExitFullScreen();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void doEventMainThread(LandScapeActivity.LandScapeVideoEvent event) {
        PlayerLog.e(TAG, "LandScapeVideoEvent: 横屏视频列表滚动，发送事件");
        // 横屏切竖屏 通信
        if (event == null) {
            return;
        }
        // 事件类型
        int eventType = event.getEventType();
        // 点击的旧位置
        int oldPosition = event.getOldPortPosition();
        // 传过来的播放数据
        IVideoBean landVideoBean = event.getLandVideoBean();
        // 传过来的播放器
        BaseListVideoView landVideoView = event.getLandVideoView();

        PlayerLog.e(TAG, "oldPosition: " + oldPosition);
        PlayerLog.e(TAG, "landVideoBean: " + landVideoBean.getTitle());

        if (eventType == LandScapeActivity.LandScapeVideoEvent.EVENT_EXIT_FULLSCREEN) {
            // 退出全屏
            mVideoViewContainer.removeAllViews();
            mCoverView.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoViewContainer.addView(landVideoView, 0, params);

            // 仅重置数据
            SinglePlayerManager.getInstance().resetData();
            return;
        }

        if (eventType == LandScapeActivity.LandScapeVideoEvent.EVENT_UPDATE_SELECTED) {
            // 更新 其他信息
            mListVideoData = (ListVideoData) landVideoBean;
            mAuthorName.setText(mListVideoData.getAuthorName());
            mVideoTitle.setText(mListVideoData.getTitle());
            return;
        }

    }

    @Override
    public boolean isLightStatusBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBack() {
        SEAMLESS_PLAYING = false;
        showIdleView();
        finishAfterTransition();
    }

    /**
     * 显示空闲页
     */
    public void showIdleView() {
        ListVideoData data = mListVideoData;

        mCoverView.setVisibility(View.VISIBLE);
        // 标题
        mCoverTitle.setText(data.getTitle());
        // 封面图
        String thumbPath = data.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)){
            FrescoUtil.displayImage(mCoverImage, new File(thumbPath));
        } else {
            File thumbFile = FileUtil.getVideoThumbFile(this, data.getUrl(), EncryptUtils.PORT_REC_VIDEO);
            if (thumbFile.exists()) {
                data.setThumbPath(thumbFile.getAbsolutePath());
            }
            FrescoUtil.displayImage(mCoverImage, thumbFile);
        }

        // 停止播放
        if (mVideoViewContainer != null && mVideoViewContainer.getChildCount() != 0) {
            BaseListVideoView videoView = (BaseListVideoView) mVideoViewContainer.getChildAt(0);
            if (videoView != null) {
                videoView.release();
                videoView = null;
            }
//            mVideoViewContainer.removeAllViews();
        }

        // 释放播放器
        SinglePlayerManager.getInstance().releaseVideoPlayer();

    }
}
