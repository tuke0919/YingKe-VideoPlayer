package com.yingke.player.java.player;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.yingke.player.java.RawDataSourceProvider;
import com.yingke.player.java.player.AbstractPlayer;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 功能：真正的播放器封装
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class IjkPlayer extends AbstractPlayer {

    private Context mAppContext;
    // 真正的播放器
    protected IjkMediaPlayer mMediaPlayer;

    private boolean mIsLooping;
    private boolean mIsEnableMediaCodec;
    private int mBufferedPercent;

    public IjkPlayer(Context context){
        mAppContext = context.getApplicationContext();
    }


    @Override
    public void initPlayer() {
        mMediaPlayer = new IjkMediaPlayer();
        setOptions();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnInfoListener(mOnInfoListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
            @Override
            public boolean onNativeInvoke(int i, Bundle bundle) {
                return true;
            }
        });

    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);

    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            Uri uri = Uri.parse(path);
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())) {
                RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(mAppContext, uri);
                mMediaPlayer.setDataSource(rawDataSourceProvider);
            } else {
                mMediaPlayer.setDataSource(mAppContext, uri, headers);
            }

        } catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mMediaPlayer.setDataSource(fd.getFileDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void prepareAsync() {
        try {
            mMediaPlayer.prepareAsync();
        }catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }

    }

    @Override
    public void start() {
        try {
            mMediaPlayer.start();
        }catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void pause() {
        try {
            mMediaPlayer.pause();
        }catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void stop() {
        try {
            mMediaPlayer.stop();
        }catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void reset() {
         mMediaPlayer.reset();
         mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
         mMediaPlayer.setLooping(mIsLooping);
         setOptions();
         setEnableMediaCodec(mIsEnableMediaCodec);
    }

    @Override
    public boolean isPlaying() {
         return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long timeMs) {
        try{
            mMediaPlayer.seekTo(timeMs);
        } catch (Exception e) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError(e.getMessage());
            }
        }
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getBufferedPercent() {
        return mBufferedPercent;
    }

    @Override
    public void setVolume(float v1, float v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.mIsLooping = isLooping;
        mMediaPlayer.setLooping(isLooping);
    }

    @Override
    public void setEnableMediaCodec(boolean enableMediaCodec) {
        mIsEnableMediaCodec = enableMediaCodec;
        int value = enableMediaCodec ? 1 : 0;
        // 开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }

    @Override
    public void setOptions() {

    }

    @Override
    public void setSpeed(float speed) {
        mMediaPlayer.setSpeed(speed);
    }

    @Override
    public long getTcpSpeed() {
        return mMediaPlayer.getTcpSpeed();
    }

    /**
     * 准备
     */
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer player) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onPrepared();
            }
        }
    };

    /**
     * 缓存更新 百分比
     */
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer player, int percent) {
            mBufferedPercent = percent;
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onBufferUpdated(percent);
            }
        }
    };

    /**
     * 错误
     */
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer player, int i, int i1) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onError("ijkPlayer is onError");
            }
            return true;
        }
    };

    /**
     *
     */
    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            mPlayerEventListener.onInfo(what, extra);
            return true;
        }
    };

    /**
     * 播放完成
     */
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer player) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onCompletion();
            }
        }
    };

    /**
     * video 尺寸改变
     */
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
            }
        }
    };

    /**
     * 调整进度完成
     */
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer player) {
            if(mPlayerEventListener != null){
               mPlayerEventListener.onSeekCompletion();
            }
        }
    };






}
