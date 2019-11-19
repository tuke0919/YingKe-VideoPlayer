package com.yingke.player.java.manager;

import com.yingke.player.java.videoview.IjkBaseVideoView;

/**
 * 功能：视频播放器管理器，需要配合addToPlayerManager()使用
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class MediaPlayerManager {

    public IjkBaseVideoView mIjkBaseVideoView;

    public static MediaPlayerManager instance(){
        return A.INSTANCE;
    }

    private static class A{
        public static final MediaPlayerManager INSTANCE = new MediaPlayerManager();
    }

    public void setIjkBaseVideoView(IjkBaseVideoView ijkBaseVideoView) {
        mIjkBaseVideoView = ijkBaseVideoView;
    }

    public void releaseVideoPlayer() {
        if (mIjkBaseVideoView != null) {
            mIjkBaseVideoView.release();
            mIjkBaseVideoView = null;
        }
    }




}
