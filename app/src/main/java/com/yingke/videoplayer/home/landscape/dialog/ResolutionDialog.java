package com.yingke.videoplayer.home.landscape.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.util.DeviceUtil;
import com.yingke.videoplayer.widget.MarqueeTextView;

import androidx.annotation.NonNull;

/**
 * 功能：分辨率
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-01
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ResolutionDialog extends HalfTransDialog implements View.OnClickListener {

    private FrameLayout mMainContent;

    private TextView mLandLanGuang;
    private TextView mLandChaoQing;
    private TextView mLandGaoQing;
    private TextView mLandBiaoQing;

    public ResolutionDialog(@NonNull Context context) {
        super(context);
        initView();
        initData();
    }

    @Override
    protected void adjustParams(View rootView) {
        super.adjustParams(rootView);
        mMainContent = rootView.findViewById(R.id.trans_view);

        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        params.width = DeviceUtil.dip2px(getContext(), 200);
        rootView.setLayoutParams(params);
        rootView.requestLayout();
    }

    private void initView() {
        View resolutionView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_land_resolution, mMainContent, false);
        mLandLanGuang = resolutionView.findViewById(R.id.land_languang);
        mLandChaoQing = resolutionView.findViewById(R.id.land_chaoqing);
        mLandGaoQing = resolutionView.findViewById(R.id.land_gaoqing);
        mLandBiaoQing = resolutionView.findViewById(R.id.land_biaoqing);
        mMainContent.addView(resolutionView);
    }

    private void initData() {
        mLandLanGuang.setOnClickListener(this);
        mLandChaoQing.setOnClickListener(this);
        mLandGaoQing.setOnClickListener(this);
        mLandBiaoQing.setOnClickListener(this);
    }

    /**
     * 更新分辨率
     * @param pos
     */
    public void updateResolution(int pos) {
        updateResolutionInner(pos);
    }

    private void updateResolutionInner(int pos) {
        mLandLanGuang.setTextColor(getSelectedColor(pos, 3));
        mLandChaoQing.setTextColor(getSelectedColor(pos, 2));
        mLandGaoQing.setTextColor(getSelectedColor(pos, 1));
        mLandBiaoQing.setTextColor(getSelectedColor(pos, 0));
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.land_languang:
                if (mListener != null) {
                    updateResolution(3);
                    mListener.OnResolutionSelected(3);
                    dismissDelay();
                }
                break;
            case R.id.land_chaoqing:
                if (mListener != null) {
                    updateResolution(2);
                    mListener.OnResolutionSelected(2);
                    dismissDelay();
                }
                break;
            case R.id.land_gaoqing:
                if (mListener != null) {
                    updateResolution(1);
                    mListener.OnResolutionSelected(1);
                    dismissDelay();

                }
                break;
            case R.id.land_biaoqing:
                if (mListener != null) {
                    updateResolution(0);
                    mListener.OnResolutionSelected(0);
                    dismissDelay();
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

    private OnResolutionListener mListener;

    public void setListener(OnResolutionListener listener) {
        mListener = listener;
    }

    /**
     * 监听器
     */
    public interface OnResolutionListener {

        /**
         * @param pos
         */
        void OnResolutionSelected(int pos);
    }
}
