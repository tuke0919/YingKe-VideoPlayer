package com.yingke.videoplayer.main;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/20
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class BottomTabData {

    public String mTag;
    public Class mFragClass;
    public int mInImageId;
    public int mOutImageId;
    public int mTextId;

    public BottomTabData(String tag, Class fragClass, int inImageId, int outImageId, int textId) {
        mTag = tag;
        mFragClass = fragClass;
        mInImageId = inImageId;
        mOutImageId = outImageId;
        mTextId = textId;
    }

    public String getTag() {
        return mTag;
    }

    public Class getFragClass() {
        return mFragClass;
    }

    public int getTextId() {
        return mTextId;
    }

    public int getInImageId() {
        return mInImageId;
    }

    public int getOutImageId() {
        return mOutImageId;
    }
}
