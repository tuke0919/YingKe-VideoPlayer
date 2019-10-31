package com.yingke.videoplayer.widget;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.PlayerUtils;
import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.controller.MediaController;
import com.yingke.player.java.controller.MediaPlayerControl;
import com.yingke.player.java.listener.OnPlayStateListener;
import com.yingke.player.java.videoview.IjkVideoView;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.landscape.LandScapeActivity;
import com.yingke.videoplayer.home.util.SinglePlayerManager;
import com.yingke.videoplayer.util.DeviceUtil;
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
import static com.yingke.player.java.videoview.IjkBaseVideoView.TAG;

/**
 * 功能：业务相关的 播放器控件
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-09
 */
public abstract class BaseListVideoView extends FrameLayout implements OnPlayStateListener, MediaPlayerControl {

    // 播放器
    protected IjkVideoView mIjkVideoView;
    // 控制器
    protected MediaController mMediaController;
    // 父容器
    protected ViewParent mPlayerParent;

    // 单个视屏全屏
    protected boolean mIsFullScreenSingle = false;

    public BaseListVideoView(@NonNull Context context) {
        super(context);
    }

    public BaseListVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseListVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * @return 控制器
     */
    public abstract MediaController getControllerView();

    /**
     * @return 播放器
     */
    public abstract IjkVideoView getIjkVideoView();

