package com.yingke.videoplayer.home.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yingke.player.java.controller.MediaController;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.util.SinglePlayerManager;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：带画中画 的控制器
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-25
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListIjkMediaController extends MediaController {

    private RelativeLayout mPipView;
    private ImageView mFullView;
    private ImageView mCloseView;

    public ListIjkMediaController(Context context) {
        super(context);
    }

    public ListIjkMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListIjkMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.pip_media_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mPipView =  mRootView.findViewById(R.id.controller_top_view_pip);
        mFullView =  mRootView.findViewById(R.id.controller_port_pip_full);
        mCloseView =  mRootView.findViewById(R.id.controller_port_pip_close);
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SinglePlayerManager.getInstance().stopFloatWindow();
            }
        });
        mFullView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mPipView.setVisibility(GONE);
    }

    /**
     * 画中画控制器
     */
    public void showPipController() {
        mTopView.setVisibility(GONE);
        mBottomView.setVisibility(GONE);
        mPipView.setVisibility(VISIBLE);
    }

    /**
     * 普通控制器
     */
    public void showNormalController() {
        mTopView.setVisibility(VISIBLE);
        mBottomView.setVisibility(VISIBLE);
        mPipView.setVisibility(GONE);
    }


}
