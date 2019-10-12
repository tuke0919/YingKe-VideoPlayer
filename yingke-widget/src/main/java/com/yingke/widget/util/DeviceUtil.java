package com.yingke.widget.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class DeviceUtil {
    private static final String TAG = "DeviceUtil";
    private static String USER_AGENT_SUFFIX;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static int STATUS_BAR_HEIGHT;

    /***
     * 获取屏幕宽度像素
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /***
     * 获取屏幕高度像素
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /***
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDeviceWidth(Context context) {
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            metrics = context.getResources().getDisplayMetrics();
            return metrics.widthPixels;
        } catch (Exception e) {
            return 851;
        }
    }

    /***
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDeviceHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /***
     * 获取屏幕尺寸
     *
     * @param context
     * @return width x heigth
     */
    public static String getDeviceSize(Context context) {
        return getDeviceWidth(context) + "x" + getDeviceHeight(context);
    }


    /**
     * 状态栏高度
     */
    public static int getStatusBarHeight(Activity mContext) {
        if (STATUS_BAR_HEIGHT != 0) {
            return STATUS_BAR_HEIGHT;
        }
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            STATUS_BAR_HEIGHT = mContext.getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return STATUS_BAR_HEIGHT;
    }

    /**
     * 除状态栏标题栏的屏幕高度
     */
    public static int getAppInnerHeight(Activity mContext) {
        return DeviceUtil.SCREEN_HEIGHT - getStatusBarHeight(mContext)
                - DeviceUtil.dip2px(mContext, 46);
    }

    /**
     * dip转pixel
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale);
    }

    /**
     * pixel转dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}

