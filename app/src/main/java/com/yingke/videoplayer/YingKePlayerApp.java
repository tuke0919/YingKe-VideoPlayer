package com.yingke.videoplayer;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.yingke.videoplayer.util.Constants;
import com.yingke.videoplayer.util.PlayerUtil;

import java.io.File;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/16
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class YingKePlayerApp extends Application {

    private static YingKePlayerApp mYingKePlayerApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mYingKePlayerApp = this;
        initFresco();

        // 获取列表网络视频的帧
        PlayerUtil.videoFrames(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static Context getContext(){
        return mYingKePlayerApp;
    }


    /**
     * 设置Fresco
     */
    private void initFresco() {
        String path = null;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            path = Constants.FILE_SDCARD_BASE;
        } else {
            File externalCacheDir = null;
            try {
                externalCacheDir = getContext().getExternalCacheDir();
            } catch (Exception e) {
            }
            if (externalCacheDir != null) {
                // 这里会出现null的情况
                path = externalCacheDir.getAbsolutePath();
            } else {
                path = Constants.FILE_SDCARD_BASE;
            }
        }
        final MemoryCacheParams memoryCacheParams = new MemoryCacheParams(Constants.MAX_MEM,
                Constants.MAX_CACHE_SIZE,
                Constants.MAX_MEM,
                Constants.MAX_CACHE_SIZE,
                Constants.MAX_CACHE_SIZE);
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return memoryCacheParams;
            }
        };
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getContext())
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(getContext())
                        .setBaseDirectoryPath(new File(path))
                        .setBaseDirectoryName(Constants.TMP_IMG_DIR)
                        .setMaxCacheSize(50 * ByteConstants.MB)// 默认缓存的最大大小。
                        .setMaxCacheSizeOnLowDiskSpace(20 * ByteConstants.MB)// 缓存的最大大小,使用设备时低磁盘空间。
                        .setMaxCacheSizeOnVeryLowDiskSpace(10 * ByteConstants.MB)// 缓存的最大大小,当设备极低磁盘空间
                        .build())
                .setProgressiveJpegConfig(new ProgressiveJpegConfig() {
                    @Override
                    public int getNextScanNumberToDecode(int i) {
                        return i + 3;
                    }

                    @Override
                    public QualityInfo getQualityInfo(int i) {
                        boolean isGoodEnough = (i >= 2);
                        return ImmutableQualityInfo.of(i, isGoodEnough, false);
                    }
                })
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setDownsampleEnabled(true)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                .setResizeAndRotateEnabledForNetwork(true)
                .build();
        Fresco.initialize(getContext(), config);
    }

}
