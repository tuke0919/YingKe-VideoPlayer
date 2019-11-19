package com.yingke.videoplayer.home.pip;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.util.DeviceUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 功能：悬浮窗视图
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-25
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class SuspensionView extends FrameLayout {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    // 相对于屏幕的坐标
    private int mDownRawX, mDownRawY;
    // 相对于悬浮窗的坐标
    private int mDownX = 0, mDownY = 0;


    public SuspensionView(@NonNull Context context) {
        super(context);
        init();
    }

    public void init() {
        setBackgroundResource(R.drawable.bg_suspension_window);
        int padding = DeviceUtil.dip2px(getContext(), 1);
        setPadding(padding, padding, padding, padding);
        initWindow();
    }

    private void initWindow(){
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 适配Android o
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            mParams.type =  WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        // 设置图片格式，效果为背景透明
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.windowAnimations = R.style.FloatWindowAnimation;

        // 调整悬浮窗口至右下角
        mParams.gravity = Gravity.START | Gravity.TOP;
        // 设置悬浮窗口长宽数据
        int width = DeviceUtil.dip2px(getContext(), 250);
        mParams.width = width;
        mParams.height = width * 9 / 16;
        mParams.x = mDownX;
        mParams.y = mDownY;
    }

    /**
     * 添加至窗口
     */
    public boolean attachToWindow() {
        if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isAttachedToWindow()) {
                    mWindowManager.addView(this, mParams);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() == null) {
                        mWindowManager.addView(this, mParams);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    /**
     * 从窗口移除
     */
    public boolean detachFromWindow() {
        if (mWindowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow()) {
                    mWindowManager.removeViewImmediate(this);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() != null) {
                        mWindowManager.removeViewImmediate(this);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                mDownRawX = (int) ev.getRawX();
                mDownRawY = (int) ev.getRawY();
                mDownX = (int) ev.getX();
                mDownY = (int) (ev.getY() + DeviceUtil.getStatusBarHeight(getContext()));
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getRawX() - mDownRawX);
                float absDeltaY = Math.abs(ev.getRawY() - mDownRawY);
                intercepted = absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop();
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                mParams.x = x - mDownX;
                mParams.y = y - mDownY;
                mWindowManager.updateViewLayout(this, mParams);
                break;
        }
        return super.onTouchEvent(event);
    }
}

