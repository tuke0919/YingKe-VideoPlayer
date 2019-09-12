package com.yingke.player.java.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/11
 */
public abstract class BaseMediaController extends FrameLayout {

    protected View mRootView;
    // 播放器控制
    protected MediaPlayerControl mMediaPlayer;

    public BaseMediaController( Context context) {
        this(context, null);
    }

    public BaseMediaController( Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView(){
        mRootView = LayoutInflater.from(getContext()).inflate(getLayoutResId(), this);
        setClickable(true);
        setFocusable(true);
    }

    /**
     * 资源id
     */
    protected abstract int getLayoutResId();


}
