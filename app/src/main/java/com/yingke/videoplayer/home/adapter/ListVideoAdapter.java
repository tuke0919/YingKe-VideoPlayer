package com.yingke.videoplayer.home.adapter;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.widget.base.BaseRecycleViewAdapter;


import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-14
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListVideoAdapter extends BaseRecycleViewAdapter<ListVideoData> {
    private static final String TAG = "ListVideoAdapter";


    public ListVideoAdapter(Context context) {
        super(context);
    }


    @Override
    public int getConvertViewResId(int itemViewType) {
        return R.layout.frag_recommend_item;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(int viewType, View rootView) {
        return new ListVideoHolder(rootView);
    }

    public class ListVideoHolder extends BaseViewHolder<ListVideoData> {

        private ListVideoVH mListVideoVH;

        public ListVideoHolder(View itemView) {
            super(itemView);
            mListVideoVH = new ListVideoVH(itemView, mListener);
        }

        @Override
        public void onRefreshData(int position, ListVideoData data) {
            mListVideoVH.onRefreshData(position, data);
        }

        public ListVideoVH getListVideoVH() {
            return mListVideoVH;
        }
    }
    private OnListVideoClickListener mListener;

    public void setListener(OnListVideoClickListener listener) {
        mListener = listener;
    }

    /**
     * 列表点击事件
     */
    public interface OnListVideoClickListener{

        /**
         * 点击播放
         * @param videoContainer
         * @param videoData
         * @param position
         */
        void onListVideoPlay(FrameLayout videoContainer, ListVideoData videoData, int position);

        /**
         * 更多点击
         * @param videoData
         */
        void onMoreClick(ListVideoData videoData);

        /**
         * 进入视频详情
         * @param itemView
         * @param videoContainer
         * @param videoData
         */
        void onVideoDetailClick(View itemView, FrameLayout videoContainer, ListVideoData videoData, int position);

    }

}
