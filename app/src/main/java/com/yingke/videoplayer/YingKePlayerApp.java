package com.yingke.videoplayer;

import android.app.Application;
import android.content.Context;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/16
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class YingKePlayerApp extends Application {

    private static YingKePlayerApp mYingKePlayerApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mYingKePlayerApp = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static Context getContent(){
        return mYingKePlayerApp;
    }
}
