package com.yingke.videoplayer.home.player;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yingke.videoplayer.R;

/**
 * 功能：带画中画和广告 的控制器
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-26
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListIjkAdMediaController extends ListIjkPipMediaController {

    private RelativeLayout mAdRootView;
    private ImageView mImageBack;
    private TextView mAdTime;
    private TextView mAdSkip;
    private TextView mAdDetail;
    private TextView mAdVolume;
    private ImageView mAdFullScreen;

    public ListIjkAdMediaController(Context context) {
        super(context);
    }

    public ListIjkAdMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListIjkAdMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.list_media_controller_ad;
    }

    @Override
    protected void initView() {
        super.initView();
        mAdRootView = mRootView.findViewById(R.id.controller_content_ad);
        mImageBack = mRootView.findViewById(R.id.controller_ad_back);
        mImageBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen()) {
                    setRequestedOrientation();
                } else {
                    if (mListener != null) {
                        mListener.onAdBack();
                    }
                }
            }
        });
        mImageBack.setVisibility(GONE);
        mAdTime = mRootView.findViewById(R.id.controller_ad_time);
        mAdSkip = mRootView.findViewById(R.id.controller_ad_skip);
        mAdSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSkipAd();
                }
            }
        });

        mAdDetail = mRootView.findViewById(R.id.controller_ad_detail);
        mAdDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onToAdDetail();
                }
            }
        });
        mAdVolume = mRootView.findViewById(R.id.controller_ad_volume);
        mAdVolume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (isMute()) {
                     mAdVolume.setSelected(false);
                     setMute(false);
                 } else {
                     mAdVolume.setSelected(true);
                     setMute(true);
                 }
            }
        });
        mAdFullScreen = mRootView.findViewById(R.id.controller_ad_full);
        mAdFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    public void showNormalController(){
        super.showNormalController();
        mAdRootView.setVisibility(GONE);
    }

    /**
     * 显示广告控制器
     */
    public void showAdController(){
        hideNormalController();
        hidePipController();
        mAdRootView.setVisibility(VISIBLE);
    }

    /**
     * 隐藏广告控制器
     */
    public void hideAdController() {
        mAdRootView.setVisibility(GONE);
    }

    @Override
    public void showPipController() {
        super.showPipController();
        hideAdController();
    }

    @Override
    protected void afterFullScreenChanged() {
        super.afterFullScreenChanged();
        if (isFullScreen()) {
            mImageBack.setVisibility(VISIBLE);
        } else {
            mImageBack.setVisibility(GONE);
        }

    }

    @Override
    protected void notifyPlayerPosition(int currentPositionS, int secondPositionS, int durationS) {
        super.notifyPlayerPosition(currentPositionS, secondPositionS, durationS);
        String leftDuration = (durationS - currentPositionS)  + "";
        String leftTime = String.format(getResources().getString(R.string.list_video_ad_time), leftDuration);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
        SpannableString spannableString = new SpannableString(leftTime);
        spannableString.setSpan(colorSpan, 0, leftDuration.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(14, true), 0, leftDuration.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAdTime.setText(spannableString);
    }

    private OnAdControllerListener mListener;

    public void setListener(OnAdControllerListener listener) {
        mListener = listener;
    }

    public interface OnAdControllerListener{
        /**
         * 跳过广告
         */
        void onSkipAd();

        /**
         * 去广告详情
         */
        void onToAdDetail();

        /**
         * 返回
         */
        void onAdBack();
    }
}
