package com.yingke.videoplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.worker.WorkerCenter;
import com.yingke.videoplayer.worker.WorkerTask;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;

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
    private static final String TAG = "PlayerUtil" ;

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
//        Log.e("getNetVideoBitmap","cost time:" + (System.currentTimeMillis() - startTime));
        return bitmap;
    }


    /**
     * 获取列表网络视频的帧
     */
    public static void videoFrames(final Context context){
        String videoListJson = StringUtil.getJsonData(context, "listvideojson.json");

        List<ListVideoData> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListVideoData>>() {}.getType());
        for (final ListVideoData videoData: listVideoData) {
            // 有缓存文件
            File thumbImage = FileUtil.getVideoThumbFile(context, EncryptUtils.md5String(videoData.getUrl()));
            if (thumbImage.exists()) {
                return;
            }
            // 无缓存文件
            WorkerCenter.getInstance().submitNormalTask(new WorkerTask<String>("workTask",true){

                ListVideoData data = videoData;
                String videoUrl = data.getUrl();
                @Override
                protected String execute() {
                    // 截取网络视频帧
                    PlayerLog.e(TAG, "videoFrames: " + "title = " + data.getTitle() + "\n");

                    long startTime = System.currentTimeMillis();
                    File thumbImageFile = FileUtil.getVideoThumbFile(context, EncryptUtils.md5String(videoUrl));
                    Bitmap bitmap = PlayerUtil.getNetVideoBitmap(videoUrl);
                    FileUtil.saveBitmapToFile(bitmap, thumbImageFile.getAbsolutePath());

                    PlayerLog.e(TAG,"cost time:" + (System.currentTimeMillis() - startTime));
                    return thumbImageFile.getAbsolutePath();
                }

                @Override
                protected void notifyResult(String result) {
                    data.setThumbPath(result);
                    PlayerLog.e(TAG, "videoFrames: " + "postSticky..." );
                    // 发送粘性事件
                    EventBus.getDefault().postSticky(data);
                }
            });
        }
    }





}
