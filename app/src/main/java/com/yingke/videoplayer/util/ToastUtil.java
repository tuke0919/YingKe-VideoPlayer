package com.yingke.videoplayer.util;

import android.widget.Toast;

import com.yingke.videoplayer.YingKePlayerAppLike;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-27
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ToastUtil {

    public static void showToast(String text){
        Toast.makeText(YingKePlayerAppLike.getContext(),text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast( int textId){
        Toast.makeText(YingKePlayerAppLike.getContext(), textId, Toast.LENGTH_SHORT).show();
    }
}
