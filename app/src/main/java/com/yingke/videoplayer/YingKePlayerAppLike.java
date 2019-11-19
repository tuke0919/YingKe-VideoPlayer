package com.yingke.videoplayer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.yingke.videoplayer.tinker.utils.TinkerManager;
import com.yingke.videoplayer.util.Constants;
import com.yingke.videoplayer.util.PlayerUtil;

import java.io.File;

import androidx.multidex.MultiDex;

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

// 代理生成 Application
@DefaultLifeCycle(application = "com.yingke.videoplayer.YingKePlayerApp",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class YingKePlayerAppLike extends DefaultApplicationLike {

    // Application
    private static Application mAppContext;

    public YingKePlayerAppLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplication();
        // 设置Fresco
        initFresco();

        // 获取列表网络视频的帧
        PlayerUtil.listRecVideoFrames(mAppContext);
        PlayerUtil.listRecLandVideoFrames(mAppContext);
        PlayerUtil.listTiktokVideoFrames(mAppContext);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // 分包
        MultiDex.install(base);

        // tinker配置
        TinkerManager.setTinkerLogImpl();
        TinkerManager.initFastCrashProtect();
        TinkerManager.setTinkerApplicationLike(this);
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);
        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());

    }

    public static Application getContext() {
        return mAppContext;
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
                externalCacheDir = mAppContext.getExternalCacheDir();
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
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(mAppContext)
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(mAppContext)
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
        Fresco.initialize(mAppContext, config);
    }
}
