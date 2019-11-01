package com.yingke.videoplayer.home.landscape.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yingke.player.java.ScreenScale;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.util.DeviceUtil;

import androidx.annotation.NonNull;

/**
 * 功能：显示更多面板
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-01
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class MoreDialog extends HalfTransDialog implements
        View.OnClickListener,
        LandVolumeHelper.VolumeListener,
        LandLightnessHelper.LightnessListener {

    private FrameLayout mMainContent;
    private TextView mLandShare;
    private TextView mLandCollect;
    private TextView mLandCache;

    private TextView mLandNoInterest;
    private TextView mLandJubao;
    private TextView mLandPlayBack;
    private TextView mLandScreenMode;

    private LinearLayout mLandScreenView;
    private TextView mLandScreen43;
    private TextView mLandScreenFull;
    private TextView mLandScreenOrigin;
    private TextView mLandScreenCrop;
    private TextView mLandScreenMirror;


    private TextView mLandSpeed07;
    private TextView mLandSpeed10;
    private TextView mLandSpeed15;
    private TextView mLandSpeed20;

    private SeekBar mVolumeSeekbar;
    private SeekBar mLightnessSeekbar;

    private LandVolumeHelper mLandVolumeHelper;
    private boolean mIsFromUser = false;

    private LandLightnessHelper mLandLightnessHelper;

    public MoreDialog(@NonNull Context context) {
        super(context);
        initView();
        initData();
    }

    @Override
    protected void adjustParams(View rootView) {
        super.adjustParams(rootView);
        mMainContent = rootView.findViewById(R.id.trans_view);
        ViewGroup.LayoutParams params = mMainContent.getLayoutParams();
        params.width = DeviceUtil.dip2px(getContext(), 350);
        mMainContent.setLayoutParams(params);
    }

    private void initView() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_land_more, mMainContent, false);
        mMainContent.addView(rootView);

        mLandShare = rootView.findViewById(R.id.land_share);
        mLandCollect = rootView.findViewById(R.id.land_collect);
        mLandCache = rootView.findViewById(R.id.land_cache);
        mLandNoInterest = rootView.findViewById(R.id.land_no_interest);
        mLandJubao = rootView.findViewById(R.id.land_jubao);
        mLandPlayBack = rootView.findViewById(R.id.land_back_play);
        mLandScreenMode = rootView.findViewById(R.id.land_screen_scale);

        mLandScreenView = rootView.findViewById(R.id.land_screen_view);
        mLandScreen43 = rootView.findViewById(R.id.land_screen_43);
        mLandScreenFull = rootView.findViewById(R.id.land_screen_full);
        mLandScreenOrigin = rootView.findViewById(R.id.land_screen_origin);
        mLandScreenCrop = rootView.findViewById(R.id.land_screen_crop);
        mLandScreenMirror = rootView.findViewById(R.id.land_screen_mirror);

        mLandSpeed07 = rootView.findViewById(R.id.land_speed_0_7);
        mLandSpeed10 = rootView.findViewById(R.id.land_speed_1);
        mLandSpeed15 = rootView.findViewById(R.id.land_speed_1_5);
        mLandSpeed20 = rootView.findViewById(R.id.land_speed_2);

        mVolumeSeekbar = rootView.findViewById(R.id.volume_seekbar);
        mLightnessSeekbar = rootView.findViewById(R.id.lightness_seekbar);
    }

    private void initData() {
        mLandShare.setOnClickListener(this);
        mLandCollect.setOnClickListener(this);
        mLandCache.setOnClickListener(this);
        mLandNoInterest.setOnClickListener(this);
        mLandJubao.setOnClickListener(this);
        mLandPlayBack.setOnClickListener(this);
        mLandScreenMode.setOnClickListener(this);

        mLandScreen43.setOnClickListener(this);
        mLandScreenOrigin.setOnClickListener(this);
        mLandScreenFull.setOnClickListener(this);
        mLandScreenCrop.setOnClickListener(this);
        mLandScreenMirror.setOnClickListener(this);

        mLandSpeed07.setOnClickListener(this);
        mLandSpeed10.setOnClickListener(this);
        mLandSpeed15.setOnClickListener(this);
        mLandSpeed20.setOnClickListener(this);

        mLandVolumeHelper = new LandVolumeHelper(getContext());
        mLandVolumeHelper.setVolumeListener(this);
        // 设置百分比
        final int streamMaxVolume = mLandVolumeHelper.getMaxVolume();
        int currentVolume = mLandVolumeHelper.getCurrentVolume();
        int percent = (int) ((currentVolume / (streamMaxVolume * 1f)) * 100);
        mVolumeSeekbar.setProgress(percent);
        mVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 设置到系统
                if (mIsFromUser) {
                    int volume = (int) ((progress / 100f) * streamMaxVolume);
                    mLandVolumeHelper.setCurrVolume(volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsFromUser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsFromUser = false;
            }
        });

        mLandLightnessHelper = new LandLightnessHelper(getContext());
        mLandLightnessHelper.setLightnessListener(this);

        int lightness = mLandLightnessHelper.getScreenBrightness();
        int percent1 = (int) ((lightness / 255f) * 100);
        mLightnessSeekbar.setProgress(percent1);

        mLightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float lightness =  (progress / 100f) * 255 ;
                // 设置到系统
                if (mIsFromUser) {
                    mLandLightnessHelper.setScreenBrightness((int) lightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsFromUser = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsFromUser = false;
            }
        });
    }

    @Override
    public void onVolumeChanged(int volume) {
        if (mIsFromUser) {
            // 当前用户在操作
            return;
        }
        mIsFromUser = false;
        // 设置百分比
        final int streamMaxVolume = mLandVolumeHelper.getMaxVolume();
        int currentVolume = mLandVolumeHelper.getCurrentVolume();

        int percent = (int) ((currentVolume / (streamMaxVolume * 1f)) * 100);
        mVolumeSeekbar.setProgress(percent);
    }

    @Override
    public void onLightnessChanged(int lightness) {
        if (mIsFromUser) {
            // 当前用户在操作
            return;
        }
        mIsFromUser = false;
        // 设置百分比
        int percent1 = (int) ((lightness / 255f) * 100);
        mLightnessSeekbar.setProgress(percent1);
    }

    @Override
    public void show() {
        super.show();
        mLandVolumeHelper.registerReceiver();
        mLandLightnessHelper.registerObserver();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mLandVolumeHelper.unregisterReceiver();
        mLandLightnessHelper.unRegisterObserver();
        mListener = null;
        mIsFromUser = false;
    }

    /**
     * 更新倍速
     * @param speedPos
     */
    public void updateSpeed(int speedPos) {
        updateSpeedInner(speedPos);
    }

    /**
     * 内部更新
     * @param speedPos
     */
    private void updateSpeedInner(int speedPos){
        mLandSpeed07.setTextColor(getSelectedColor(speedPos, 0));
        mLandSpeed10.setTextColor(getSelectedColor(speedPos, 1));
        mLandSpeed15.setTextColor(getSelectedColor(speedPos, 2));
        mLandSpeed20.setTextColor(getSelectedColor(speedPos, 3));
    }

    /**
     * 选中颜色
     * @param selectPos
     * @param targetPos 文本位置
     * @return
     */
    private int getSelectedColor(int selectPos, int targetPos) {
        if (selectPos == targetPos) {
            return getContext().getResources().getColor(R.color.red);
        } else {
            return getContext().getResources().getColor(R.color.white);
        }
    }

    /**
     * 更新屏幕模式
     * @param screenScale {@link ScreenScale}
     */
    public void updateScreenScale(int screenScale){
        int selectedPos = -1;
        switch (screenScale) {
            case ScreenScale.SCREEN_SCALE_DEFAULT: selectedPos = -1; break;
            case ScreenScale.SCREEN_SCALE_4_3: selectedPos = 0; break;
            case ScreenScale.SCREEN_SCALE_MATCH_PARENT: selectedPos = 1; break;
            case ScreenScale.SCREEN_SCALE_ORIGINAL: selectedPos = 2; break;
            case ScreenScale.SCREEN_SCALE_CENTER_CROP: selectedPos = 3; break;
            default:
                selectedPos = 4;
                break;
        }

        updateScreenScaleInner(selectedPos);
    }

    /**
     * 更新屏幕模式
     * @param selectedPos
     */
    private void updateScreenScaleInner(int selectedPos){
        mLandScreen43.setTextColor(getSelectedColor(selectedPos, 0));
        mLandScreenFull.setTextColor(getSelectedColor(selectedPos, 1));
        mLandScreenOrigin.setTextColor(getSelectedColor(selectedPos, 2));
        mLandScreenCrop.setTextColor(getSelectedColor(selectedPos, 3));
        mLandScreenMirror.setTextColor(getSelectedColor(selectedPos, 4));


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.land_share:
                if (mListener != null) {
                    mListener.onLandShare();
                }
                break;
            case R.id.land_collect:
                if (mListener != null) {
                    mListener.onLandCollect();
                }
                break;
            case R.id.land_cache:
                if (mListener != null) {
                    mListener.onLandCache();
                }
                break;
            case R.id.land_no_interest:
                if (mListener != null) {
                    mListener.onLandNoInterest();
                }
                break;
            case R.id.land_jubao:
                if (mListener != null) {
                    mListener.onLandJubao();
                }
                break;

            case R.id.land_back_play:
                if (mListener != null) {
                    mListener.onLandPlayBack();
                }
                break;
            case R.id.land_screen_scale:
                mLandScreenView.setVisibility(mLandScreenView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.land_screen_43:
                if (mListener != null) {
                    updateScreenScale(ScreenScale.SCREEN_SCALE_4_3);
                    mListener.onLandScreenMode(ScreenScale.SCREEN_SCALE_4_3);
                    dismissDelay();
                }
                break;
            case R.id.land_screen_full:
                if (mListener != null) {
                    updateScreenScale(ScreenScale.SCREEN_SCALE_MATCH_PARENT);
                    mListener.onLandScreenMode(ScreenScale.SCREEN_SCALE_MATCH_PARENT);
                    dismissDelay();
                }
                break;
            case R.id.land_screen_origin:
                if (mListener != null) {
                    updateScreenScale(ScreenScale.SCREEN_SCALE_ORIGINAL);
                    mListener.onLandScreenMode(ScreenScale.SCREEN_SCALE_ORIGINAL);
                    dismissDelay();
                }
                break;

            case R.id.land_screen_crop:
                if (mListener != null) {
                    updateScreenScale(ScreenScale.SCREEN_SCALE_CENTER_CROP);
                    mListener.onLandScreenMode(ScreenScale.SCREEN_SCALE_CENTER_CROP);
                    dismissDelay();

                }
                break;
            case R.id.land_screen_mirror:
                if (mListener != null) {
                    updateScreenScale(-1);
                    mListener.onLandMirror();
                    dismissDelay();
                }
                break;
            case R.id.land_speed_0_7:
                if (mListener != null) {
                    updateSpeed(0);
                    mListener.onSpeed(0);
                }
                break;
            case R.id.land_speed_1:
                if (mListener != null) {
                    updateSpeed(1);
                    mListener.onSpeed(1);
                }
                break;
            case R.id.land_speed_1_5:
                if (mListener != null) {
                    updateSpeed(2);
                    mListener.onSpeed(2);
                }
                break;
            case R.id.land_speed_2:
                if (mListener != null) {
                    updateSpeed(3);
                    mListener.onSpeed(3);
                }
                break;

        }
    }

    private void dismissDelay() {
        mMainContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 100);
    }


    private OnMoreListener mListener;

    public void setListener(OnMoreListener listener) {
        mListener = listener;
    }

    /**
     *
     */
    public interface OnMoreListener{
        /**
         * 分享
         */
        void onLandShare();

        /**
         * 收藏
         */
        void onLandCollect();

        /**
         * 缓存
         */
        void onLandCache();

        /**
         * 不感兴趣
         */
        void onLandNoInterest();

        /**
         * 举报
         */
        void onLandJubao();

        /**
         * 后台播放
         */
        void onLandPlayBack();

        /**
         * {@link ScreenScale}
         * @param screenMode
         */
        void onLandScreenMode(int screenMode);

        /**
         * 镜像
         */
        void onLandMirror();

        /**
         * @param speed 0,1,2,3
         */
        void onSpeed(int speed);
    }
}
