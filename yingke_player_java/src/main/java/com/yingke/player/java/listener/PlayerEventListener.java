package com.yingke.player.java.listener;

/**
 * 功能：播放器事件回调
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 */
public interface PlayerEventListener {

    /**
     * 准备
     */
    void onPrepared();

    /**
     * 缓存更新 百分比
     * @param percent
     */
    void onBufferUpdated(int percent);

    /**
     * 错误
     * @param message
     */
    void onError(String message);

    /**
     * 其他信息
     * @param what
     * @param extra
     */
    void onInfo(int what, int extra);

    /**
     * 播放完成
     */
    void onCompletion();

    /**
     * 调整进度完成
     */
    void onSeekCompletion();

    /**
     * video 尺寸改变
     * @param width
     * @param height
     */
    void onVideoSizeChanged(int width, int height);



}
