package com.yingke.videoplayer.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.listener.OnPlayStateListener;
import com.yingke.player.java.videoview.IjkVideoView;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.util.NetUtils;
import com.yingke.videoplayer.util.PlayerSetting;

import static com.yingke.player.java.videoview.IjkBaseVideoView.SCREEN_MODE_FULL;
import static com.yingke.player.java.videoview.IjkBaseVideoView.SCREEN_MODE_NORMAL;
import static com.yingke.player.java.videoview.IjkBaseVideoView.SCREEN_MODE_TINY;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_BUFFERED;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_BUFFERING;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_ERROR;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_IDLE;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_PAUSED;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_PLAYBACK_COMPLETED;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_PLAYING;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_PREPARED;
import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_PREPARING;

/**
 * 功能：业务相关的 播放器控件
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-09
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class BaseListVideoView extends FrameLayout implements OnPlayStateListener {

    public BaseListVideoView(@NonNull Context context) {
        super(context);
    }

    public BaseListVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseListVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected abstract BaseMediaController getControllerView();

    protected abstract IjkVideoView getIjkVideoView();

    /**
     * 初始化 播放器
     */
    protected void initVideoPlayerView(){
        getIjkVideoView().setMediaController(getControllerView());
        getIjkVideoView().addPlayStateListener(this);
    }

    /**
     * 初始化 控制器
     */
    protected void initControllerView(){
        getControllerView().setFullScreenListener(new BaseMediaController.OnFullScreenListener() {
            @Override
            public void onEnterFullScreen() {

            }

            @Override
            public void onExitFullScreen() {

            }
        });
        getControllerView().setOnShareListener(new BaseMediaController.onShareListener() {
            @Override
            public void onShare() {

            }
        });
        getControllerView().setControllerShownHidedListener(new BaseMediaController.OnShownHiddenListener() {
            @Override
            public void onShown() {

            }

            @Override
            public void onHidden() {

            }
        });
    }



    @Override
    public void onScreenModeChanged(int screenMode) {
        switch (screenMode) {
            case SCREEN_MODE_TINY:
                break;
            case SCREEN_MODE_NORMAL:
                break;
            case SCREEN_MODE_FULL:
                break;
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case  STATE_ERROR :
                onInnerError();
                break;
            case  STATE_IDLE :
                break;
            case  STATE_PREPARING:
                onInnerPreparing();
                break;
            case  STATE_PREPARED :
                onInnerPrepared();
                break;
            case  STATE_PLAYING :
                onInnerPlaying();
                break;
            case  STATE_PAUSED :
                onInnerPaused();
                break;
            case  STATE_PLAYBACK_COMPLETED :
                onInnerPlayCompletion();
                break;
            case  STATE_BUFFERING :
                onInnerBuffering();
                break;
            case  STATE_BUFFERED :
                onInnerBuffered();
                break;
            default:
                return ;
        }
    }

    public void onInnerError(){
        showErrorView();
    }

    public void onInnerPreparing(){
        showLoadingView();
    }

    public void onInnerPrepared(){
        showPlayingView();
    }

    public void onInnerPlaying(){
        showPlayingView();
    }

    public void onInnerPaused(){

    }

    public void onInnerPlayCompletion(){
        showCompletionView();
    }

    public void onInnerBuffering(){

    }

    public void onInnerBuffered(){

    }


    /**
     * 显示 播放中页面
     */
    protected abstract void showPlayingView();

    /**
     * 显示 错误 界面，内部错误或者网络错误
     */
    protected abstract void showErrorView();

    /**
     * 显示播放完成界面
     */
    protected abstract void showCompletionView();

    /**
     * 显示 加载中 界面
     */
    protected abstract void showLoadingView();

    /**
     * 显示网络提示界面
     */
    protected abstract void showNetTipView();

    /**
     * 检查网络
     * @return
     */
    protected boolean checkNetWork(){
        if (!NetUtils.isAvailable(getContext())) {
            showErrorView();
            return false;
        }

        if (NetUtils.isMobileNetwork()) {
            if (PlayerSetting.is4GPlayAllowed()) {
                Toast.makeText(getContext(), R.string.vdetail_2g3g_tip, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                showNetTipView();
                return false;
            }
        }
        return true;
    }








}
