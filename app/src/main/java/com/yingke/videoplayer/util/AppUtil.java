package com.yingke.videoplayer.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.yingke.player.java.util.PlayerLog;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-17
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class AppUtil {

    public static String getNumberVersion(Context context) {
        if (null == context) {
            PlayerLog.w("Util", "invalid input when calling getClientVersion()");
            return "";
        }
        String clientVer = "0.0.0";
        try {
            clientVer = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            PlayerLog.w("Util", "failed to get version");
        }
        if (null != clientVer) {
            clientVer = clientVer.trim();
        }
        return clientVer;
    }

    public static int getNumberCode(Context context) {
        int clientCode = 20151230;
        if (null == context) {
            PlayerLog.w("Util", "invalid input when calling getClientCode()");
            return 0;
        }
        try {
            clientCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            PlayerLog.w("Util", "failed to get version");
        }
        return clientCode;
    }

    public static String getApplicationMetaInfo(Context context, String metaName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo info;
        try {
            info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info != null && info.metaData != null) {
                return info.metaData.getString(metaName);
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

}
