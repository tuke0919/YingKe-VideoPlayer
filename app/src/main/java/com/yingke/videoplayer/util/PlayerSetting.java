package com.yingke.videoplayer.util;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/21
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class PlayerSetting {

    private static boolean isMobilePlayAllowed = false;

    public static boolean isMobilePlayAllowed() {
        return isMobilePlayAllowed;
    }

    public static void setMobilePlayAllowed(boolean isMobilePlayAllowed) {
        PlayerSetting.isMobilePlayAllowed = isMobilePlayAllowed;
    }
}
