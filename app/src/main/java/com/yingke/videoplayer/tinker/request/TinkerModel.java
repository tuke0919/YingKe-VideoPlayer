package com.yingke.videoplayer.tinker.request;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yingke.player.java.util.PlayerLog;
import com.yingke.videoplayer.YingKePlayerAppLike;
import com.yingke.videoplayer.tinker.utils.TinkerUtils;
import com.yingke.videoplayer.util.AppUtil;

/**
 * 功能：tinker 热修复
 *
 *</p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/17
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TinkerModel {
    public static final String TAG = "TinkerModel";


    public static final int TINKER_REQUEST_CODE = 1000;

    public TinkerPresenter.Callback mCallback;

    public TinkerModel(TinkerPresenter.Callback callback) {
        mCallback = callback;
    }

    /**
     * 请求patch的Url
     */
    public void requestPatchUrl(){

    }

    /**
     * 销毁
     */
    public void onDestroy(){
        if (mCallback != null) {
            mCallback = null;
        }
    }

    /**
     * 假设 这是请求补丁的结果
     * @param i
     * @param bundle
     * @param code
     */
    public void networkCallBack(int i, Bundle bundle, int code) {

        String json = "";
        if (i == TINKER_REQUEST_CODE && code == 200 ) {
            TinkerPatchInfo patchInfo = getBean(json, TinkerPatchInfo.class);
            if (patchInfo == null) {
                return;
            }
            String versionName = AppUtil.getNumberVersion(YingKePlayerAppLike.getContext());
            PlayerLog.e(TAG, "versionName = " + versionName + " " + patchInfo.toString());
            if (!TextUtils.equals("0.0.0", versionName)) {
                String patchMd5 = TinkerUtils.getComposedOrInstalledPatchMd5();
                if (!TextUtils.equals(patchMd5, patchInfo.md5)) {
                    // 不一样需要下载
                    PlayerLog.e(TAG, "tinker patch url is not same, need download \n " + patchInfo.toString());
                    // 下载
                    String downloadUrl = patchInfo.jsUrl;
                    if (mCallback != null) {
                        mCallback.onDownloadPatch(downloadUrl);
                    }
                } else {
                    // 一样，不下载
                    PlayerLog.e(TAG, "tinker patch url is same, not need download \n" + patchInfo.toString());
                }
            }
        }
    }

    /**
     * json -> bean
     * @param json
     * @param t
     * @param <T>
     * @return
     */
    public <T> T getBean(String json, Class<T> t) {
        T bean = null;

        try {
            bean = new Gson().fromJson(json, t);
        } catch (Exception var4) {
            var4.printStackTrace();
            bean = null;
        }

        return bean;
    }

}
