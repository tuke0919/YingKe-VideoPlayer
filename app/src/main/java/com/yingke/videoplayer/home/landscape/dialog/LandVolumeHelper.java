package com.yingke.videoplayer.home.landscape.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.lang.ref.WeakReference;

/**
 * 功能：媒体音量监听类
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-01
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class LandVolumeHelper {

    private static final String ACTION_VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    private Context mContext;
    private AudioManager mAudioManager;
    private VolumeReceiver mVolumeReceiver;
    private VolumeListener mVolumeListener;

    public LandVolumeHelper(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * @return 当前音量
     */
    public int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置音量
     * @param volume
     */
    public void setCurrVolume(int volume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    /**
     * @return 最大音量
     */
    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 注册广播
     */
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_VOLUME_CHANGED);
        mVolumeReceiver = new VolumeReceiver(this);
        mContext.registerReceiver(mVolumeReceiver, intentFilter);
    }

    /**
     * 注销广播
     */
    public void unregisterReceiver() {
        if (mVolumeReceiver != null) {
            mContext.unregisterReceiver(mVolumeReceiver);
        }
        mVolumeListener = null;
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void setVolumeListener(VolumeListener listener) {
        this.mVolumeListener = listener;
    }

    /**
     * 监听器
     */
    public interface VolumeListener {
        /**
         * @param volume
         */
        void onVolumeChanged(int volume);
    }

    /**
     * 广播
     */
    private static class VolumeReceiver extends BroadcastReceiver {

        private WeakReference<LandVolumeHelper> mObserver;

        VolumeReceiver(LandVolumeHelper observer) {
            mObserver = new WeakReference<>(observer);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mObserver == null && mObserver.get().mVolumeListener == null){
                return;
            }

            if (isReceiveVolumeChange(intent)) {
                VolumeListener listener = mObserver.get().mVolumeListener;
                if (listener != null) {
                    listener.onVolumeChanged(mObserver.get().getCurrentVolume());
                }
            }
        }

        /**
         * 媒体音量改变
         * @param intent
         * @return
         */
        private boolean isReceiveVolumeChange(Intent intent) {
            return intent.getAction() != null
                    && intent.getAction().equals(ACTION_VOLUME_CHANGED)
                    && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC;
        }
    }
}
