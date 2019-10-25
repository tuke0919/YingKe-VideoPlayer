package com.yingke.videoplayer.tiktok.player;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.controller.MediaController;
import com.yingke.player.java.videoview.IjkVideoView;
import com.yingke.videoplayer.widget.BaseListVideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-21
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListTiktokVideoView extends BaseListVideoView {

    public ListTiktokVideoView(@NonNull Context context) {
        super(context);
        initView();
        initData();
    }

    public ListTiktokVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
    }

    public ListTiktokVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
    }

    public void initView(){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mIjkVideoView = new IjkVideoView(getContext());
        addView(mIjkVideoView, params);
    }

    public void setLooping(boolean looping){
        if (mIjkVideoView != null) {
            mIjkVideoView.setLooping(looping);
        }
    }

    public void initData(){
        initVideoPlayerView();
    }

    @Override
    protected boolean hasController() {
        return false;
    }

    @Override
    public MediaController getControllerView() {
        return null;
    }

    @Override
    public IjkVideoView getIjkVideoView() {
        return mIjkVideoView;
    }

    @Override
    protected void showPlayingView() {

    }

    @Override
    protected void showErrorView() {
        Toast.makeText(getContext(), "网络不可用", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void showCompletionView() {

    }

    @Override
    protected void showLoadingView() {

    }

    @Override
    protected void showNetTipView() {
        Toast.makeText(getContext(), "您正在使用4G网络播放", Toast.LENGTH_SHORT).show();
    }
}
