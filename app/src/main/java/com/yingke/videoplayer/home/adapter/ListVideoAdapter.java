package com.yingke.videoplayer.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.videoplayer.home.player.ListIjkVideoView;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.util.PlayerUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;
import com.yingke.widget.base.BaseRecycleViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.recyclerview.widget.LinearLayoutManager;
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

    public interface OnListVideoClickListener{

        void onListVideoPlay(View rootView, FrameLayout videoContainer, ListVideoData videoData);

        void onMoreClick(ListVideoData videoData);
    }

}
