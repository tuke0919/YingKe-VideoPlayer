package com.yingke.player.java.manager;

/**
 * 功能：进度管理器，可以是内存保存，也可以是数据库保存
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class ProgressManager {

    /**
     * 保存进度
     */
    public abstract void saveProgress(String url, long progress);

    /**
     * 获取进度
     */
    public abstract long getSavedProgress(String url);


}
