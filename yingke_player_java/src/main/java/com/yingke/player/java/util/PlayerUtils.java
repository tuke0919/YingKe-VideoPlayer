package com.yingke.player.java.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class PlayerUtils {

    /**
     * 获取Activity
     */
    public static Activity scanForActivity(Context context) {
        return context == null ? null : (context instanceof Activity ? (Activity) context : (context instanceof ContextWrapper ? scanForActivity(((ContextWrapper) context).getBaseContext()) : null));
    }

    /**
     * 获取View 所在的Activity
     * @param view
     * @return
     */
    public static Activity getAttachedActivity(View view) {
        if (view == null) {
            return null;
        }
        View decorView = view.getRootView();
        if (decorView == null){
            throw new IllegalArgumentException("BaseListVideoView attached rootView is null");
        }

        // 用父布局的Activity
        Context context = ((ViewGroup)decorView).findViewById(android.R.id.content).getContext();
        Activity activity = PlayerUtils.scanForActivity(context);
        if (activity == null){
            throw new IllegalArgumentException("BaseListVideoView attached activity is null");
        }
        return activity;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
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
