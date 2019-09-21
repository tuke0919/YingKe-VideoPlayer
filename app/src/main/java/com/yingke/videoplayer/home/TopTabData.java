package com.yingke.videoplayer.home;

import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

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
    public static TopTabData newTabData(int type, String name){
        return new TopTabData(type, name);
    }

    public static String mTopDataJson = "[\n" +
            "    {\n" +
            "        \"id\": 1222,\n" +
            "        \"name\": \"影视\",\n" +
            "        \"weight\": 98,\n" +
            "        \"type\": 67,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1223,\n" +
            "        \"name\": \"游戏\",\n" +
            "        \"weight\": 97,\n" +
            "        \"type\": 32,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1224,\n" +
            "        \"name\": \"小视频\",\n" +
            "        \"weight\": 96,\n" +
            "        \"type\": 34,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1225,\n" +
            "        \"name\": \"VLOG\",\n" +
            "        \"weight\": 95,\n" +
            "        \"type\": 111,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1226,\n" +
            "        \"name\": \"70年\",\n" +
            "        \"weight\": 94,\n" +
            "        \"type\": 21,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1227,\n" +
            "        \"name\": \"音乐\",\n" +
            "        \"weight\": 93,\n" +
            "        \"type\": 43,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1228,\n" +
            "        \"name\": \"综艺\",\n" +
            "        \"weight\": 92,\n" +
            "        \"type\": 54,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1229,\n" +
            "        \"name\": \"美食\",\n" +
            "        \"weight\": 91,\n" +
            "        \"type\": 65,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1230,\n" +
            "        \"name\": \"宠物\",\n" +
            "        \"weight\": 90,\n" +
            "        \"type\": 988,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1231,\n" +
            "        \"name\": \"宠物\",\n" +
            "        \"weight\": 89,\n" +
            "        \"type\": 13,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1231,\n" +
            "        \"name\": \"儿童\",\n" +
            "        \"weight\": 88,\n" +
            "        \"type\": 88,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1232,\n" +
            "        \"name\": \"情感\",\n" +
            "        \"weight\": 87,\n" +
            "        \"type\": 567,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  },\n" +
            "   {\n" +
            "        \"id\": 1233,\n" +
            "        \"name\": \"NBA\",\n" +
            "        \"weight\": 86,\n" +
            "        \"type\": 76,\n" +
            "        \"fontcolor\": \"\"  \n" +
            "  }\n" +
            "]";









}
