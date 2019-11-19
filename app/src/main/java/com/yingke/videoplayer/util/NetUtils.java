package com.yingke.videoplayer.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yingke.videoplayer.YingKePlayerAppLike;

/**
 * <br/>网络工具类.
 * <br/>主要用于检测当前网络是否可用，检测当前网络连接类型
 *
 */
public class NetUtils {
    public static final String TAG = "NetUtils";

    /**
     * 检测是否可以连网.
     *
     * @param context
     * @return
     */
    public static boolean isAvailable(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 检测是否是 移动网络
     * @return
     */
    public static boolean isMobileNetwork() {
        ConnectivityManager cm = (ConnectivityManager) YingKePlayerAppLike.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 检测是否是wifi连网.
     *
     * @param context
     * @return
     */
    public static boolean isWIFI(Context context) {
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回ConnectivityManager.
     *
     * @param context
     * @return
     */
    private static ConnectivityManager getConnectivityManager(Context context) {
        if (context == null) {
            return null;
        }
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivity;
    }
}
