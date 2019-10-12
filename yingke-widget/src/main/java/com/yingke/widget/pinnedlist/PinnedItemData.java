package com.yingke.widget.pinnedlist;

/**
 * 数据item
 */
public class PinnedItemData<T> {

    // tpye 普通item，不悬停
    public static final int ITEM_NORML = 0;
    // tpye 需要悬停的Item
    public static final int ITEM_GROUP = 1;

    /**
     *
     */
    // item 类型 0 普通item 1 需要悬停的item
    public final int type;
    // 总数组中item的index
    public int indexItem;
    // 分组数组中，该item的index
    public int indexGroup;
    // 真正的数据
    public final T mData;

    //
    public PinnedItemData(int type, T data) {
        this.type = type;
        this.mData = data;
    }
}
