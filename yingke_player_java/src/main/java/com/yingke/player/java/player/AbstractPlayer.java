package com.yingke.player.java.player;

import android.content.res.AssetFileDescriptor;
import android.support.annotation.IntDef;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.yingke.player.java.listener.PlayerEventListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class AbstractPlayer {
    /**
     * 开始渲染视频画面
     */
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;

    /**
     * 缓冲开始
     */
    public static final int MEDIA_INFO_BUFFERING_START = 701;

    /**
     * 缓冲结束
     */
    public static final int MEDIA_INFO_BUFFERING_END = 702;

    /**
     * 视频旋转信息
     */
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;


    @IntDef({MEDIA_INFO_VIDEO_RENDERING_START,
            MEDIA_INFO_BUFFERING_START,
            MEDIA_INFO_BUFFERING_END,
            MEDIA_INFO_VIDEO_ROTATION_CHANGED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaInfo{

    }

    /**
     * 播放器事件回调
     */
    protected PlayerEventListener mPlayerEventListener;

    /**
     * 初始化
     */
    public abstract void initPlayer();

    /**
     * 设置渲染视频的View,主要用于TextureView
     * @param surface
     */
    public abstract void setSurface(Surface surface);

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     * @param surfaceHolder
     */
    public abstract void setDisplay(SurfaceHolder surfaceHolder);

    /**
     * 设置播放地址
     * @param path
     * @param headers
     */
    public abstract void setDataSource(String path, Map<String, String> headers);

    /**
     * 用于播放raw和asset里面的视频文件
     * @param fd
     */
    public abstract void setDataSource(AssetFileDescriptor fd);

    /**
     * 准备开始播放（异步）
     */
    public abstract void prepareAsync();

    /**
     * 开始
     */
    public abstract void start();

    /**
     * 暂停
     */
    public abstract void pause();

    /**
     * 停止
     */
    public abstract void stop();

    /**
     * 重置
     */
    public abstract void reset();

    /**
     * 是否正在播放
     */
    public abstract boolean isPlaying();

    /**
     * 调整进度
     * @param timeMs ms
     */
    public abstract void seekTo(long timeMs);

    /**
     * 释放资源
     */
    public abstract void release();

    /**
     * @return 获取当前播放 位置 ms
     */
    public abstract long getCurrentPosition();

    /**
     * @return 获取视频总时长 ms
     */
    public abstract long getDuration();

    /**
     * @return 获取 缓冲百分比
     */
    public abstract int getBufferedPercent();

    /**
     * 设置音量
     */
    public abstract void setVolume(float v1, float v2);

    /**
     * 设置是否循环播放
     * @param isLooping
     */
    public abstract void setLooping(boolean isLooping);

    /**
     * 设置是否开启 硬解码
     * @param enableMediaCodec
     */
    public abstract void setEnableMediaCodec(boolean enableMediaCodec);

    /**
     * 设置其他播放配置
     */
    public abstract void setOptions();

    /**
     * 设置播放速度
     * @param speed
     */
    public abstract void setSpeed(float speed);

    /**
     * @return 设置当前缓存的网速
     */
    public abstract long getTcpSpeed();

    /**
     * 设置 播放时间回调
     * @param playerEventListener
     */
    public void setPlayerEventListener(PlayerEventListener playerEventListener) {
        mPlayerEventListener = playerEventListener;
    }

}
