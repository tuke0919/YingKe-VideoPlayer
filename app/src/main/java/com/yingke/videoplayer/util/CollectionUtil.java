package com.yingke.videoplayer.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/19
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class CollectionUtil {

    /**
     * 集合 空
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * map 空
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 数组 空
     * @param array
     * @return
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * List 空
     * @param list
     * @return
     */
    public static boolean isEmpty(List<Object> list) {
        return list == null || list.size() == 0;
    }

}
