package com.yingke.player.java.videoview;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;

import com.yingke.player.java.controller.MediaPlayerControl;
import com.yingke.player.java.manager.MediaPlayerManager;
import com.yingke.player.java.listener.OnVideoViewStateChangeListener;
import com.yingke.player.java.listener.PlayerEventListener;
import com.yingke.player.java.manager.ProgressManager;
import com.yingke.player.java.R;
import com.yingke.player.java.player.AbstractPlayer;
import com.yingke.player.java.player.IjkPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class IjkBaseVideoView extends FrameLayout implements MediaPlayerControl, PlayerEventListener {

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING= 6;
    public static final int STATE_BUFFERED = 7;

    public static final int SCREEN_MODE_NORMAL = 10; // 普通播放器
    public static final int SCREEN_MODE_FULL = 11;   // 全屏播放器
    public static final int SCREEN_MODE_TINY = 12;   // 小屏播放器

    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 2;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 3;

    // 真实播放器接口
    protected AbstractPlayer mMediaPlayer;
    protected AbstractPlayer mTempMediaPlayer;

    // 当前播放状态
    protected int mCurrentPlayState = STATE_IDLE;
    // 当前屏幕模式
    protected int mCurrentScreenMode = SCREEN_MODE_NORMAL;
    // 当前屏幕方向
    protected int mCurrentOrientation = 0;

    // 音频管理器
    protected AudioManager mAudioManager;
    // 音频焦点管理
    protected AudioFocusHelper mAudioFocusHelper;
    // 是否静音
    protected boolean mIsMute;

    // 当前 视频Url
    protected String mCurrentUrl;
    // 当前视频地址的请求头
    protected Map<String, String> mHeaders;
    // assets文件
    protected AssetFileDescriptor mAssetFileDescriptor;

    // 进度管理器
    protected ProgressManager mProgressManager;
    // 当前正在播放视频的位置
    protected long mCurrentPosition;

    // 是否锁定屏幕
    protected boolean mIsLockFullScreen;

    // 播放器状态改变监听器
    protected List<OnVideoViewStateChangeListener> mOnVideoViewStateChangeListeners;

    // 是否自动旋转
    protected boolean mAutoRotate;
    // 是否使用 SurfaceView
    protected boolean mUsingSurfaceView;
    // 是否循环播放
    protected boolean mIsLooping;
    // 是否使能音频焦点
    protected boolean mEnableAudioFocus;
    // 是否使用硬解码
    protected boolean mEnableMediaCodec;

    protected boolean mAddToVideoViewManager;


    public IjkBaseVideoView(Context context) {
        super(context);
    }

    public IjkBaseVideoView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public IjkBaseVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IjkBaseVideoView);
        mAutoRotate = typedArray.getBoolean(R.styleable.IjkBaseVideoView_autoRotate, false);
        mUsingSurfaceView = typedArray.getBoolean(R.styleable.IjkBaseVideoView_usingSurfaceView, false);
        mIsLooping = typedArray.getBoolean(R.styleable.IjkBaseVideoView_looping, false);
        mEnableAudioFocus = typedArray.getBoolean(R.styleable.IjkBaseVideoView_enableAudioFocus, true);
        mEnableMediaCodec = typedArray.getBoolean(R.styleable.IjkBaseVideoView_enableMediaCodec, false);
        typedArray.recycle();
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if (mCurrentPlayState == STATE_IDLE) {
            startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
        }
        setKeepScreenOn(true);
        if (mAudioFocusHelper != null){
            mAudioFocusHelper.requestFocus();
        }
    }

    /**
     * 新开始 播放
     */
    public void startPlay() {
        if (mAddToVideoViewManager) {
            MediaPlayerManager.instance().releaseVideoPlayer();
            MediaPlayerManager.instance().setIjkBaseVideoView(this);
        }

        // 音频焦点
        if (mEnableAudioFocus) {
            mAudioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioFocusHelper = new AudioFocusHelper();
        }
        // 当前进度
        if (mProgressManager != null) {
            mCurrentPosition = mProgressManager.getSavedProgress(mCurrentUrl);
        }

        // 自动旋转
        if (mAutoRotate) {
            mOrientationEventListener.enable();
        }

        initPlayer();
        startPrepare(false);
    }

    /**
     * 初始化播放器
     */
    public void initPlayer() {
        if (mTempMediaPlayer != null) {
            mMediaPlayer = mTempMediaPlayer;
        } else {
            mMediaPlayer = new IjkPlayer(getContext());
        }
        mMediaPlayer.initPlayer();
        mMediaPlayer.setEnableMediaCodec(mEnableMediaCodec);
        mMediaPlayer.setLooping(mIsLooping);
        mMediaPlayer.setPlayerEventListener(this);
    }

    /**
     * 开始准备播放（直接播放）
     * @param needReset
     */
    protected void startPrepare(boolean needReset) {
        if (TextUtils.isEmpty(mCurrentUrl) && mAssetFileDescriptor == null) {
            return;
        }
        if (needReset) {
            mMediaPlayer.reset();
        }
        if (mAssetFileDescriptor != null) {
            mMediaPlayer.setDataSource(mAssetFileDescriptor);
        } else {
            mMediaPlayer.setDataSource(mCurrentUrl, mHeaders);
        }
        // 准备播放
        mMediaPlayer.prepareAsync();
        notifyPlayStateChanged(STATE_PREPARING);
        notifyScreenModeChanged(isFullScreen() ? SCREEN_MODE_FULL : isTinyScreen() ? SCREEN_MODE_TINY : SCREEN_MODE_NORMAL);
    }

    /**
     * 通知播放状态改变
     * @param playState
     */
    public void notifyPlayStateChanged(int playState){
        mCurrentPlayState = playState;

        for (OnVideoViewStateChangeListener listener: mOnVideoViewStateChangeListeners) {
            if (listener != null) {
                listener.onPlayStateChanged(playState);
            }
        }
    }

    /**
     * 通知屏幕模式改变
     * @param screenMode
     */
    public void notifyScreenModeChanged(int screenMode) {
        mCurrentScreenMode = screenMode;

        for (OnVideoViewStateChangeListener listener: mOnVideoViewStateChangeListeners) {
            if (listener != null) {
                listener.onScreenModeChanged(screenMode);
            }
        }
    }

    /**
     * 播放状态下开始播放
     */
    protected void startInPlaybackState() {
        mMediaPlayer.start();
        notifyPlayStateChanged(STATE_PLAYING);
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isInPlaybackState() {
        return (mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED);
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
            notifyPlayStateChanged(STATE_PAUSED);
            setKeepScreenOn(false);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.abandonFocus();
        }
    }

    /**
     * 继续播放
     */
    @Override
    public void resume() {
        if (isInPlaybackState() && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            notifyPlayStateChanged(STATE_PLAYING);
            if (mAudioFocusHelper != null){
                mAudioFocusHelper.requestFocus();
            }
            setKeepScreenOn(true);
        }
    }

    /**
     * 停止播放, 不同于暂停，不交给控制器
     */
    public void stopPlayback() {
        if (mProgressManager != null && isInPlaybackState()){
            mProgressManager.saveProgress(mCurrentUrl, mCurrentPosition);
        }

        if (mMediaPlayer != null) {
            // 停止
            mMediaPlayer.stop();
            notifyPlayStateChanged(STATE_IDLE);
            if (mAudioFocusHelper != null){
                mAudioFocusHelper.abandonFocus();
            }
            setKeepScreenOn(false);
        }
        onPlayStopped();
    }

    /**
     * 释放播放器 不交给控制器
     */
    public void release() {
        if (mProgressManager != null && isInPlaybackState()){
            mProgressManager.saveProgress(mCurrentUrl, mCurrentPosition);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            notifyPlayStateChanged(STATE_IDLE);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.abandonFocus();
            setKeepScreenOn(false);
        }
        onPlayStopped();
    }

    /**
     *
     */
    private void onPlayStopped() {
        mOrientationEventListener.disable();
        mIsLockFullScreen = false;
        mCurrentPosition = 0;
    }

    /**
     * 添加 监听播放状态变化
     */
    public void addOnVideoViewStateChangeListener(OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners == null) {
            mOnVideoViewStateChangeListeners = new ArrayList<>();
        }
        mOnVideoViewStateChangeListeners.add(listener);
    }

    /**
     * 移除播放状态监听
     */
    public void removeOnVideoViewStateChangeListener(OnVideoViewStateChangeListener listener) {
        if (mOnVideoViewStateChangeListeners != null) {
            mOnVideoViewStateChangeListeners.remove(listener);
        }
    }

    /**
     * 移除所有播放状态监听
     */
    public void clearOnVideoViewStateChangeListeners() {
        if (mOnVideoViewStateChangeListeners != null) {
            mOnVideoViewStateChangeListeners.clear();
        }
    }


    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    /**
     * 调整播放进度
     */
    @Override
    public void seekTo(long pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercent() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        if (mMediaPlayer != null) {
            this.mIsMute = isMute;
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 是否处于静音状态
     */
    @Override
    public boolean isMute() {
        return mIsMute;
    }

    /**
     * 设置controller是否处于锁定状态
     */
    @Override
    public void setLock(boolean isLocked) {
        this.mIsLockFullScreen = isLocked;
    }


    /**
     * 获取缓冲速度
     */
    @Override
    public long getTcpSpeed() {
        return mMediaPlayer.getTcpSpeed();
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        if (isInPlaybackState()) {
            mMediaPlayer.setSpeed(speed);
        }
    }


    /**
     * 视频播放器内部 信息 回调
     * @param what
     * @param extra
     */
    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_BUFFERING_START:
                notifyPlayStateChanged(STATE_BUFFERING);
                break;
            case AbstractPlayer.MEDIA_INFO_BUFFERING_END:
                notifyPlayStateChanged(STATE_BUFFERED);
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                // 视频开始渲染
                notifyPlayStateChanged(STATE_PLAYING);
                if (getWindowVisibility() != VISIBLE) {
                    pause();
                }
                break;
        }
    }

    /**
     * 视频播放器内部 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared() {
        notifyPlayStateChanged(STATE_PREPARED);
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
    }

    /**
     * 视频播放器内部 播放出错回调
     */
    @Override
    public void onError(String message) {
        notifyPlayStateChanged(STATE_ERROR);
    }

    /**
     * 视频播放器内部 播放完成回调
     */
    @Override
    public void onCompletion() {
        notifyPlayStateChanged(STATE_PLAYBACK_COMPLETED);
        setKeepScreenOn(false);
        mCurrentPosition = 0;
    }


    /**
     * 获取 当前播放状态
     * @return
     */
    public int getCurrentPlayState() {
        return mCurrentPlayState;
    }

    /**
     * 获取当前 屏幕状态
     * @return
     */
    public int getCurrentScreenMode() {
        return mCurrentScreenMode;
    }

    /**
     * 设置视频地址
     */
    public void setUrl(String url) {
        this.mCurrentUrl = url;
    }

    /**
     * 设置包含请求头信息的视频地址
     *
     * @param url     视频地址
     * @param headers 请求头
     */
    public void setUrl(String url, Map<String, String> headers) {
        mCurrentUrl = url;
        mHeaders = headers;
    }

    /**
     * 用于播放assets里面的视频文件
     */
    public void setAssetFileDescriptor(AssetFileDescriptor fd) {
        this.mAssetFileDescriptor = fd;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public void skipPositionWhenPlay(int position) {
        this.mCurrentPosition = position;
    }

    /**
     * 设置音量 0.0f-1.0f 之间
     *
     * @param v1 左声道音量
     * @param v2 右声道音量
     */
    public void setVolume(float v1, float v2) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
        }
    }

    /**
     * 设置进度管理器，用于保存播放进度
     */
    public void setProgressManager(ProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    /**
     * 循环播放， 默认不循环播放
     */
    public void setLooping(boolean looping) {
        mIsLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    /**
     * 是否自动旋转， 默认不自动旋转
     */
    public void setAutoRotate(boolean autoRotate) {
        mAutoRotate = autoRotate;
    }

    /**
     * 是否启用SurfaceView，默认不启用
     */
    public void setUsingSurfaceView(boolean usingSurfaceView) {
        mUsingSurfaceView = usingSurfaceView;
    }

    /**
     * 是否开启AudioFocus监听， 默认开启
     */
    public void setEnableAudioFocus(boolean enableAudioFocus) {
        mEnableAudioFocus = enableAudioFocus;
    }

    /**
     * 是否使用MediaCodec进行解码（硬解码），默认不开启，使用软解
     */
    public void setEnableMediaCodec(boolean enableMediaCodec) {
        mEnableMediaCodec = enableMediaCodec;
    }

    /**
     * 添加到{@link MediaPlayerManager}
     * 如需集成到RecyclerView或ListView请开启此选项，用于实现同一列表同时只播放一个视频
     */
    public void addToVideoViewManager() {
        mAddToVideoViewManager = true;
    }

    /**
     * 自定义播放核心，继承{@link AbstractPlayer}实现自己的播放核心
     */
    public void setCustomMediaPlayer(AbstractPlayer abstractPlayer) {
        mTempMediaPlayer = abstractPlayer;
    }

    /**
     * 音频焦点管理
     */
    public class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener{
        private boolean startRequested = false;
        private boolean pausedForLoss = false;
        private int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }
            currentFocus = focusChange;
            switch (currentFocus) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    //暂时获得焦点
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    if (mMediaPlayer != null && !mIsMute)//恢复音量
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // 失去焦点
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //此时需降低音量
                    if (mMediaPlayer != null && isPlaying() && !mIsMute) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        /**
         * 请求焦点
         */
        public void requestFocus(){
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return;
            }
            if (mAudioManager == null) {
                return;
            }
            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (status == mAudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return;
            }
            startRequested = true;
        }

        /**
         * 放弃音频焦点
         */
        public void abandonFocus(){
            if (mAudioManager == null) {
                return;
            }

            startRequested = false;
            mAudioManager.abandonAudioFocus(this);
        }
    }

    protected OrientationEventListener mOrientationEventListener = new OrientationEventListener(getContext()){
        @Override
        public void onOrientationChanged(int orientation) {

        }
    };


}
