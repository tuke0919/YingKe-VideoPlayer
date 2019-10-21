package com.yingke.videoplayer.tiktok;

import android.content.Context;
import android.view.View;

import com.yingke.videoplayer.home.BaseRecycleViewAdapter;
import com.yingke.videoplayer.tiktok.bean.ListTiktokBean;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-21
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TikTokAdapter extends BaseRecycleViewAdapter<ListTiktokBean> {

    public TikTokAdapter(Context context, List<ListTiktokBean> mDataList) {
        super(context, mDataList);
    }

    @Override
    public int getConvertViewResId(int itemViewType) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(int viewType, View rootView) {
        return null;
    }
}
