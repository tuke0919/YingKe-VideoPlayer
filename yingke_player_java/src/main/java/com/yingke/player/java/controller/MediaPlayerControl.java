package com.yingke.player.java.controller;

import android.graphics.Bitmap;

/**
 * 功能：播放器控制视图 控制 真实播放器的 接口
 *   是控制器对播放器的操作和控制
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 */
public interface MediaPlayerControl {

    /**
     * 开始/恢复
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stopPlayback();

    /**
     * 释放资源
     */
    void release();

    /**
     * 总时长 ms
     * @return
     */
    long getDuration();

    /**
     * 当前播放位置
     * @return
     */
    long getCurrentPosition();

    /**
     * 调整进度
     * @param pos
     */
    void seekTo(long pos);

    /**
     * 正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 是否是 播放状态 不同于正在播放
     * @return
     */
    boolean isInPlaybackState();

    /**
     * 缓存比例
     * @return
     */
    int getBufferedPercentage();


    /**
     * 设置静音
     * @param isMute
     */
    void setMute(boolean isMute);

    /**
     * 是否静音
     * @return
     */
    boolean isMute();

    /**
     * 设置 锁屏
     * @param isLocked
     */
    void setLock(boolean isLocked);

    /**
     * 设置 屏幕缩放
     * @param screenScale
     */
    void setScreenScale(int screenScale);

    /**
     * 设置倍速
     * @param speed
     */
    void setSpeed(float speed);

    /**
     * 获取 缓存网络速度
     * @return
     */
    long getTcpSpeed();

    /**
     * 重播
     * @param resetPosition
     */
    void replay(boolean resetPosition);

    /**
     * 设置镜像旋转
     * @param enable
     */
    void setMirrorRotation(boolean enable);

    /**
     * 截屏，视频帧
     * @return
     */
    Bitmap doScreenShot();

    /**
     * 获取video尺寸
     * @return
     */
    int[] getVideoSize();

    /**
     * 设置旋转
     * @param rotation
     */
    void setRotation(float rotation);

    /**
     * 开始小屏
     */
    @Deprecated
    void startTinyScreen();

    /**
     * 结束小屏
     */
    @Deprecated
    void stopTinyScreen();

    /**
     * 是否小屏播放
     * @return
     */
    @Deprecated
    boolean isTinyScreen();


}
