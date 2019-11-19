package com.yingke.videoplayer.util.share;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
public class SPUtils {

    Context mContext;
    SharedPreferences sharedPreferences;

    public SPUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * set string
     * @param shareName
     * @param key
     * @param value
     */
    public void setStringForShare(String shareName, String key, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * get string
     *
     * @param shareName
     * @param key
     * @return
     */
    public String getStringForShare(String shareName, String key) {
        sharedPreferences = mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * get string
     *
     * @param shareName
     * @param key
     * @param defultValue
     * @return
     */
    public String getStringForShare(String shareName, String key, String defultValue) {
        sharedPreferences = mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, defultValue);
    }


    /**
     * set int
     * @param shareName
     * @param key
     * @param value
     */
    public void setIntForShare(String shareName, String key, int value){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * get int
     *
     * @param shareName
     * @param key
     * @param value
     * @return
     */
    public int getIntForShare(String shareName, String key,int value){
        sharedPreferences = mContext.getSharedPreferences(shareName,Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(key, value);
    }

    /**
     * set long
     *
     * @param shareName
     * @param key
     * @param value
     */
    public void setLongForShare(String shareName, String key, long value){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * get long
     *
     *
     * @param shareName
     * @param key
     * @param value
     * @return
     */
    public long getLongForShare(String shareName, String key, long value){
        sharedPreferences = mContext.getSharedPreferences(shareName,Activity.MODE_PRIVATE);
        return sharedPreferences.getLong(key, value);
    }


    /**
     * set boolean
     *
     * @param shareName
     * @param key
     * @param value
     */
    public void setBooleanForShare(String shareName, String key, boolean value){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(shareName, Activity.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * get boolean
     *
     * @param shareName
     * @param key
     * @return
     */
    public boolean getBooleanForShare(String shareName, String key){
        sharedPreferences = mContext.getSharedPreferences(shareName,Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * get boolean
     *
     * @param shareName
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBooleanForShare(String shareName, String key, boolean defValue){
        sharedPreferences = mContext.getSharedPreferences(shareName,Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defValue);
    }

}
