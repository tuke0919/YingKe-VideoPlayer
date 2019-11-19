package com.yingke.videoplayer.tinker.request;

/**
 * 功能：返回的数据结构
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/17
 * @email xxx
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TinkerPatchInfo {

    @Deprecated
    public String appKey;
    // app的版本
    public String appVersion;
    @Deprecated
    public int patchVersion;
    // 补丁下载地址
    public String jsUrl;
    // 补丁的md5
    public String md5;
    @Deprecated
    public int publicKey;
    @Deprecated
    public String condition;

    @Override
    public String toString() {
        return "TinkerPatchInfo : \n"
                + "appVersion = " + appVersion + "\n"
                + "patchVersion = " + patchVersion + "\n"
                + "jsUrl = " + jsUrl + "\n"
                + "md5 = " + md5;

    }
}
