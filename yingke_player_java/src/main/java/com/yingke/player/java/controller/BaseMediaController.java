package com.yingke.player.java.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yingke.player.java.R;
import com.yingke.player.java.listener.OnFullScreenListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 */
public abstract class BaseMediaController extends FrameLayout {

    protected View mRootView;
    // 控制器内容
    protected RelativeLayout mControllerContent;

    // 顶部总布局
    protected RelativeLayout mTopView;
    // 竖屏 标题
    protected TextView mTitlePort;
    // 横屏标题 根布局
    protected RelativeLayout mTitleLandView;
    // 横屏 返回
    protected ImageView mBackLand;
    // 横屏 标题
    protected TextView mTitleLand;
    // 横屏 分享
    protected ImageView mShareLand;

    // 播放/暂停
    protected ImageView mPauseResumeBtn;

    // 当前时间
    protected TextView mCurrentTime;
    // 总时间
    protected TextView mTotalTime;
    // 进度条
    protected SeekBar mSeekBar;
    // 倍速
    protected TextView mSpeedLandView;
    // 清晰度
    protected TextView mResolutionLandView;
    // 全屏
    protected ImageView mFullScreenView;
    // 屏幕锁
    protected ImageView mLockView;


    // 播放器控制
    protected MediaPlayerControl mMediaPlayer;
    protected boolean mIsLocked = false;

    protected boolean mIsDraggingSeekBar = false;
    protected boolean mIsShowing = false;
    protected boolean mPaused = false;
    protected boolean mIsFullScreen = false;
    protected OnFullScreenListener mFullScreenListener;
    protected OnShownListener mShownListener;
    protected OnHiddenListener mHiddenListener;

    protected UpdateProgressHelper mUpdateProgressHelper;

    public BaseMediaController(Context context) {
        this(context, null);
    }

    public BaseMediaController(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
    }

