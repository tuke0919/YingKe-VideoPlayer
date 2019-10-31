package com.yingke.videoplayer.home.landscape;

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
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-29
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class LandListVideoVH {
    public static final String TAG = "LandListVideoVH";
    private View itemView;

    private FrameLayout mLandVideoContainer;

    private RelativeLayout mLandCoverView;
    // 返回
    private ImageView mImageBack;
    // 横屏标题
    private TextView mLandTitle;
    // 封面
    private SimpleDraweeView mDraweeView;

    private int position;
    private ListVideoData mListVideoData;


    public LandListVideoVH(View itemView){
        this.itemView = itemView;
        mLandVideoContainer = itemView.findViewById(R.id.land_video_view_container);
        mLandCoverView = itemView.findViewById(R.id.land_cover_view);
        mDraweeView = itemView.findViewById(R.id.land_cover_image);
        mImageBack = itemView.findViewById(R.id.land_image_back);
        mLandTitle = itemView.findViewById(R.id.land_title);
        mImageBack = itemView.findViewById(R.id.land_image_back);
        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void onRefreshData(int position, ListVideoData data) {
        PlayerLog.e(TAG, "position = " + position  );
        if (data == null) {
            return;
        }
        this.position = position;
        mListVideoData = data;

        mLandCoverView.setVisibility(View.VISIBLE);
        mDraweeView.setVisibility(View.VISIBLE);
        mLandTitle.setVisibility(View.VISIBLE);
        // 标题
        mLandTitle.setText(data.getTitle());
        // 封面图
        String thumbPath = data.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)){
            FrescoUtil.displayImage(mDraweeView, new File(thumbPath));
        } else {
            File thumbFile = FileUtil.getVideoThumbFile(this.itemView.getContext(), EncryptUtils.md5String(data.getUrl()));
            if (thumbFile.exists()) {
                data.setThumbPath(thumbFile.getAbsolutePath());
            }
            FrescoUtil.displayImage(mDraweeView, thumbFile);
        }
    }

    /**
     * 绑定横屏播放器
     * @param baseListVideoView
     */
    public void attachVideoPlayer(BaseListVideoView baseListVideoView) {
        hideLandCoverView();
        clearVideoPlayer(mLandVideoContainer, true);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mLandVideoContainer.addView(baseListVideoView, 0, params);
    }


    /**
     * 显示空闲界面
     * @param releasePlayer 是否释放播放器
     */
    public void showIdleView(boolean releasePlayer) {
        clearVideoPlayer(mLandVideoContainer, releasePlayer);
        // 显示封面标题
        showLandCoverView();
    }

    /**
     * 清除播放器
     * @param videoContainer
     */
    public void clearVideoPlayer(ViewGroup videoContainer, boolean releasePlayer) {
        if (videoContainer != null && videoContainer.getChildCount() > 0) {
            if (releasePlayer) {
                ListIjkVideoView videoView = (ListIjkVideoView) videoContainer.getChildAt(0);
                if (videoView != null) {
                    videoView.release();
                    // 修改成最初类型
                    if (mListVideoData != null) {
                        mListVideoData.setCurrentType(mListVideoData.getFirstType());
                    }
                }
            }
            videoContainer.removeAllViews();
        }
    }


    /**
     * 显示封面
     */
    public void showLandCoverView() {
        if (mLandCoverView != null) {
            mLandCoverView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏封面
     */
    public void hideLandCoverView() {
        if (mLandCoverView != null) {
            mLandCoverView.setVisibility(View.GONE);
        }
    }












}
