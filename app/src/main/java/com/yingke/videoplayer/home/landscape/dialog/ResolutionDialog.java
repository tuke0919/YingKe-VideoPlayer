package com.yingke.videoplayer.home.landscape.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.util.DeviceUtil;

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
public class ResolutionDialog extends HalfTransDialog {

    FrameLayout mMainContent;

    public ResolutionDialog(@NonNull Context context) {
        super(context);
        initView();
        initData();
    }

    @Override
    protected void adjustParams(View rootView) {
        super.adjustParams(rootView);
        mMainContent = rootView.findViewById(R.id.trans_view);
        ViewGroup.LayoutParams params = mMainContent.getLayoutParams();
        params.width = DeviceUtil.dip2px(getContext(), 200);
        mMainContent.setLayoutParams(params);
    }

    private void initView() {
        View resolutionView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_land_resolution, mMainContent, false);
        mMainContent.addView(resolutionView);
    }

    private void initData() {

    }





}
