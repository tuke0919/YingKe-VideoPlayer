package com.yingke.videoplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.util.PlayerLog;
import com.yingke.videoplayer.home.ListVideoStickEvent;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.tiktok.bean.ListTiktokBean;
import com.yingke.videoplayer.worker.WorkerCenter;
import com.yingke.videoplayer.worker.WorkerTask;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.yingke.videoplayer.util.EncryptUtils.AD_REC_VIDEO;
import static com.yingke.videoplayer.util.EncryptUtils.LAND_REC_VIDEO;
import static com.yingke.videoplayer.util.EncryptUtils.PORT_REC_VIDEO;
import static com.yingke.videoplayer.util.EncryptUtils.TIKTOK_VIDEO;

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
     *  @param firstFrame 是否第一帧
     * @return
     */
    public static Bitmap getNetVideoBitmap(String videoUrl, boolean firstFrame) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long startTime = System.currentTimeMillis();
        try {
            // 根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            if (firstFrame) {
                // 100ms
                bitmap = retriever.getFrameAtTime(100 * 1000);
            } else {
                // 1s
                bitmap = retriever.getFrameAtTime(1000 * 1000);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }


    /**
     * 推荐列表网络视频的帧
     */
    public static void listRecVideoFrames(final Context context){
        String videoListJson = StringUtil.getJsonData(context, "list_rec_video.json");

        List<ListVideoData> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListVideoData>>() {}.getType());
        for (final ListVideoData videoData: listVideoData) {
            // 有缓存文件
            File thumbImage = FileUtil.getVideoThumbFile(context, videoData.getUrl(), PORT_REC_VIDEO);
            if (!thumbImage.exists()) {
                // 无缓存文件
                WorkerCenter.getInstance().submitNormalTask(new WorkerTask<String>("workTask",true){

                    ListVideoData data = videoData;
                    String videoUrl = data.getUrl();
                    @Override
                    protected String execute() {
                        // 截取网络视频帧
                        PlayerLog.e(TAG, "listRecVideoFrames: " + "title = " + data.getTitle() + "\n");

                        long startTime = System.currentTimeMillis();
                        File thumbImageFile = FileUtil.getVideoThumbFile(context, videoUrl, PORT_REC_VIDEO);

                        Bitmap bitmap = PlayerUtil.getNetVideoBitmap(videoUrl, false);
                        FileUtil.saveBitmapToFile(bitmap, thumbImageFile.getAbsolutePath());

                        PlayerLog.e(TAG,"cost time:" + (System.currentTimeMillis() - startTime));
                        return thumbImageFile.getAbsolutePath();
                    }

                    @Override
                    protected void notifyResult(String result) {
                        data.setThumbPath(result);
                        // 发送粘性事件
                        EventBus.getDefault().postSticky(new ListVideoStickEvent(data, false));
                    }
                });
            }

            if (!TextUtils.isEmpty(videoData.getAdUrl())) {

                File thumbAdImage =  FileUtil.getVideoThumbFile(context, videoData.getAdUrl(), AD_REC_VIDEO);
                if (!thumbAdImage.exists()) {
                    // 无缓存文件
                    WorkerCenter.getInstance().submitNormalTask(new WorkerTask<String>("workTask1",true){

                        ListVideoData data = videoData;
                        String videoUrl = data.getAdUrl();
                        @Override
                        protected String execute() {
                            // 截取网络视频帧
                            PlayerLog.e(TAG, "listRecAdVideoFrames: " + "title = " + data.getTitle() + "\n");

                            long startTime = System.currentTimeMillis();
                            File thumbImageFile = FileUtil.getVideoThumbFile(context, videoUrl, AD_REC_VIDEO);
                            Bitmap bitmap = PlayerUtil.getNetVideoBitmap(videoUrl, true);
                            FileUtil.saveBitmapToFile(bitmap, thumbImageFile.getAbsolutePath());

                            PlayerLog.e(TAG,"cost time:" + (System.currentTimeMillis() - startTime));
                            return thumbImageFile.getAbsolutePath();
                        }

                        @Override
                        protected void notifyResult(String result) {
                            data.setAdThumbPath(result);
                            // 发送粘性事件
                            EventBus.getDefault().postSticky(new ListVideoStickEvent(data, true));
                        }
                    });
                }
            }

        }
    }

    /**
     * 推荐列表 横屏网络视频的帧
     */
    public static void listRecLandVideoFrames(final Context context){
        String videoListJson = StringUtil.getJsonData(context, "list_rec_land_video.json");

        List<ListVideoData> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListVideoData>>() {}.getType());
        for (final ListVideoData videoData: listVideoData) {
            // 有缓存文件
            File thumbImage = FileUtil.getVideoThumbFile(context, videoData.getUrl(), LAND_REC_VIDEO);
            if (!thumbImage.exists()) {
                // 无缓存文件
                WorkerCenter.getInstance().submitNormalTask(new WorkerTask<String>("workTask_land",true){

                    ListVideoData data = videoData;
                    String videoUrl = data.getUrl();
                    @Override
                    protected String execute() {
                        // 截取网络视频帧
                        PlayerLog.e(TAG, "listRecLandVideoFrames: " + "title = " + data.getTitle() + "\n");

                        long startTime = System.currentTimeMillis();
                        File thumbImageFile = FileUtil.getVideoThumbFile(context, videoUrl, LAND_REC_VIDEO);
                        Bitmap bitmap = PlayerUtil.getNetVideoBitmap(videoUrl, false);
                        FileUtil.saveBitmapToFile(bitmap, thumbImageFile.getAbsolutePath());

                        PlayerLog.e(TAG,"cost time:" + (System.currentTimeMillis() - startTime));
                        return thumbImageFile.getAbsolutePath();
                    }

                    @Override
                    protected void notifyResult(String result) {
                        data.setThumbPath(result);
                    }
                });
            }
        }
    }


    /**
     * 抖音列表网络视频的帧
     */
    public static void listTiktokVideoFrames(final Context context){
        String videoListJson = StringUtil.getJsonData(context, "list_tiktok_video.json");

        List<ListTiktokBean> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListTiktokBean>>() {}.getType());
        for (final ListTiktokBean videoData: listVideoData) {
            // 有缓存文件
            File thumbImage = FileUtil.getVideoThumbFile(context, videoData.getUrl(), TIKTOK_VIDEO);
            if (thumbImage.exists()) {
                return;
            }
            // 无缓存文件
            WorkerCenter.getInstance().submitNormalTask(new WorkerTask<String>("workTask_tiktok",true){

                ListTiktokBean data = videoData;
                String videoUrl = data.getUrl();
                @Override
                protected String execute() {
                    // 截取网络视频帧
                    PlayerLog.e(TAG, "listTiktokVideoFrames: " + "title = " + data.getUserName() + "\n");

                    long startTime = System.currentTimeMillis();
                    File thumbImageFile = FileUtil.getVideoThumbFile(context, videoUrl, TIKTOK_VIDEO);
                    Bitmap bitmap = PlayerUtil.getNetVideoBitmap(videoUrl, true);
                    FileUtil.saveBitmapToFile(bitmap, thumbImageFile.getAbsolutePath());

                    PlayerLog.e(TAG,"cost time:" + (System.currentTimeMillis() - startTime));
                    return thumbImageFile.getAbsolutePath();
                }

                @Override
                protected void notifyResult(String result) {
                    // 设置封面路径
                    data.setCoverImage(result);
                    // 发送粘性事件
                    EventBus.getDefault().postSticky(data);
                }
            });
        }
    }






}
