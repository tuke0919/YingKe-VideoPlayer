package com.yingke.videoplayer;

import android.widget.Toast;

import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.listener.OnPlayStateListener;
import com.yingke.player.java.videoview.IjkVideoView;
import com.yingke.videoplayer.base.BaseFragment;
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
public abstract class BasePlayerFragment extends BaseFragment implements OnPlayStateListener {


    protected abstract BaseMediaController getControllerView();

    protected abstract IjkVideoView getIjkVideoView();

    protected void initVideoPlayerView(){
        getIjkVideoView().setMediaController(getControllerView());
        getIjkVideoView().addPlayStateListener(this);
    }

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

    public abstract void onInnerError();
    public abstract void onInnerPreparing();
    public abstract void onInnerPrepared();
    public abstract void onInnerPlaying();
    public abstract void onInnerPaused();
    public abstract void onInnerPlayCompletion();
    public abstract void onInnerBuffering();
    public abstract void onInnerBuffered();

    /**
     * 检查网络 并播放
     */
    protected void checkNetWorkAndPlay(){
        if (checkNetWork()) {
            showPlayingView();
            doPlayVideo();
        }
    }

    /**
     * 检查网络
     * @return
     */
    protected boolean checkNetWork(){
        if (!NetUtils.isAvailable(getContext())) {
            return false;
        }
        if (NetUtils.isMobileNetwork()) {
            if (PlayerSetting.isMobilePlayAllowed()) {
                Toast.makeText(getContext(), R.string.vdetail_2g3g_tip, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    protected abstract void showPlayingView();

    protected abstract void showErrorView();

    protected abstract void showCompletionView();

    protected abstract void showLoadingView();

    protected abstract void doPlayVideo();











}
