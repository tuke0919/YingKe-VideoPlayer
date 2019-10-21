package com.yingke.videoplayer.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-14
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class StringUtil {


    public static int MINUTE = 1000 * 60;
    public static int THREE_MINUTES = MINUTE * 3;
    public static int HOUR = MINUTE * 60;
    public static int DAY = HOUR * 24;
    public static int THREE_DAYS = DAY * 3;

    public static String getTimeStrForUsrMsg(long timeStamp) {
        long curTime = System.currentTimeMillis();
        long dTime = curTime - timeStamp;
        String timeStr;
        if (dTime < THREE_MINUTES) {
            timeStr = "刚刚";
        } else if (dTime < HOUR) {
            timeStr = dTime / MINUTE + "分钟前";
        } else if (dTime < DAY) {
            timeStr = dTime / HOUR + "小时前";
        } else if (dTime < THREE_DAYS) {
            timeStr = dTime / DAY + "天前";
        } else {
            Date date = new Date(timeStamp);
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//            timeStr = formatter.format(date);
            timeStr = "";
        }
        return timeStr;
    }

    /**
     * 获取去最原始的数据信息
     * @return json data
     */
    public static String getJsonData(Context context, String jsonFileName) {
        InputStream input = null;
        try {
            // taipingyang.json文件名称
            input = context.getAssets().open(jsonFileName);
            String json = convertStreamToString(input);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * input 流转换为字符串
     *
     * @param is
     * @return
     */
    private static String convertStreamToString(InputStream is) {
        String s = null;
        try {
            // 格式转换
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) {
                s = scanner.next();
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 观看人数规则：
     * <p>
     * 1万以下直接展示数字
     * 1万以上展示xx.x万，四舍五入保留一位小数
     * 最高限制：9999.9万，超过了只展示这个数字
     */
    public static String countFormat(int count) {
        String countStr;
        if (count < 10000) {
            countStr = String.valueOf(count);
        } else if (count >= 10000 && count < 99999000) {
            countStr = removeZero1(count * 1.0f / 10000) + "万";
        } else {
            countStr = "9999.9万";
        }
        return countStr;
    }

    /**
     * 有小数保留，没有小数，显示为整数
     *
     * @param value
     * @return
     */
    public static String removeZero1(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("###.#");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(value);
    }




}