    /**
     * 初始化 控制器
     */
    protected void initControllerView(){
        if (!hasController()) {
            return;
        }
        getControllerView().setFullScreenListener(new BaseMediaController.OnFullScreenListener() {
            @Override
            public void onEnterFullScreen() {
                // 控制器回调的 进入全屏
                enterFullScreen();
            }

            @Override
            public void onExitFullScreen() {
                // 控制器回调的 退出全屏
                exitFullScreen();
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

    /**
     * 初始化 播放器
     */
    protected void initVideoPlayerView(){
        if (hasController()) {
            getIjkVideoView().setMediaController(getControllerView());
        }
        getIjkVideoView().addPlayStateListener(this);
    }

    /**
     * 设置播放器初始化时全屏状态 仅用在初始化
     * @param isFullScreen
     */
    public void setFullScreenStatus(boolean isFullScreen){
        if (getControllerView() != null) {
            getControllerView().setFullScreenStatus(isFullScreen);
        }
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
                onInnerIdle();
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

    public void onInnerIdle(){
        showIdleView();
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
     * 显示 空闲页面,
     */
    protected abstract void showIdleView();

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
     * @return 是否有控制器
     */
    protected boolean hasController() {
        return true;
    }

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

    /**
     * 刚开时 设置数据
     * @param videoBean
     */
    public void setVideoOnline(IVideoBean videoBean){
        if (videoBean == null || getIjkVideoView() == null) {
            return;
        }

        if (checkNetWork()) {
            beforeSetVideoOnline(videoBean);
            getIjkVideoView().setVideoBean(videoBean);

        }
    }

    /**
     * 设置链接之前
     * @param videoBean
     */
    public void beforeSetVideoOnline(IVideoBean videoBean){

    }




    @Override
    public void start() {
        if (mIjkVideoView != null) {
            mIjkVideoView.start();
        }
    }

    @Override
    public void pause() {
        if (mIjkVideoView != null) {
            mIjkVideoView.pause();
        }
    }

    @Override
    public void stopPlayback() {
        if (mIjkVideoView != null) {
            mIjkVideoView.stopPlayback();
        }
    }

    @Override
    public void release() {
        if (mIjkVideoView != null) {
            mIjkVideoView.release();
        }
    }

    @Override
    public long getDuration() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(long pos) {
        if (mIjkVideoView != null) {
            mIjkVideoView.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isInPlaybackState() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.isInPlaybackState();
        }
        return false;
    }

    @Override
    public int getBufferedPercentage() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getBufferedPercentage();
        }
        return 0;
    }

    @Override
    public void setMute(boolean isMute) {
        if (mIjkVideoView != null) {
            mIjkVideoView.setMute(isMute);
        }
    }

    @Override
    public boolean isMute() {
        if (mIjkVideoView != null) {
            mIjkVideoView.isMute();
        }
        return false;
    }

    @Override
    public void setLock(boolean isLocked) {
        if (mIjkVideoView != null) {
            mIjkVideoView.setLock(isLocked);
        }
    }

    @Override
    public void setScreenScale(int screenScale) {
        if (mIjkVideoView != null) {
            mIjkVideoView.setScreenScale(screenScale);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (mIjkVideoView != null) {
            mIjkVideoView.setSpeed(speed);
        }
    }

    @Override
    public long getTcpSpeed() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getTcpSpeed();
        }
        return 0;
    }

    @Override
    public void replay(boolean resetPosition) {
        if (mIjkVideoView != null) {
            mIjkVideoView.replay(resetPosition);
        }
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        if (mIjkVideoView != null) {
            mIjkVideoView.setMirrorRotation(enable);
        }
    }

    @Override
    public Bitmap doScreenShot() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.doScreenShot();
        }
        return null;
    }

    @Override
    public int[] getVideoSize() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.getVideoSize();
        }
        return new int[0];
    }

    /**
     * 进入全屏
     */
    public void enterFullScreen() {
        PlayerLog.d("ListVideo", "enterFullScreen: ");
        if (mIsFullScreenSingle) {
            enterFullScreenForSingle();
        } else {
            enterFullScreenForList();
        }

    }

    /**
     * 进入全屏 仅单个视频
     */
    public void enterFullScreenForSingle() {
        ViewGroup contentView = getActivity().findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mPlayerParent = getParent();
        if (mPlayerParent instanceof ViewGroup) {
            ((ViewGroup) mPlayerParent).removeView(this);
        }
        contentView.addView(this, params);
    }

    /**
     * 进入全屏 横屏视频列表
     */
    public void enterFullScreenForList() {
        if (getActivity() instanceof LandScapeActivity) {
            ((LandScapeActivity) getActivity()).enterFullScreen(this);
        }
    }


    /**
     * 退出全屏
     */
    public void exitFullScreen() {
        PlayerLog.d("ListVideo", "exitFullScreen: ");
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
            ViewGroup contentView = getActivity().findViewById(android.R.id.content);
            contentView.removeView(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            ((ViewGroup) mPlayerParent).addView(this, params);
        }
    }

    /**
     * 退出全屏 横屏视频列表
     */
    public void exitFullScreenForList() {
        if (getActivity() instanceof LandScapeActivity) {
            ((LandScapeActivity) getActivity()).exitFullScreen();
        }
    }

    /**
     * @return
     */
    public boolean isFullScreen() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.isFullScreen();
        }
        return false;
    }

    @Override
    public void startTinyScreen() {
        // 业务相关的小屏

        if (mIjkVideoView == null) {
            return;
        }

        if (mIjkVideoView.isTinyScreen()) {
            return;
        }

        mIjkVideoView.setTinyScreen(true);

        // contentView添加
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null){
            PlayerLog.d(TAG,"controller attached activity is null");
            return;
        }

        // 实际是添加到contentView上
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        int width = PlayerUtils.getScreenWidth(getContext()) / 2;
        int height = width * 9 / 16;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.TOP | Gravity.END;
        params.topMargin = DeviceUtil.getStatusBarHeight(getContext());
        // 添加到ContentView
        contentView.addView(this, params);
    }

    @Override
    public void stopTinyScreen() {
        if (mIjkVideoView == null) {
            return;
        }

        if (!mIjkVideoView.isTinyScreen()) {
            return;
        }

        mIjkVideoView.setTinyScreen(false);

        // contentView 移除
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null){
            return;
        }
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        // 移除
        contentView.removeView(this);
    }

    @Override
    public boolean isTinyScreen() {
        if (mIjkVideoView != null) {
            return mIjkVideoView.isTinyScreen();
        }
        return false;
    }

    public Activity getActivity() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null){
            throw new IllegalArgumentException("BaseListVideoView attached activity is null");
        }
        return activity;
    }
}
