package com.yingke.player.java.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.PlayerUtils;
import com.yingke.player.java.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.yingke.player.java.videoview.IjkBaseVideoView.STATE_PREPARED;

/**
 * 功能：视频控制器
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 */
public abstract class BaseMediaController extends FrameLayout {

    protected static final String TAG = "BaseMediaController";

    protected static final int sDefaultTimeout = 3000;

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

    // 顶部总布局
    protected RelativeLayout mBottomView;
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

    // 播放过程中总是显示控制器
    protected boolean mIsShowAlways = false;

    // 监听回调
    protected OnFullScreenListener mFullScreenListener;
    protected OnShownHiddenListener mShownHiddenListener;

    // 更新进度条
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

        // 播放暂停
        mPauseResumeBtn = mRootView.findViewById(R.id.controller_play_pause_btn);
        if (mPauseResumeBtn != null) {
            mPauseResumeBtn.setOnClickListener(mPauseResumeListener);
        }

        mBottomView = mRootView.findViewById(R.id.controller_bottom_view);

        // 当前时间
        mCurrentTime = mRootView.findViewById(R.id.controller_current_time);
        // 总时间
        mTotalTime = mRootView.findViewById(R.id.controller_total_time);

        // 倍速
        mSpeedLandView = mRootView.findViewById(R.id.controller_speed_btn);
        if (mSpeedLandView != null){
            mSpeedLandView.setText(mSpeedName[mSpeedPos]);
            mSpeedLandView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSpeedPos++;
                    if (mSpeedPos == 3)
                        mSpeedPos = 0;
                    if (mOnSpeedAction != null) {
                        mOnSpeedAction.onSpeedClick(mSpeedPos);
                    }
                    mSpeedLandView.setText(mSpeedName[mSpeedPos]);
                }
            });
        }

        // 分辨率
        mResolutionLandView = mRootView.findViewById(R.id.controller_clearness_btn);
        if (mResolutionLandView != null) {
            mResolutionLandView.setText(mResolutionName[mResolutionPos]);
            mResolutionLandView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mResolutionPos++;
                    if (mResolutionPos == 3)
                        mResolutionPos = 0;
                    if (mResolutionAction != null) {
                        mResolutionAction.onResolutionClick(mResolutionPos);
                    }
                    mResolutionLandView.setText(mResolutionName[mResolutionPos]);
                }
            });
        }


        // 全屏
        mFullScreenView = mRootView.findViewById(R.id.controller_fullscreen_btn);
        if (mFullScreenView != null) {
            mFullScreenView.setOnClickListener(mScaleListener);
        }

        mLockView = mRootView.findViewById(R.id.controller_lock);
        // 进度条
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
            show();
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
            setRequestedOrientation();
        }
    };

    /**
     * 主动设置 横屏 竖屏
     */
    protected void setRequestedOrientation(){
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) {
            PlayerLog.d(TAG,"controller attached activity is null");
            return;
        }
        if (isFullScreen()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setFullScreenStatus(!mIsFullScreen);
    }

    /**
     * 设置全屏状态
     * 外面进入/退出全屏状态，需要同步状态给控制器
     * @param isFullScreen
     */
    public void setFullScreenStatus(boolean isFullScreen){
        this.mIsFullScreen = isFullScreen;
        afterFullScreenChanged();

    }

    /**
     * 进入/退出全屏，UI变更
     */
    protected void afterFullScreenChanged() {
        // 进入全屏
        mFullScreenView.setImageResource(isFullScreen() ? R.drawable.icon_controller_unfull : R.drawable.icon_controller_full);
        if (isFullScreen()) {
            mTitlePort.setVisibility(GONE);
            mTitleLandView.setVisibility(VISIBLE);
            mBackLand.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 退出全屏
                    setRequestedOrientation();
                }
            });
            mShareLand.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                    }
                    // 更多/分享回调
                    if (mOnShareListener != null) {
                        mOnShareListener.onShare();
                    }
                }
            });
            mSpeedLandView.setVisibility(VISIBLE);
            mResolutionLandView.setVisibility(VISIBLE);

            // 回调到外面
            if (mFullScreenListener != null) {
                mFullScreenListener.onEnterFullScreen();
            }
        } else {
            mTitlePort.setVisibility(VISIBLE);
            mTitleLandView.setVisibility(GONE);
            mSpeedLandView.setVisibility(GONE);
            mResolutionLandView.setVisibility(GONE);

            // 回调到外面
            if (mFullScreenListener != null) {
                mFullScreenListener.onExitFullScreen();
            }
        }

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
            show(0);
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

            show();
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

            // 通知位置 s
            notifyPlayerPosition((int) currentPosition / 1000, (int) (percent * (duration / 1000)), (int) (duration / 1000));
        }
    }

    /**
     * 进度条的进度位置 单位s
     * @param currentPositionS  当前位置
     * @param secondPositionS   第二位置
     * @param durationS         总时长
     */
    protected void notifyPlayerPosition(int currentPositionS, int secondPositionS, int durationS) {

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
     */
    public void show() {
        if (mIsShowAlways) {
            show(0);
        } else {
            show(sDefaultTimeout);
        }
    }

    /**
     * 显示 控制器
     * @param timeOut 0 一直显示直到 hide 调用
     */
    public void show(int timeOut){
        if (!isShowing()) {
            setVisibility(VISIBLE);
            if (mShownHiddenListener != null){
                mShownHiddenListener.onShown();
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

            // 开启更新进度条
//            if (mUpdateProgressHelper == null) {
//                mUpdateProgressHelper = new UpdateProgressHelper();
//            }
//            mUpdateProgressHelper.startSeekBarUpdate();
        }
    }

    /**
     * 隐藏控制器
     */
    public void hide() {
        if (isShowing()) {
            setVisibility(GONE);
            if (mShownHiddenListener != null) {
                mShownHiddenListener.onHidden();
            }
            updatePlayPauseView();
            mIsShowing = false;
            mMainHandler.removeCallbacks(mShowHideTask);
        }
    }

    public boolean isShowing() {
        mIsShowing = (getVisibility() == VISIBLE);
        return mIsShowing;
    }

    public void setControllerShownHidedListener(OnShownHiddenListener shownHiddenListener) {
        mShownHiddenListener = shownHiddenListener;
    }

    /**
     * 设置播放器状态
     * @param playerState
     */
    public void setPlayerState(int playerState) {
        switch (playerState) {
            case STATE_PREPARED:
                // 开启更新进度条
                if (mUpdateProgressHelper == null) {
                    mUpdateProgressHelper = new UpdateProgressHelper();
                }
                mUpdateProgressHelper.startSeekBarUpdate();
                break;
        }
    }

    /**
     * 显示回调
     */
    public interface OnShownHiddenListener {
        /**
         * 显示
         */
        void onShown();

        /**
         * 隐藏
         */
        void onHidden();
    }

    /**
     * 设置全屏
     * @param fullScreenListener
     */
    public void setFullScreenListener(OnFullScreenListener fullScreenListener) {
        mFullScreenListener = fullScreenListener;
    }

    /**
     * 全屏操作
     */
    public interface OnFullScreenListener {

        /**
         * 进入全屏
         */
        void onEnterFullScreen();

        /**
         * 退出全屏
         */
        void onExitFullScreen();

    }

    // 分享相关
    private onShareListener mOnShareListener;

    /**
     * 设置分享回调
     * @param onShareListener
     */
    public void setOnShareListener(onShareListener onShareListener) {
        mOnShareListener = onShareListener;
    }

    /**
     * 分享回调
     */
    public interface onShareListener{

        /**
         * 更多，分享
         */
        void onShare();
    }

    // 倍速相关
    public static final String mSpeedName[] = new String[]{
            "1.0x", "1.25x", "1.5x"
    };

    private int mSpeedPos = 0;

    /**
     * 设置倍速 外部
     *
     * @param pos
     */
    public void setSpeed(int pos) {
        this.mSpeedPos = pos;
        mSpeedLandView.setText(mSpeedName[mSpeedPos]);
    }

    private OnSpeedAction mOnSpeedAction = null;

    /**
     * 设置倍速监听器
     *
     * @param l
     */
    public void setOnSpeedAction(OnSpeedAction l) {
        this.mOnSpeedAction = l;
    }

    /**
     * 倍速监听器
     */
    public interface OnSpeedAction {
        /**
         * 倍速点击
         *
         * @param speedPos
         */
        void onSpeedClick(int speedPos);
    }

    // 清晰度相关

    public static final String mResolutionName[] = new String[]{
            "标清", "高清", "超清"
    };

    private int mResolutionPos = 0;

    /**
     * 设置清晰度 外部
     *
     * @param pos
     */
    public void setResolution(int pos) {
        this.mResolutionPos = pos;
        mResolutionLandView.setText(mResolutionName[mResolutionPos]);
    }

    public OnResolutionAction mResolutionAction;

    /**
     * 设置 清晰度
     * @param resolutionAction
     */
    public void setResolutionAction(OnResolutionAction resolutionAction) {
        mResolutionAction = resolutionAction;
    }

    /**
     * 清晰度回调
     */
    public interface OnResolutionAction{

        /**
         * 清晰度点击
         * @param resolutionPos
         */
        void onResolutionClick(int resolutionPos);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        mTitlePort.setText(title);
        mTitleLand.setText(title);
    }

    /**
     * @return 是否静音
     */
    public boolean isMute() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isMute();
        }
        return false;
    }

    /**
     * 设置静音
     * @param isMute
     */
    public void setMute(boolean isMute) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setMute(isMute);
        }
    }
    /**
     * 一直显示控制器
     * @return
     */
    public boolean isShowAlways() {
        return mIsShowAlways;
    }

    /**
     *
     * @param showAlways
     */
    public void setShowAlways(boolean showAlways) {
        mIsShowAlways = showAlways;
    }

    public void showNormalController(){
        mControllerContent.setVisibility(VISIBLE);
    }

    public void hideNormalController(){
        mControllerContent.setVisibility(GONE);
    }

}
