package com.yingke.videoplayer.home.player;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.controller.MediaController;
import com.yingke.player.java.videoview.IjkVideoView;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.util.ToastUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import java.io.File;

import static com.yingke.player.java.IVideoBean.TYPE_AD;
import static com.yingke.player.java.IVideoBean.TYPE_REAL;

/**
 * 功能：业务相关的 播放器控件
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-09
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListIjkVideoView extends BaseListVideoView {

    private static final String TAG = "ListIjkVideoView";

    // 播放错误
    private RelativeLayout mPlayerErrorView;
    private TextView mPlayerErrorRetry;

    // 网络提示
    private RelativeLayout mNetTipView;
    private TextView mNetTipContinue;
    private TextView mBindFreeCard;

    // 加载页面
    private RelativeLayout mLoadingView;
    private ImageView mLoadingImage;

    // 播放完成
    private RelativeLayout mRePlayView;
    private TextView mRePlayBtn;

    // 空闲页面
    private RelativeLayout mCoverView;
    private SimpleDraweeView mCoverImage;
    private TextView mCoverTitle;
    private ImageView mCoverPlay;

    // 数据
    protected IVideoBean mVideoBean;

    public ListIjkVideoView(@NonNull Context context) {
        this(context, null);
    }

    public ListIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
        initData();
    }

    @Override
    public MediaController getControllerView() {
        return mMediaController;
    }

    @Override
    public IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }

    private void initViews() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.frag_video_player, null, false);
        mIjkVideoView = rootView.findViewById(R.id.ijk_video_view);
        mMediaController = rootView.findViewById(R.id.ijk_media_controller);

        mPlayerErrorView = rootView.findViewById(R.id.player_error_view);
        mPlayerErrorRetry = rootView.findViewById(R.id.player_error_retry);
        mPlayerErrorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新加载
            }
        });

        mNetTipView = rootView.findViewById(R.id.net_tips_view);
        mNetTipContinue = rootView.findViewById(R.id.player_net_continue);
        mNetTipContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 继续播放
            }
        });

        mBindFreeCard = rootView.findViewById(R.id.player_net_free_card);
        mBindFreeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 绑定免流卡
            }
        });

        mLoadingView = rootView.findViewById(R.id.player_loading_view);
        mLoadingImage = rootView.findViewById(R.id.player_loading);

        mRePlayView = rootView.findViewById(R.id.player_replay_view);
        mRePlayBtn = rootView.findViewById(R.id.player_replay);
        mRePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重播

            }
        });

        mCoverView = rootView.findViewById(R.id.cover_view);
        mCoverImage = rootView.findViewById(R.id.cover_image);
        mCoverTitle = rootView.findViewById(R.id.cover_title);
        mCoverTitle.setVisibility(GONE);
        mCoverPlay = rootView.findViewById(R.id.cover_play);
        mCoverPlay.setVisibility(VISIBLE);
        addView(rootView);
    }

    @Override
    protected void initControllerView() {
        super.initControllerView();
        if (getControllerView() instanceof ListIjkAdMediaController) {
            ((ListIjkAdMediaController) getControllerView()).setListener(new ListIjkAdMediaController.OnAdControllerListener() {
                @Override
                public void onSkipAd() {
                    PlayerLog.e(TAG, "跳过广告");
                    // 继续播放真视频
                    if (mVideoBean != null) {
                        mVideoBean.setCurrentType(TYPE_REAL);
                        setVideoOnline(mVideoBean);
                    }
                }

                @Override
                public void onToAdDetail() {
                    PlayerLog.e(TAG, "去广告详情");
                    ToastUtil.showToast("去广告详情");
                }

                @Override
                public void onAdBack() {
                    PlayerLog.e(TAG, "广告全屏时返回");

                }
            });
        }
    }

    private void initData() {
        initControllerView();
        initVideoPlayerView();
    }

    @Override
    public void setVideoOnline(IVideoBean videoBean) {
        super.setVideoOnline(videoBean);
    }

    @Override
    public void beforeSetVideoOnline(IVideoBean videoBean) {
        super.beforeSetVideoOnline(videoBean);
        mVideoBean = videoBean;
        // 设置封面
        if (mVideoBean instanceof ListVideoData) {
            int mCurrentType = mVideoBean.getCurrentType();
            switch (mCurrentType) {
                case TYPE_AD:
                    // 广告视频

                    getControllerView().setShowAlways(true);
                    getControllerView().show();

                    setCoverImage((ListVideoData) mVideoBean, true);
                    if (getControllerView() instanceof ListIjkAdMediaController) {
                        ((ListIjkAdMediaController) getControllerView()).showAdController();
                    }
                    break;
                case TYPE_REAL:
                    // 真视频

                    getControllerView().setShowAlways(false);
                    getControllerView().hide();

                    setCoverImage((ListVideoData) mVideoBean, false);
                    if (getControllerView() instanceof ListIjkAdMediaController) {
                        ((ListIjkAdMediaController) getControllerView()).showNormalController();
                    }

                    break;
            }
        }
    }

    /**
     * 设置封面图
     * @param videoData
     * @param isAd
     */
    public void setCoverImage(ListVideoData videoData, boolean isAd) {
        String thumbPath = videoData.getThumbPath();;
        if (isAd) {
            thumbPath = videoData.getAdThumbPath();
        }
        if (!TextUtils.isEmpty(thumbPath)){
            FrescoUtil.displayImage(mCoverImage, new File(thumbPath));
        } else {
            String url = videoData.getUrl();
            if (isAd) {
                url = videoData.getAdUrl();
            }
            if (!TextUtils.isEmpty(url)) {
                File thumbFile = FileUtil.getVideoThumbFile(getContext(), EncryptUtils.md5String(url));
                if (thumbFile.exists()) {
                    if (isAd) {
                        videoData.setAdThumbPath(thumbFile.getAbsolutePath());
                    } else {
                        videoData.setThumbPath(thumbFile.getAbsolutePath());
                    }
                    FrescoUtil.displayImage(mCoverImage, thumbFile);
                }
            }
        }
    }

    /**
     * 正在播放
     */
    @Override
    public void showPlayingView(){
        if (mMediaController.isShowAlways()) {
            mMediaController.show();
        }
        mCoverView.setVisibility(GONE);
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 空闲页
     */
    @Override
    protected void showIdleView() {
        mMediaController.hide();
        mCoverView.setVisibility(VISIBLE);
        mCoverPlay.setVisibility(VISIBLE);

        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 加载中
     */
    @Override
    public void showLoadingView() {
        mCoverView.setVisibility(VISIBLE);
        mCoverPlay.setVisibility(GONE);

        mMediaController.hide();
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(500);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        mLoadingImage.startAnimation(rotate);
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 播放错误
     */
    @Override
    public void showErrorView(){
        mCoverView.setVisibility(GONE);
        mMediaController.hide();
        mPlayerErrorView.setVisibility(View.VISIBLE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

    /**
     * 播放完成
     */
    @Override
    public void showCompletionView(){
        mCoverView.setVisibility(GONE);
        mMediaController.hide();
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();

        int mCurrentType = mVideoBean.getCurrentType();
        switch (mCurrentType) {
            case TYPE_AD:
                mRePlayView.setVisibility(View.GONE);

                // 继续播放真视频
                mVideoBean.setCurrentType(TYPE_REAL);
                setVideoOnline(mVideoBean);
                break;
            case TYPE_REAL:
                mRePlayView.setVisibility(View.VISIBLE);
                mVideoBean.setCurrentType(TYPE_AD);
                break;
        }
    }

    /**
     * 显示网络提示界面
     */
    @Override
    public void showNetTipView(){
        mCoverView.setVisibility(GONE);

        mMediaController.hide();
        mPlayerErrorView.setVisibility(View.GONE);
        mNetTipView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mLoadingImage.clearAnimation();
        mRePlayView.setVisibility(View.GONE);
    }

}
