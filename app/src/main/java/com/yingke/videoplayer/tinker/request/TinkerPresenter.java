package com.yingke.videoplayer.tinker.request;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 功能：下载 咱自己服务器管理的补丁
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/17
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TinkerPresenter {
    private Context mContext;
    private TinkerModel mTinkerModel;


    public TinkerPresenter(Context context) {
        mContext = context;
        mTinkerModel = new TinkerModel(new Callback() {
            @Override
            public void onDownloadPatch(String url) {
                if (mContext != null) {
                    downloadPatch(mContext, url);
                }
            }
        });
    }

    /**
     * 请求 tinker patch
     */
    public void requestTinkerPatch(){
        if (mTinkerModel != null) {
            mTinkerModel.requestPatchUrl();
        }
    }

    /**
     * 下载热修复patch
     *
     * @param url
     */
    public void downloadPatch(Context context,String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent intent = new Intent("com.yingke.videoplayer.DOWNLOAD");
        intent.setData(Uri.parse(url));
        intent.setClass(context, TinkerDownloadService.class);
        context.startService(intent);
    }

    /**
     * 销毁
     */
    public void onDestroy(){
        if (mTinkerModel != null) {
            mTinkerModel.onDestroy();
        }
    }

    public interface Callback{

        /**
         * 下载 patch
         * @param url
         */
        void onDownloadPatch(String url);
    }

}
