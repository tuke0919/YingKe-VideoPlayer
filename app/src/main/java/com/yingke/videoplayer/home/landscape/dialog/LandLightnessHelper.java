package com.yingke.videoplayer.home.landscape.dialog;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-01
 * @email xxx
 * <p>
 * 最后修改人：无
 * <p>
 */
public class LandLightnessHelper {

    private Context mContext;
    private LightnessListener mLightnessListener;
    private ContentObserver mBrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mLightnessListener != null) {
                mLightnessListener.onLightnessChanged(getScreenBrightness());
            }
        }
    };

    public LandLightnessHelper(Context context) {
        mContext = context;
    }

    /**
     * 注册观察者
     */
    public void registerObserver() {
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true,
                mBrightnessObserver);
    }

    /**
     * 取消观察者
     */
    public void unRegisterObserver() {
        mContext.getContentResolver().unregisterContentObserver(mBrightnessObserver);
    }

    /**
     * 获得系统屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private int getScreenMode(){
        int screenMode=0;
        try{
            screenMode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException){
        }
        return screenMode;
    }

    /**
     * 获得系统屏幕亮度值 0--255
     */
    public int getScreenBrightness(){
        int screenBrightness = 255;
        try{
            screenBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException){
        }
        return screenBrightness;
    }

    /**
     * 设置系统屏幕亮度值 0--255
     * @param paramInt
     */
    public void setScreenBrightness(int paramInt){
        try{
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
        } catch (Exception localException){
            localException.printStackTrace();
            // 授权 android.permission.WRITE_SETTINGS
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(mContext)) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }

        }
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void setLightnessListener(LightnessListener listener) {
        this.mLightnessListener = listener;
    }

    /**
     * 监听器
     */
    public interface LightnessListener {
        /**
         * @param lightness
         */
        void onLightnessChanged(int lightness);
    }
}
