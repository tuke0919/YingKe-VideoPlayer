package com.yingke.player.java.listener;

import com.yingke.player.java.videoview.IjkBaseVideoView;

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
public interface OnPlayStateListener {

    /**
     * 屏幕模式 改变
     * @param screenMode {@link IjkBaseVideoView#SCREEN_MODE_NORMAL}
     *                   {@link IjkBaseVideoView#SCREEN_MODE_FULL}
     *                   {@link IjkBaseVideoView#SCREEN_MODE_TINY}
     */
    void onScreenModeChanged(int screenMode);

    /**
     * 播放状态改变
     * @param playState {@link IjkBaseVideoView#STATE_ERROR}
     *                  {@link IjkBaseVideoView#STATE_IDLE}
     *                  {@link IjkBaseVideoView#STATE_PREPARING}
     *                  {@link IjkBaseVideoView#STATE_PREPARED}
     *                  {@link IjkBaseVideoView#STATE_PLAYING}
     *                  {@link IjkBaseVideoView#STATE_PAUSED}
     *                  {@link IjkBaseVideoView#STATE_PLAYBACK_COMPLETED}
     *                  {@link IjkBaseVideoView#STATE_BUFFERING}
     *                  {@link IjkBaseVideoView#STATE_BUFFERED}
     *
     */
    void onPlayStateChanged(int playState);
}
