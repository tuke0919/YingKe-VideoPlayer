package com.yingke.videoplayer.home;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static com.yingke.videoplayer.home.TopTabData.TabType.TAB_COURSE_MENU;
import static com.yingke.videoplayer.home.TopTabData.TabType.TAB_OTHER;
import static com.yingke.videoplayer.home.TopTabData.TabType.TAB_RECOMMEND;
import static com.yingke.videoplayer.home.TopTabData.TabType.TAB_SHORT_VIDEO;
import static com.yingke.videoplayer.home.TopTabData.TabType.TAB_SUBSCRIBE;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/19
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TopTabData {

    @IntDef({TAB_OTHER, TAB_SUBSCRIBE, TAB_RECOMMEND, TAB_SHORT_VIDEO,TAB_COURSE_MENU })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabType {
        // 其他
        int TAB_OTHER = -1;
        // 关注
        int TAB_SUBSCRIBE = 0;
        // 推荐
        int TAB_RECOMMEND = 1;
        // 小视频
        int TAB_SHORT_VIDEO = 2;
        // 课单
        int TAB_COURSE_MENU = 3;
    }

    public TopTabData(int type, String name) {
        this.type = type;
        this.name = name;
    }

    // 数据id
    private int id;
    // 名称
    private String name;
    // 权重
    private int weight;
    // 类型
    private int type;
    // 字体颜色
    private String fontColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 自定义
     * @param type
     * @param name
     * @return
     */
    public static TopTabData newTabData(@TabType int type, String name){
        return new TopTabData(type, name);
    }




}
