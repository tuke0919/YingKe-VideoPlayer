package com.yingke.videoplayer.util;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-14
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class EncryptUtils {

    public static final String PORT_REC_VIDEO = "port";
    public static final String LAND_REC_VIDEO = "land";
    public static final String TIKTOK_VIDEO = "tiktok";
    public static final String AD_REC_VIDEO = "ad";

    /**
     * @param string
     * @param prefix
     * @return
     */
    public static String md5String(String string, String prefix){
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes)
            {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            result = prefix + "_"+ result;
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";

    }
}
