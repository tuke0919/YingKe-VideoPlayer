package com.yingke.player.java.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.PlayerUtils;
import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.controller.MediaPlayerControl;
import com.yingke.player.java.manager.MediaPlayerManager;
import com.yingke.player.java.listener.OnPlayStateListener;
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

    public static final String TAG = "IjkBaseVideoView";

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;


    public static final int SCREEN_MODE_NORMAL = 10; // 普通播放器
    public static final int SCREEN_MODE_FULL = 11;   // 全屏播放器
    public static final int SCREEN_MODE_TINY = 12;   // 小屏播放器

    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 2;
    public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 3;

    // 真实播放器接口
    protected AbstractPlayer mMediaPlayer;
    protected AbstractPlayer mTempMediaPlayer;
    // 控制器
    protected BaseMediaController mBaseMediaController;

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
    protected List<OnPlayStateListener> mOnPlayStateListeners;

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

    // 已经准备
    protected boolean mIsPrepared;

    // 视频数据
    protected IVideoBean mVideoBean;


    public IjkBaseVideoView(Context context) {
        this(context, null);
    }

    public IjkBaseVideoView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
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
     * 打开播放器 指第一次
     */
    public void openVideo() {
        PlayerLog.d(TAG, "openVideo : "  );

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        notifyPlayStateChanged(STATE_IDLE);

        initAndPreparePlayer();
    }

    /**
     * 初始化并准备 播放
     */
    public void initAndPreparePlayer() {
        PlayerLog.d(TAG, "initAndPreparePlayer : "  );

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
        } else {
            mOrientationEventListener.disable();
        }

        initPlayer();
        addDisplay();
        preparePlayer(false);
    }

    /**
     * 初始化播放器，实例化，设置监听回调等
     */
    public void initPlayer() {
        PlayerLog.d(TAG, "initPlayer : "  );

        if (mTempMediaPlayer != null) {
            mMediaPlayer = mTempMediaPlayer;
        } else {
            mMediaPlayer = new IjkPlayer(getContext());
        }
        mMediaPlayer.initPlayer();
        mIsPrepared = false;
        mMediaPlayer.setEnableMediaCodec(mEnableMediaCodec);
        mMediaPlayer.setLooping(mIsLooping);
        mMediaPlayer.setPlayerEventListener(this);
    }

    /**
     * 添加 显示控件
     */
    public void addDisplay() {
        PlayerLog.d(TAG, "addDisplay : "  );
    }

    /**
     * 开始准备播放，设置数据资源等
     * @param needReset
     */
    protected void preparePlayer(boolean needReset) {
        PlayerLog.d(TAG, "preparePlayer : "  );
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
     * 是否处于正常播放状态
     * 处于
     * {@link #STATE_PREPARED}
     * {@link #STATE_PLAYING}
     * {@link #STATE_BUFFERING}
     * {@link #STATE_BUFFERED}
     * {@link #STATE_PAUSED}
     */
    @Override
    public boolean isInPlaybackState() {
        PlayerLog.d(TAG, "isInPlaybackState : "  );
        return (mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED);
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        PlayerLog.d(TAG, "start : "  );
        if (mAudioFocusHelper != null){
            mAudioFocusHelper.requestFocus();
        }
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            notifyPlayStateChanged(STATE_PLAYING);
        }
        setKeepScreenOn(true);
    }


    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        PlayerLog.d(TAG, "pause : "  );
        if (isPlaying()) {
            mMediaPlayer.pause();
            notifyPlayStateChanged(STATE_PAUSED);
            setKeepScreenOn(false);
            if (mAudioFocusHelper != null)
                mAudioFocusHelper.abandonFocus();
        }
    }

    /**
     * 停止播放, 不同于暂停，不交给控制器
     */
    @Override
    public void stopPlayback() {
        PlayerLog.d(TAG, "stopPlayback :  "  );
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
     * 释放播放器资源 彻底结束的时候
     */
    @Override
    public void release() {
        PlayerLog.d(TAG, "release :  "  );
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
     * @param listener
     */
    public void addPlayStateListener(OnPlayStateListener listener) {
        if (mOnPlayStateListeners == null) {
            mOnPlayStateListeners = new ArrayList<>();
        }
        mOnPlayStateListeners.add(listener);
    }

    /**
     * 移除播放状态监听
     * @param listener
     */
    public void removePlayStateListener(OnPlayStateListener listener) {
        if (mOnPlayStateListeners != null) {
            mOnPlayStateListeners.remove(listener);
        }
    }

    /**
     * 移除所有播放状态监听
     */
    public void clearOnVideoViewStateChangeListeners() {
        if (mOnPlayStateListeners != null) {
            mOnPlayStateListeners.clear();
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
            PlayerLog.d(TAG, "getCurrentPosition : pos = " + mCurrentPosition  );
            return mCurrentPosition;
        }
        return 0;
    }

    /**
     * 调整播放进度
     */
    @Override
    public void seekTo(long pos) {
        PlayerLog.d(TAG, "seekTo : pos = " + pos  );
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        PlayerLog.d(TAG, "isPlaying : "  );
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        int bufferPercent = mMediaPlayer != null ? mMediaPlayer.getBufferedPercent() : 0;
        PlayerLog.d(TAG, "getBufferedPercentage : bufferPercent " + bufferPercent );
        return bufferPercent;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        PlayerLog.d(TAG, "setMute : isMute = " + isMute );
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
        PlayerLog.d(TAG, "setLock : isLocked = " + isLocked );
        this.mIsLockFullScreen = isLocked;
    }


    /**
     * 获取缓冲速度
     */
    @Override
    public long getTcpSpeed() {
        PlayerLog.d(TAG, "getTcpSpeed : "  );
        return mMediaPlayer.getTcpSpeed();
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        PlayerLog.d(TAG, "setSpeed : speed = " + speed );
        if (isInPlaybackState()) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    /**
     * 重新播放
     * @param resetPosition 是否从头开始播放
     */
    @Override
    public void replay(boolean resetPosition) {
        PlayerLog.d(TAG, "replay : resetPosition = " + resetPosition );
        if (resetPosition) {
            mCurrentPosition = 0;
        }
       openVideo();
    }


    /**
     * 视频播放器内部 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared() {
        PlayerLog.d(TAG, "onPrepared : " );
        mIsPrepared = true;
        notifyPlayStateChanged(STATE_PREPARED);
        if (mBaseMediaController != null) {
            mBaseMediaController.setEnabled(true);
        }
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        PlayerLog.d(TAG, "onVideoSizeChanged : width = " + width + " height = " + height );
    }

    /**
     * 视频播放器内部 缓存 回调
     * @param percent
     */
    @Override
    public void onBufferUpdated(int percent) {
        PlayerLog.d(TAG, "onBufferUpdated : percent = " + percent );
    }

    /**
     * 视频播放器内部 seek完成 回调
     */
    @Override
    public void onSeekCompletion() {
        PlayerLog.d(TAG, "onSeekCompletion : " );
    }

    /**
     * 视频播放器内部 信息 回调
     * @param what
     * @param extra
     */
    @Override
    public void onInfo(int what, int extra) {
        PlayerLog.d(TAG, "onInfo : " );
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
     * 视频播放器内部 播放出错回调
     */
    @Override
    public void onError(String message) {
        PlayerLog.d(TAG, "onError : " + message);
        notifyPlayStateChanged(STATE_ERROR);
    }

    /**
     * 视频播放器内部 播放完成回调
     */
    @Override
    public void onCompletion() {
        PlayerLog.d(TAG, "onCompletion : " );
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
     * 设置视频数据
     * @param videoBean
     */
    public void setVideoBean(IVideoBean videoBean) {
        mVideoBean = videoBean;
        if (videoBean == null) {
            notifyPlayStateChanged(STATE_ERROR);
        }
        setVideoUrl(videoBean.getSource());
        if (mBaseMediaController != null) {
            mBaseMediaController.setTitle(videoBean.getTitle());
        }
    }

    /**
     * 设置视频地址
     */
    public void setVideoUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            this.mCurrentUrl = url;
            openVideo();
        } else {
            notifyPlayStateChanged(STATE_ERROR);
        }
    }

    /**
     * 设置包含请求头信息的视频地址
     *
     * @param url     视频地址
     * @param headers 请求头
     */
    public void setVideoUrl(String url, Map<String, String> headers) {
        mCurrentUrl = url;
        mHeaders = headers;
        openVideo();
    }

    /**
     * 用于播放assets里面的视频文件
     */
    public void setAssetFileDescriptor(AssetFileDescriptor fd) {
        this.mAssetFileDescriptor = fd;
        openVideo();
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

    /**
     * 设备方向传感器 监听，只和设备有关
     */
    protected OrientationEventListener mOrientationEventListener = new OrientationEventListener(getContext()){
        private long mLastTime;
        @Override
        public void onOrientationChanged(int orientation) {

            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime < 300) {
                //300毫秒检测一次
                return;
            }
            Activity activity = PlayerUtils.scanForActivity(IjkBaseVideoView.this.getContext());
            if (activity == null) {
                return;
            }

            if(orientation > 350 || orientation < 10) {
                //0度，Home键 在下方，屏幕顶部朝上
                onOrientationPortrait(activity);

            } else if(orientation > 80 && orientation < 100) {
                //90度，Home键 在右边，屏幕右边朝上
                onOrientationReverseLandscape(activity);

            } else if(orientation > 170 && orientation < 190) {
                //180度，用户反向竖直拿着手机


            } else if(orientation > 260 && orientation < 280) {
                //270度，Home键 在右边，屏幕左边朝上
                onOrientationLandscape(activity);
            }
            mLastTime = currentTime;
        }
    };

    /**
     * activity 竖屏
     */
    protected void onOrientationPortrait(Activity activity) {
        if (activity == null) {
            return;
        }
        // 全屏锁 || 不允许自动旋转 || 当前是竖屏
        if (mIsLockFullScreen || !mAutoRotate || mCurrentOrientation == SCREEN_ORIENTATION_PORTRAIT)
            return;
        if ((mCurrentOrientation == SCREEN_ORIENTATION_LANDSCAPE || mCurrentOrientation == SCREEN_ORIENTATION_REVERSE_LANDSCAPE) && !isFullScreen()) {
            mCurrentOrientation = SCREEN_ORIENTATION_PORTRAIT;
            return;
        }
        mCurrentOrientation = SCREEN_ORIENTATION_PORTRAIT;
        // 主动设置 竖屏
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        exitFullScreen();
    }

    /**
     * activity 正向横屏，
     */
    protected void onOrientationLandscape(Activity activity) {
        if (activity == null) {
            return;
        }

        if (mCurrentOrientation == SCREEN_ORIENTATION_LANDSCAPE) {
            return;
        }

        if (mCurrentOrientation == SCREEN_ORIENTATION_PORTRAIT
                && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                && isFullScreen()) {
            mCurrentOrientation = SCREEN_ORIENTATION_LANDSCAPE;
            return;
        }
        mCurrentOrientation = SCREEN_ORIENTATION_LANDSCAPE;

        // 主动设置 横屏
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!isFullScreen()) {
            enterFullScreen();
        }
    }

    /**
     * activity 反向横屏
     */
    protected void onOrientationReverseLandscape(Activity activity) {
        if (mCurrentOrientation == SCREEN_ORIENTATION_REVERSE_LANDSCAPE) return;
        if (mCurrentOrientation == SCREEN_ORIENTATION_PORTRAIT
                && activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && isFullScreen()) {
            mCurrentOrientation = SCREEN_ORIENTATION_LANDSCAPE;
            return;
        }
        mCurrentOrientation = SCREEN_ORIENTATION_LANDSCAPE;

        // 主动设置 横屏
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!isFullScreen()) {
            enterFullScreen();
        }

    }

    /**
     * 进入全屏
     */
    public void enterFullScreen(){
        if (mBaseMediaController != null) {
            mBaseMediaController.enterFullScreen();
        }
        notifyScreenModeChanged(SCREEN_MODE_FULL);
    }

    /**
     * 退出全屏
     */
    public void exitFullScreen(){
        if (mBaseMediaController != null) {
            mBaseMediaController.exitFullScreen();
        }
        notifyScreenModeChanged(SCREEN_MODE_NORMAL);
        if (!mAutoRotate) {
            mOrientationEventListener.disable();
        }
    }

    /**
     * 是否全屏
     * @return
     */
    public boolean isFullScreen(){
        return mBaseMediaController != null && mBaseMediaController.isFullScreen();
    }


    /**
     * 设置控制器
     * @param baseMediaController
     */
    public void setMediaController(BaseMediaController baseMediaController) {
        mBaseMediaController = baseMediaController;

        // 不为null且不是一直显示
        if (baseMediaController != null) {
            baseMediaController.hide();
        }
        attachMediaController();
    }

    /**
     * 控制器里设置播放器
     */
    public void attachMediaController() {
        if (mBaseMediaController != null) {
            mBaseMediaController.setMediaPlayer(this);
            mBaseMediaController.setEnabled(true);
        }
    }


    /**
     * 通知播放状态改变
     * @param playState
     */
    public void notifyPlayStateChanged(int playState){
        mCurrentPlayState = playState;
        PlayerLog.d(TAG, "playState : " + playState(playState));
        if (mBaseMediaController != null) {
            mBaseMediaController.setPlayerState(playState);
        }
        for (OnPlayStateListener listener: mOnPlayStateListeners) {
            if (listener != null) {
                listener.onPlayStateChanged(playState);
            }
        }
    }

    public String playState(int playState){
        switch (playState) {
            case  STATE_ERROR : return "STATE_ERROR";
            case  STATE_IDLE : return "STATE_IDLE";
            case  STATE_PREPARING : return "STATE_PREPARING";
            case  STATE_PREPARED : return "STATE_PREPARED";
            case  STATE_PLAYING : return "STATE_PLAYING";
            case  STATE_PAUSED : return "STATE_PAUSED";
            case  STATE_PLAYBACK_COMPLETED : return "STATE_PLAYBACK_COMPLETED";
            case  STATE_BUFFERING : return "STATE_BUFFERING";
            case  STATE_BUFFERED : return "STATE_BUFFERED";
            default:
                return "";
        }
    }

    /**
     * 通知屏幕模式改变
     * @param screenMode
     */
    public void notifyScreenModeChanged(int screenMode) {
        mCurrentScreenMode = screenMode;

        for (OnPlayStateListener listener : mOnPlayStateListeners) {
            if (listener != null) {
                listener.onScreenModeChanged(screenMode);
            }
        }
    }

}
