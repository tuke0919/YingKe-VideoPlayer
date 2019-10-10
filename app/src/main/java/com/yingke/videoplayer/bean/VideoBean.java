package com.yingke.videoplayer.bean;

import com.yingke.player.java.IVideoBean;

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
public class VideoBean implements IVideoBean {

    private static final String VOD_URL = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";

    @Override
    public String getSource() {
        return VOD_URL;
    }

    @Override
    public String getTitle() {
        return "标题标题标题标题标题标题标题标题标题标题";
    }
}
