package com.yingke.videoplayer.util;

import android.os.Environment;

import com.facebook.common.util.ByteConstants;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-14
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class Constants {

    // 常量
    public static final int MAX_MEM = 15 * ByteConstants.MB;
    public static final int MAX_CACHE_SIZE = Integer.MAX_VALUE;

    // 存储路径
    public static final String TMP_IMG_DIR = "TMP_IMG_DIR";
    public static final String FILE_SDCARD_BASE = Environment.getExternalStorageDirectory().toString() + "/yingke/video/";

}
