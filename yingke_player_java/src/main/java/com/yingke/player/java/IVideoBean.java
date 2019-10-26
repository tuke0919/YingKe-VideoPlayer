package com.yingke.player.java;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/23
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class IVideoBean {

    public static final int TYPE_AD = 0;
    public static final int TYPE_REAL = 1;

    public int mCurrentType = TYPE_REAL;

    /**
     * @return 当前播放的类型
     */
    public int getCurrentType(){
        return mCurrentType;
    }

    /**
     * @param currentType
     */
    public void setCurrentType(int currentType) {
        mCurrentType = currentType;
    }

    /**
     * @return 首选播放的类型
     */
    public int getFirstType(){
        return mCurrentType;
    }

    /**
     * 真实链接、广告链接
     * @return
     */
    public abstract String getSource();

    /**
     * 标题
     * @return
     */
    public abstract String getTitle();
}
