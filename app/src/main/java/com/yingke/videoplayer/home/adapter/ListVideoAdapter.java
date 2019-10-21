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
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.util.PlayerUtil;
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

        private FrameLayout mVideoContainer;
        private RelativeLayout mCoverView;
        private SimpleDraweeView mCoverImage;
        private TextView mCoverTitle;
        private ImageView mCoverPlay;

        private SimpleDraweeView mAuthorAvatar;
        private TextView mAuthorName;
        private TextView mDescription;
        private TextView mCommentCount;
        private TextView mVoteCount;

        private int position;
        private ListVideoData mListVideoData;

        public ListVideoHolder(View itemView) {
            super(itemView);
            mVideoContainer = itemView.findViewById(R.id.video_view_container);
            mCoverView = itemView.findViewById(R.id.cover_view);
            mCoverImage = itemView.findViewById(R.id.cover_image);
            mCoverTitle = itemView.findViewById(R.id.cover_title);
            mCoverPlay = itemView.findViewById(R.id.cover_play);

            mAuthorAvatar = itemView.findViewById(R.id.user_avatar);
            mAuthorName = itemView.findViewById(R.id.tv_user_name);
            mDescription = itemView.findViewById(R.id.tv_description);
            mCommentCount = itemView.findViewById(R.id.tv_comments);
            mVoteCount = itemView.findViewById(R.id.iv_vote_view);

            mCoverPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCoverView.setVisibility(View.GONE);
                    if (mListener != null) {
                        mListener.onListVideoPlay(mRootView, mVideoContainer, mListVideoData);
                    }
                }
            });
        }

        @Override
        public void onRefreshData(int position, ListVideoData data) {
            PlayerLog.e(TAG, "position = " + position  );
            if (data == null) {
                return;
            }
            this.position = position;
            mListVideoData = data;

            if (isCoverVisible()) {
                // 标题
                mCoverTitle.setText(data.getTitle());
                // 封面图
                String thumbPath = data.getThumbPath();
                if (!TextUtils.isEmpty(thumbPath)){
                    FrescoUtil.displayImage(mCoverImage, new File(thumbPath));
                } else {
                    File thumbFile = FileUtil.getVideoThumbFile(context, EncryptUtils.md5String(data.getUrl()));
                    if (thumbFile.exists()) {
                        data.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    FrescoUtil.displayImage(mCoverImage, thumbFile);
                }
            }
            // 用户信息
            FrescoUtil.displayImage(mAuthorAvatar, data.getAuthorAvatar());
            mAuthorName.setText(data.getAuthorName());
            mDescription.setText(data.getDescription());
            mCommentCount.setText(String.valueOf(data.getCommentCount()));
            mVoteCount.setText(String.valueOf(data.getVoteCount()));

        }

        /**
         * @return 封面可见，判断播放标志
         */
        public boolean isCoverVisible(){
            return  mCoverView != null && mCoverView.getVisibility() == View.VISIBLE;
        }

        /**
         * @param thumbImagePath 视频封面
         */
        public void changeImage(String thumbImagePath){
            FrescoUtil.displayImage(mCoverImage, new File(thumbImagePath));
        }

    }
    private OnListVideoClickListener mListener;

    public void setListener(OnListVideoClickListener listener) {
        mListener = listener;
    }

    public interface OnListVideoClickListener{

        void onListVideoPlay(View rootView, FrameLayout videoContainer, ListVideoData videoData);
    }

}
