package com.yingke.player.java.controller;

import android.content.Context;
import android.media.AudioManager;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.PlayerUtils;
import com.yingke.player.java.R;
import com.yingke.player.java.widget.GestureView;

/**
 * 功能：控制器基类  与业务无关，有手势实现亮度，音量，快进快退功能
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/21
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class MediaController extends BaseMediaController {

    private static final String TAG = "MediaController";

    protected static boolean mIsGestureEnabled = true;

    protected GestureDetector mGestureDetector;
    protected GestureView mGestureView;
    protected AudioManager mAudioManager;

    // 音量
    protected int mCurrentVolume;
    // 亮度 0 ~ 1
    protected float mCurrentBrightness;
    // 视频
    protected boolean mNeedSeek;
    // 需要seek的位置
    protected int mNeedSeekToPosition;

    public MediaController(Context context) {
        super(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();

        if (mIsGestureEnabled) {
            mGestureView = new GestureView(getContext());
            mGestureView.setVisibility(GONE);
            addView(mGestureView);

            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mGestureDetector = new GestureDetector(getContext(), new MediaGestureListener());

            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    PlayerLog.e(TAG, "onTouch: event = " + getAction(event));
                    if (isGestureOperateEnabled(event)) {
                        return mGestureDetector.onTouchEvent(event);
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    protected class MediaGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean mIsFirstTouch;
        private boolean mIsChangePosition;
        private boolean mIsChangeBrightness;
        private boolean mIsChangeVolume;

        @Override
        public boolean onDown(MotionEvent e) {
            PlayerLog.e(TAG, "onDown: event = " + getAction(e));

            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mCurrentBrightness = PlayerUtils.scanForActivity(getContext()).getWindow().getAttributes().screenBrightness;

            mIsFirstTouch = true;
            mIsChangePosition = false;
            mIsChangeBrightness = false;
            mIsChangeVolume = false;

            PlayerLog.e(TAG, "onDown: return = true" );
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            PlayerLog.e(TAG, "onScroll: event1 = " + getAction(e1));
            PlayerLog.e(TAG, "onScroll: event2 = " + getAction(e2));
            // 上下/左右 滑动
            float deltaX = e1.getX() - e2.getX();
            float deltaY = e1.getY() - e2.getY();

            if (mIsFirstTouch) {
                mIsChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
                if (!mIsChangePosition) {
                    if (e2.getX() > PlayerUtils.getScreenWidth(getContext()) / 2) {
                        mIsChangeBrightness = true;
                    } else {
                        mIsChangeVolume = true;
                    }
                }
                mIsFirstTouch = false;
            }

            // 实际改变
            if (mIsChangePosition) {
                doChangePlayPosition(deltaX);
            } else if (mIsChangeBrightness) {
                doChangeBrightness(deltaY);
            } else if (mIsChangeVolume) {
                doChangeVolume(deltaY);
            }

            PlayerLog.e(TAG, "onScroll: return = true" );
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            PlayerLog.e(TAG, "onSingleTapUp: event = " + getAction(e));
            PlayerLog.e(TAG, "onSingleTapUp: return = true" );
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            PlayerLog.e(TAG, "onDoubleTap: event = " + getAction(e));
            // 双击暂停
            doPauseResume();
            PlayerLog.e(TAG, "onDoubleTap: return = true" );
            return true;

        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            PlayerLog.e(TAG, "onDoubleTapEvent: event = " + getAction(e));
            PlayerLog.e(TAG, "onDoubleTap: return = true" );
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            PlayerLog.e(TAG, "onSingleTapConfirmed: event = " + getAction(e));
            // 显示主控制器内容
            toggleMediaControllerVisibility();
            PlayerLog.e(TAG, "onSingleTapConfirmed: return = true" );
            return true;
        }


        @Override
        public void onLongPress(MotionEvent e) {
            PlayerLog.e(TAG, "onLongPress: event = " + getAction(e));
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            PlayerLog.e(TAG, "onContextClick: event = " + getAction(e));
            PlayerLog.e(TAG, "onContextClick: return = false" );
            return super.onContextClick(e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        PlayerLog.e(TAG, "onTouchEvent: event = " + getAction(ev));

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: return true;
            case MotionEvent.ACTION_MOVE:
                if (isGestureOperateEnabled(ev)) {
                    hide();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (isGestureOperateEnabled(ev)) {
                    PlayerLog.e(TAG, "onTouchEvent: it works");
                    if (mGestureView != null && mGestureView.getVisibility() == VISIBLE) {
                        mGestureView.setVisibility(GONE);
                    }
                    if (mNeedSeek && mMediaPlayer != null) {
                        mMediaPlayer.seekTo(mNeedSeekToPosition);
                        mNeedSeek = false;
                    }
                    return true;
                } else {
                    PlayerLog.e(TAG, "super.onTouchEvent: event = " + getAction(ev));
                    return super.onTouchEvent(ev);
                }
                default:
                    return false;
        }
    }

    /**
     * 实际手势操作是否可用
     * 有可能显示的其他 控制器页面（画中画，广告）
     * @param e
     * @return
     */
    public boolean isGestureOperateEnabled(MotionEvent e) {
        boolean isEnable = mIsGestureEnabled && isNormalControllerShow() && !isEdge(getContext(), e) && isFullScreen();
        PlayerLog.e(TAG, "isGestureOperateEnabled: isEnable = " + isEnable);
        return isEnable;
    }


    /**
     * 边缘检测
     * 触点位置 在距离上下边框40dp内
     */
    public boolean isEdge(Context context, MotionEvent e) {
        int edgeSize = PlayerUtils.dip2px(context, 40);
        return e.getRawX() < edgeSize
                || e.getRawX() > PlayerUtils.getScreenWidth(context) - edgeSize
                || e.getRawY() < edgeSize
                || e.getRawY() > PlayerUtils.getScreenHeight(context) - edgeSize;
    }


    /**
     * 改变播放位置
     * @param deltaX
     */
    protected void doChangePlayPosition(float deltaX) {
        PlayerLog.e(TAG, "doChangePlayPosition: " );
        mGestureView.setVisibility(VISIBLE);
        hide();
        mGestureView.setProVisibility(View.GONE);
        deltaX = -deltaX;

        int width = getMeasuredWidth();
        int duration = (int) mMediaPlayer.getDuration();
        int currentPosition = (int) mMediaPlayer.getCurrentPosition();
        int position = (int) (deltaX / width * 120000 + currentPosition);

        // 设置图标
        if (position > currentPosition) {
            mGestureView.setIcon(R.drawable.icon_forward);
        } else {
            mGestureView.setIcon(R.drawable.icon_rewind);
        }
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        // 需要seek的位置
        mNeedSeekToPosition = position;
        // 设置文本
        mGestureView.setTextView( DateUtils.formatElapsedTime(mNeedSeekToPosition / 1000) + "/" + DateUtils.formatElapsedTime(duration / 1000));
        mNeedSeek = true;
    }

    /**
     * 改变 亮度
     * @param deltaY
     */
    protected void doChangeBrightness(float deltaY) {
        PlayerLog.e(TAG, "doChangeBrightness: " );

        hide();

        mGestureView.setVisibility(VISIBLE);
        mGestureView.setProVisibility(View.VISIBLE);
        mGestureView.setIcon(R.drawable.icon_brightness);

        // 计算亮度
        int height = getMeasuredHeight();
        if (mCurrentBrightness == -1.0f) {
            mCurrentBrightness = 0.5f;
        }
        float brightness = deltaY * 2 / height * 1.0f + mCurrentBrightness;

        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) {
            brightness = 1.0f;
        }
        int percent = (int) (brightness * 100);
        mGestureView.setTextView(percent + "%");
        mGestureView.setProPercent(percent);

        // 设置到系统
        Window window = PlayerUtils.scanForActivity(getContext()).getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
    }

    /**
     * 改变音量
     * @param deltaY
     */
    protected void doChangeVolume(float deltaY) {
        PlayerLog.e(TAG, "doChangeVolume: " );

        hide();

        mGestureView.setVisibility(VISIBLE);
        mGestureView.setProVisibility(View.VISIBLE);

        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mCurrentVolume + deltaV;

        if (index > streamMaxVolume) {
            index = streamMaxVolume;
        }
        if (index < 0) {
            mGestureView.setIcon(R.drawable.icon_volume_off);
            index = 0;
        } else {
            mGestureView.setIcon(R.drawable.icon_volume_on);
        }
        int percent = (int) (index / streamMaxVolume * 100);
        mGestureView.setTextView(percent + "%");
        mGestureView.setProPercent(percent);

        // 设置到系统
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
    }

    public String getAction(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN : return "ACTION_DOWN";
            case MotionEvent.ACTION_UP: return "ACTION_UP";
            case MotionEvent.ACTION_MOVE: return "ACTION_MOVE";
            case MotionEvent.ACTION_CANCEL: return "ACTION_CANCEL";
            case MotionEvent.ACTION_POINTER_DOWN : return "ACTION_POINTER_DOWN";
            case MotionEvent.ACTION_POINTER_UP : return "ACTION_POINTER_UP";
            case MotionEvent.ACTION_HOVER_MOVE : return "ACTION_HOVER_MOVE";
            case MotionEvent.ACTION_SCROLL : return "ACTION_SCROLL";

            default: return "ACTION_OTHER";

        }
    }

}
