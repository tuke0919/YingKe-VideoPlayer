package com.yingke.videoplayer.util.share;

import android.app.Activity;

import com.yingke.videoplayer.YingKePlayerAppLike;

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
public class PlayerSpUtil {

    private static SPUtils share = new SPUtils(YingKePlayerAppLike.getContext());

    // tinker的sp
    private static String FILE_TINKER_PATCH = "file_tinker_patch";

    /**
     * 保存版本对应的patch的版本 md5
     *
     * @param versionName
     * @param patchVersion patchmd5
     */
    public static void saveTinkerPatchVersion(String versionName, String patchVersion) {
        share.setStringForShare(FILE_TINKER_PATCH, versionName, patchVersion);
    }

    /**
     * 获取tinker patch的版本 md5
     *
     * @param versionName
     * @return
     */
    public static String getTinkerPatchVersion(String versionName) {
        return share.getStringForShare(FILE_TINKER_PATCH, versionName, null);
    }

}
