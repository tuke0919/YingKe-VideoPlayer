package com.yingke.videoplayer.util;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/21
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class PlayerSetting {

    private static boolean isMobilePlayAllowed = false;

    public static boolean is4GPlayAllowed() {
        return isMobilePlayAllowed;
    }

    public static void set4GPlayAllowed(boolean isMobilePlayAllowed) {
        PlayerSetting.isMobilePlayAllowed = isMobilePlayAllowed;
    }
}
