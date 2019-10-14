package com.yingke.videoplayer.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

import wseemann.media.FFmpegMediaMetadataRetriever;

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
public class PlayerUtil {

    /**
     *  服务器返回url，通过url去获取视频的第一帧
     *  Android 原生给我们提供了一个MediaMetadataRetriever类
     *  提供了获取url视频第一帧的方法,返回Bitmap对象
     *  耗时的，考虑用线程
     *
     *  @param videoUrl
     *  @return
     */
    public static Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long startTime = System.currentTimeMillis();
        try {
            // 根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            // 获得1s图片
            bitmap = retriever.getFrameAtTime(1000 * 1000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        Log.e("getNetVideoBitmap","cost time:" + (System.currentTimeMillis() - startTime));
        return bitmap;
    }


}