    protected void initView(){
        mRootView = LayoutInflater.from(getContext()).inflate(getLayoutResId(), this);
        initControllerView();
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 资源id
     */
    protected int getLayoutResId(){
        return R.layout.base_media_controller;
    }

    /**
     * 初始化布局
     */
    protected void initControllerView() {
        mControllerContent = mRootView.findViewById(R.id.controller_content);
        mTopView = mRootView.findViewById(R.id.controller_top_view);

        mTitlePort = mRootView.findViewById(R.id.controller_title_port);
        mTitleLandView = mRootView.findViewById(R.id.controller_title_land_view);
        mBackLand = mRootView.findViewById(R.id.controller_back_land);
        mTitleLand = mRootView.findViewById(R.id.controller_title_land);
        mShareLand = mRootView.findViewById(R.id.controller_share_land);

        mPauseResumeBtn = mRootView.findViewById(R.id.controller_play_pause_btn);
        if (mPauseResumeBtn != null) {
            mPauseResumeBtn.setOnClickListener(mPauseResumeListener);
        }
        mCurrentTime = mRootView.findViewById(R.id.controller_current_time);
        mTotalTime = mRootView.findViewById(R.id.controller_total_time);
        mSpeedLandView = mRootView.findViewById(R.id.controller_speed_btn);
        mResolutionLandView = mRootView.findViewById(R.id.controller_clearness_btn);

        mFullScreenView = mRootView.findViewById(R.id.controller_fullscreen_btn);
        if (mFullScreenView != null) {
            mFullScreenView.setOnClickListener(mScaleListener);
        }

        mLockView = mRootView.findViewById(R.id.controller_lock);
        mSeekBar = mRootView.findViewById(R.id.controller_seekbar);
        if (mSeekBar != null){
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }

    }

    /**
     * 初始化数据
     */
    protected void initData() {
        mUpdateProgressHelper = new UpdateProgressHelper();
    }

    /**
     * 设置  播放器
     * @param mediaPlayer
     */
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    /**
     * 暂停，恢复播放按钮
     */
    protected View.OnClickListener mPauseResumeListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            // TODO show
        }
    };

    /**
     * 做 暂停播放操作
     */
    protected void doPauseResume() {
        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
        // 更新 暂停/播放 按钮
        updatePlayPauseView();
    }

    /**
     * 更新 暂停/播放 按钮
     */
    protected void updatePlayPauseView() {
        if (mPauseResumeBtn == null || mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mPauseResumeBtn.setImageResource(R.drawable.icon_controller_pause);
        } else {
            mPauseResumeBtn.setImageResource(R.drawable.icon_controller_play);
        }
    }

    /**
     * 全屏缩放
     */
    protected View.OnClickListener mScaleListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
             if (mIsFullScreen) {
                 if (mFullScreenListener != null) {
                     mFullScreenListener.onExitFullScreen();
                 }
             } else {
                 if (mFullScreenListener != null) {
                     mFullScreenListener.onEnterFullScreen();
                 }
             }
             setFullScreenStatus(!mIsFullScreen);
        }
    };

    /**
     * 设置全屏状态
     * @param isFullScreen
     */
    public void setFullScreenStatus(boolean isFullScreen){
        this.mIsFullScreen = isFullScreen;
    }

    /**
     * 是否全屏
     * @return
     */
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    /**
     * 进度条 改变监听器
     */
    protected SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // s秒
            long currentTime = progress;
            if (mCurrentTime != null) {
                mCurrentTime.setText(DateUtils.formatElapsedTime(currentTime));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO show
            BaseMediaController.this.setVisibility(VISIBLE);
            mIsDraggingSeekBar = true;
            if (mUpdateProgressHelper != null) {
                mUpdateProgressHelper.stopSeekBarUpdate();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mMediaPlayer == null) {
                return;
            }
            // TODO show
            mIsDraggingSeekBar = false;
            mMediaPlayer.seekTo(seekBar.getProgress() * 1000);
            if (mUpdateProgressHelper != null) {
                mUpdateProgressHelper.startSeekBarUpdate();
            }
        }
    };

    /**
     * 内部更新进度条
     * SeekBar是 s
     */
    public void updateProgressInternal() {
        if (mMediaPlayer == null) {
            return;
        }
        long duration = mMediaPlayer.getDuration();
        long currentPosition = mMediaPlayer.getCurrentPosition();
        boolean isPlaying  = mMediaPlayer.isPlaying();
        // 更新按钮
        updatePlayPauseView();

        // 初始化总时长
        if (mTotalTime != null) {
            mTotalTime.setText(DateUtils.formatElapsedTime(duration / 1000));
        }

        // 1、初始化最大进度 2、更新当前进度
        if (mSeekBar != null) {
            if (duration > 0) {
                // 当前最大进度 != 当前音频时长
                int max = mSeekBar.getMax();
                int du = (int) duration / 1000;
                // 设置最大进度
                if (max != du) {
                    mSeekBar.setMax(du);
                }
            }
            // 缓存进度
            int percent = mMediaPlayer.getBufferedPercentage();
            mSeekBar.setSecondaryProgress((int) (percent * (duration / 1000)));

            // 设置当前进度
            mSeekBar.setProgress((int) currentPosition / 1000);
        }
    }

    /**
     * 更新进度条
     */
    public class UpdateProgressHelper {
        private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
        private ScheduledFuture<?> mScheduledFuture;

        private static final long PROGRESS_UPDATE_INTERNAL = 1000;
        private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 200;

        private final Handler mMainHandler = new Handler(Looper.getMainLooper());

        private final Runnable mUpdateProgressTask = new Runnable() {
            @Override
            public void run() {
                // 接口回调
                updateProgressInternal();
            }
        };

        /**
         * 开始更新 进度条
         */
        public void startSeekBarUpdate() {
            stopSeekBarUpdate();
            if (!mExecutorService.isShutdown()) {
                mScheduledFuture = mExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        mMainHandler.post(mUpdateProgressTask);
                    }
                }, PROGRESS_UPDATE_INITIAL_INTERVAL,PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
            }
        }

        /**
         * 停止更新 进度条
         */
        public void stopSeekBarUpdate(){
            if (mScheduledFuture != null) {
                mScheduledFuture.cancel(false);
            }
        }
    }

    private Runnable mShowHideTask;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 显示 控制器
     * @param timeOut 0 一直显示直到 hide 调用
     */
    public void show(int timeOut){
        if (!isShowing()) {
            setVisibility(VISIBLE);
            if (mShownListener != null){
                mShownListener.onShown();
            }
            updatePlayPauseView();
            mIsShowing = true;
            if (timeOut != 0) {
                mMainHandler.removeCallbacks(mShowHideTask);
                mShowHideTask = new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                };
                mMainHandler.postDelayed(mShowHideTask, timeOut);
            }
        }
    }

    /**
     * 隐藏控制器
     */
    public void hide() {
        if (isShowing()) {
            setVisibility(GONE);
            if (mHiddenListener != null) {
                mHiddenListener.onHidden();
            }
            updatePlayPauseView();
            mIsShowing = false;
            mMainHandler.removeCallbacks(mShowHideTask);
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * 显示回调
     */
    public interface OnShownListener {
        /**
         * 显示
         */
        void onShown();
    }

    /**
     * 隐藏回调
     */
    public interface OnHiddenListener {
        /**
         * 隐藏
         */
        void onHidden();
    }



}