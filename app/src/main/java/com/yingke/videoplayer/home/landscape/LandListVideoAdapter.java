package com.yingke.videoplayer.home.landscape;

import android.content.Context;
import android.view.View;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.widget.base.BaseRecycleViewAdapter;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-29
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class LandListVideoAdapter extends BaseRecycleViewAdapter<ListVideoData> {


    public LandListVideoAdapter(Context context) {
        super(context);
    }

    public LandListVideoAdapter(Context context, List<ListVideoData> mDataList) {
        super(context, mDataList);
    }

    @Override
    public int getConvertViewResId(int itemViewType) {
        return R.layout.frag_recommend_land_item;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(int viewType, View rootView) {
        return new LandVideoViewHolder(rootView);
    }

    public class LandVideoViewHolder extends BaseViewHolder<ListVideoData> {

        private LandListVideoVH mLandListVideoVH;

        public LandVideoViewHolder(View itemView) {
            super(itemView);
            mLandListVideoVH = new LandListVideoVH(itemView, mListener);
        }

        @Override
        public void onRefreshData(int position, ListVideoData data) {
            mLandListVideoVH.onRefreshData(position, data);
        }

        public LandListVideoVH getLandListVideoVH(){
            return mLandListVideoVH;
        }
    }


    private OnBackListener mListener;

    public void setListener(OnBackListener listener) {
        mListener = listener;
    }

    /**
     * 返回监听
     */
    public interface OnBackListener{
        void onBack();
    }



}
