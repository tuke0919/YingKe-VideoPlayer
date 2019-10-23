package com.yingke.videoplayer.tiktok;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.tiktok.bean.ListTiktokBean;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.util.StringUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;
import com.yingke.widget.textview.ScrollTextView;

import java.io.File;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-22
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TiktokVideoVH {

    private View itemView;

    // 视频容器
    private FrameLayout mVideoContainer;
    // 封面
    private SimpleDraweeView mDraweeView;
    // 头像
    private SimpleDraweeView mUserAvatar;

    // 点赞
    private LinearLayout mVoteView;
    private ImageView mVoteImage;
    private TextView mVoteCount;
    // 评论
    private LinearLayout mMsgView;
    private TextView mMsgCount;
    // 分享
    private LinearLayout mShareView;
    private TextView mShareCount;
    // 用户名
    private TextView mUserName;
    private TextView mDescription;
    private TextView mMusic;
    private ScrollTextView mMusicText;

    private TextView mCreateTime;
    private ImageView mPlayBtn;

    public TiktokVideoVH(View itemView) {
        this.itemView = itemView;
        initView();
    }

    public void initView(){
        mVideoContainer = itemView.findViewById(R.id.tiktok_video_container);
        mDraweeView = itemView.findViewById(R.id.tiktok_image);
        mUserAvatar = itemView.findViewById(R.id.tiktok_user_avatar);
        mVoteView = itemView.findViewById(R.id.tiktok_vote_view);
        mVoteImage = itemView.findViewById(R.id.tiktok_vote_image);;
        mVoteCount = itemView.findViewById(R.id.tiktok_vote_count);
        mMsgView = itemView.findViewById(R.id.tiktok_msg_view);
        mMsgCount = itemView.findViewById(R.id.tiktok_msg_count);
        mShareView = itemView.findViewById(R.id.tiktok_share_view);
        mShareCount = itemView.findViewById(R.id.tiktok_share_count);
        mUserName = itemView.findViewById(R.id.tiktok_username);
        mDescription = itemView.findViewById(R.id.tiktok_description);
        mMusicText = itemView.findViewById(R.id.tiktok_music_text);
        mCreateTime = itemView.findViewById(R.id.tiktok_time);
        mPlayBtn = itemView.findViewById(R.id.tiktok_play_icon);
    }

    /**
     * 添加视频布局
     * @param baseListVideoView
     */
    public void addVideoView(BaseListVideoView baseListVideoView, String thumb) {
        if (baseListVideoView == null) {
            return;
        }
        mDraweeView.setVisibility(View.VISIBLE);
        FrescoUtil.displayImage(mDraweeView, new File(thumb));

        // 移除父类
        ViewParent parent = baseListVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(baseListVideoView);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mVideoContainer.addView(baseListVideoView, params);
        // 隐藏封面

        mVideoContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDraweeView.setVisibility(View.GONE);
                mPlayBtn.setVisibility(View.GONE);
            }
        },200);


    }

    /**
     * 移除视频布局
     */
    public void removeVideoView(String thumbPath){
        if (mVideoContainer.getChildCount() != 0) {
            mVideoContainer.removeAllViews();
        }
        mDraweeView.setVisibility(View.VISIBLE);
        FrescoUtil.displayImage(mDraweeView, new File(thumbPath));
        mPlayBtn.setVisibility(View.VISIBLE);
    }
}
