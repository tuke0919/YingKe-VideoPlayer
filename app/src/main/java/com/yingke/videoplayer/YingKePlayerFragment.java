package com.yingke.videoplayer;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.controller.MediaController;
import com.yingke.player.java.videoview.IjkVideoView;
import com.yingke.videoplayer.bean.IVideoBean;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/21
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class YingKePlayerFragment extends BasePlayerFragment {

    public static final String TAG = "YingKePlayerFragment";

    // 播放器
    private IjkVideoView mIjkVideoView;
    // 控制器
    private MediaController mMediaController;
    // 播放错误
    private RelativeLayout mPlayerErrorView;
    private TextView mPlayerErrorRetry;
    // 网络提示
    private RelativeLayout mNetTipView;
    private TextView mNetTipContinue;
    private TextView mBindFreeCard;
    // 加载页面
    private RelativeLayout mLoadingView;
    private ImageView mLoadingImage;
    // 播放完成
    private RelativeLayout mRePlayView;
    private TextView mRePlayBtn;

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_video_player;
    }

    @Override
    protected void initView() {
        mIjkVideoView = mRootView.findViewById(R.id.ijk_video_view);
        mMediaController = mRootView.findViewById(R.id.ijk_media_controller);

        mPlayerErrorView = mRootView.findViewById(R.id.player_error_view);
        mPlayerErrorRetry = mRootView.findViewById(R.id.player_error_retry);
        mPlayerErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新加载
            }
        });

        mNetTipView = mRootView.findViewById(R.id.net_tips_view);
        mNetTipContinue = mRootView.findViewById(R.id.player_net_continue);
        mNetTipContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 继续播放
            }
        });
        mBindFreeCard = mRootView.findViewById(R.id.player_net_free_card);
        mBindFreeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 绑定免流卡
            }
        });

        mLoadingView = mRootView.findViewById(R.id.player_loading_view);
        mLoadingImage = mRootView.findViewById(R.id.player_loading);

        mRePlayView = mRootView.findViewById(R.id.player_replay_view);
        mRePlayBtn = mRootView.findViewById(R.id.player_replay);
        mRePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重播
            }
        });
    }

    @Override
    protected void initData() {
        initControllerView();
        initVideoPlayerView();
    }

    public void setVideoOnline(IVideoBean videoBean){


    }


    @Override
    protected void doPlayVideo() {

    }


    @Override
    protected BaseMediaController getControllerView() {
        return mMediaController;
    }

    @Override
    protected IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }

    /**
     * 显示 播放中页面
     */
    @Override
    public void showPlayingView(){
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 显示 加载中 界面
     */
    @Override
    public void showLoadingView() {
        mMediaController.setVisibility(View.GONE);
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(300);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        mLoadingImage.startAnimation(rotate);
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 显示 错误 界面
     */
    @Override
    public void showErrorView(){
        mMediaController.setVisibility(View.GONE);
        mPlayerErrorView.setVisibility(View.VISIBLE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 显示播放完成界面
     */
    @Override
    public void showCompletionView(){
        mMediaController.setVisibility(View.GONE);
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInnerError() {

    }

    @Override
    public void onInnerPreparing() {

    }

    @Override
    public void onInnerPrepared() {

    }

    @Override
    public void onInnerPlaying() {

    }

    @Override
    public void onInnerPaused() {

    }

    @Override
    public void onInnerPlayCompletion() {

    }

    @Override
    public void onInnerBuffering() {

    }

    @Override
    public void onInnerBuffered() {

    }
}
