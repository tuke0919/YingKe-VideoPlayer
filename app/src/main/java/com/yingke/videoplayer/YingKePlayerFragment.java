package com.yingke.videoplayer;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.controller.MediaController;
import com.yingke.player.java.videoview.IjkVideoView;

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

    public static YingKePlayerFragment newInstance(){
        return new YingKePlayerFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_video_player;
    }

    @Override
    protected void initView(View rootView) {
        mIjkVideoView = rootView.findViewById(R.id.ijk_video_view);
        mMediaController = rootView.findViewById(R.id.ijk_media_controller);

        mPlayerErrorView = rootView.findViewById(R.id.player_error_view);
        mPlayerErrorRetry = rootView.findViewById(R.id.player_error_retry);
        mPlayerErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新加载
            }
        });

        mNetTipView = rootView.findViewById(R.id.net_tips_view);
        mNetTipContinue = rootView.findViewById(R.id.player_net_continue);
        mNetTipContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 继续播放
            }
        });
        mBindFreeCard = rootView.findViewById(R.id.player_net_free_card);
        mBindFreeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 绑定免流卡
            }
        });

        mLoadingView = rootView.findViewById(R.id.player_loading_view);
        mLoadingImage = rootView.findViewById(R.id.player_loading);

        mRePlayView = rootView.findViewById(R.id.player_replay_view);
        mRePlayBtn = rootView.findViewById(R.id.player_replay);
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

    /**
     * 刚开时 设置数据
     * @param videoBean
     */
    public void setVideoOnline(IVideoBean videoBean){
        if (videoBean == null) {
            return;
        }
        if (checkNetWork()) {
            mIjkVideoView.setVideoBean(videoBean);
        }
    }


    @Override
    protected BaseMediaController getControllerView() {
        return mMediaController;
    }

    @Override
    protected IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }

    @Override
    public void showPlayingView(){
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

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

    @Override
    public void showErrorView(){
        mMediaController.setVisibility(View.GONE);
        mPlayerErrorView.setVisibility(View.VISIBLE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

    @Override
    public void showCompletionView(){
        mMediaController.setVisibility(View.GONE);
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示网络提示界面
     */
    @Override
    public void showNetTipView(){
        mMediaController.setVisibility(View.GONE);
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

}
