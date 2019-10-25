package com.yingke.videoplayer.home.item;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.player.ListIjkVideoView;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import java.io.File;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-25
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListVideoVH implements View.OnClickListener {

    public static final String TAG = "ListVideoVH";

    private View itemView;

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

    private ListVideoAdapter.OnListVideoClickListener mListener;

    public ListVideoVH(View itemView, ListVideoAdapter.OnListVideoClickListener listener) {
        this.itemView = itemView;
        mListener = listener;
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

        mCoverPlay.setOnClickListener(this);

    }

    public void onRefreshData(int position, ListVideoData data) {
        PlayerLog.e(TAG, "position = " + position  );
        if (data == null) {
            return;
        }
        this.position = position;
        mListVideoData = data;

        // 停止播放
        if (mVideoContainer != null && mVideoContainer.getChildCount() != 0) {
            BaseListVideoView videoView = (BaseListVideoView) mVideoContainer.getChildAt(0);
            if (videoView != null) {
                videoView.release();
                videoView = null;
            }
            mVideoContainer.removeAllViews();
        }

        mCoverView.setVisibility(View.VISIBLE);
        // 标题
        mCoverTitle.setText(data.getTitle());
        // 封面图
        String thumbPath = data.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)){
            FrescoUtil.displayImage(mCoverImage, new File(thumbPath));
        } else {
            File thumbFile = FileUtil.getVideoThumbFile(this.itemView.getContext(), EncryptUtils.md5String(data.getUrl()));
            if (thumbFile.exists()) {
                data.setThumbPath(thumbFile.getAbsolutePath());
            }
            FrescoUtil.displayImage(mCoverImage, thumbFile);
        }

        // 用户信息
        FrescoUtil.displayImage(mAuthorAvatar, data.getAuthorAvatar());
        mAuthorName.setText(data.getAuthorName());
        mDescription.setText(data.getDescription());
        mCommentCount.setText(String.valueOf(data.getCommentCount()));
        mVoteCount.setText(String.valueOf(data.getVoteCount()));
    }

    /**
     * 显示空闲界面
     */
    public void showIdleView() {
        clearVideoPlayer(mVideoContainer);
        // 显示封面标题
        mCoverView.setVisibility(View.VISIBLE);
    }

    /**
     * 清除播放器
     * @param videoContainer
     */
    public void clearVideoPlayer(ViewGroup videoContainer) {
        if (videoContainer != null && videoContainer.getChildCount() > 0) {
            ListIjkVideoView videoView = (ListIjkVideoView) videoContainer.getChildAt(0);
            if (videoView != null) {
                videoView.release();
                videoView = null;
            }
            videoContainer.removeAllViews();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cover_play :
                if (mListVideoData == null || TextUtils.isEmpty(mListVideoData.getThumbPath())) {
                    return;
                }

                mCoverView.setVisibility(View.GONE);
                if (mListener != null) {
                    clearVideoPlayer(mVideoContainer);
                    mListener.onListVideoPlay(ListVideoVH.this.itemView, mVideoContainer, mListVideoData);
                }
                break;
        }
    }
}
