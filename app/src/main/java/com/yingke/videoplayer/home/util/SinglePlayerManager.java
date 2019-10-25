package com.yingke.videoplayer.home.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yingke.player.java.IVideoBean;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.videoplayer.home.pip.SuspensionView;
import com.yingke.videoplayer.widget.BaseListVideoView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：单视频管理器
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-23
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class SinglePlayerManager {


    public static SinglePlayerManager getInstance() {
        return INSTANCE.instance;
    }

    private static class INSTANCE{
        public static SinglePlayerManager instance = new SinglePlayerManager();
    }

    private RecyclerView mRecyclerView;
    private IVideoBean mCurrentVideoBean;
    private BaseListVideoView mCurrentListVideoView;

    public void attachRecycleView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        if (recyclerView == null) {
            throw new IllegalArgumentException("recyclerView in manager must nt be null!");
        }
    }

    public void attachVideoPlayer(IVideoBean bean, BaseListVideoView listVideoView) {

        if (isEnableAndShowing()) {
            stopFloatWindow(true);
        }
        releaseVideoPlayer();
        mCurrentVideoBean = bean;
        mCurrentListVideoView = listVideoView;
    }

    /**
     * 释放播放器
     */
    public void releaseVideoPlayer() {
        if (mCurrentListVideoView != null) {

            if (isEnableAndShowing() ) {
                return;
            }

            removePlayer();
            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        }
    }

    /**
     * 移除播放器
     */
    private void removePlayer() {
        if (mCurrentListVideoView == null) {
            return;
        }
        ViewParent parent = mCurrentListVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ViewParent itemView = parent.getParent();
            if (itemView != null) {
                showIdleView((View) itemView, true);
            }
        }
    }

    /**
     * 移除但不释放 播放器
     */
    private void removePlayerNotRelease() {
        if (mCurrentListVideoView == null) {
            return;
        }
        ViewParent videoParent = mCurrentListVideoView.getParent();
        if (videoParent instanceof ViewGroup) {
            ViewParent itemView = videoParent.getParent();
            if (itemView != null) {
                showIdleView((View) itemView, false);
            }
        }
    }

    /**
     * 显示空闲页
     * @param itemView
     * @param releasePlayer
     */
    private void showIdleView(View itemView, boolean releasePlayer) {
        if (itemView == null) {
            return;
        }
        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(itemView);
        if (holder instanceof ListVideoAdapter.ListVideoHolder) {
            ListVideoVH listVideoVH = ((ListVideoAdapter.ListVideoHolder) holder).getListVideoVH();
            listVideoVH.showIdleView(releasePlayer);
        }
    }

    /**
     * @return 当前播放数据
     */
    public IVideoBean getCurrentVideoBean() {
        return mCurrentVideoBean;
    }

    /**
     * @return 当前播放器
     */
    public BaseListVideoView getCurrentListVideoView() {
        return mCurrentListVideoView;
    }

    /**
     * 播放
     */
    public void onResume() {
        if (mCurrentListVideoView != null
                && mCurrentListVideoView.isInPlaybackState()
                && !mCurrentListVideoView.isPlaying()) {
            mCurrentListVideoView.start();
        }
    }

    /**
     * 暂停
     */
    public void onPause(){
        if (mCurrentListVideoView != null && mCurrentListVideoView.isPlaying()) {
            mCurrentListVideoView.pause();
        }
    }

    //#### 画中画

    private boolean isSuspensionEnable = false;
    private boolean mIsShowing = false;
    private SuspensionView mSuspensionView;

    /**
     * 启动悬浮窗
     * @param context
     * @param suspensionEnable
     */
    public void enableSuspensionWindow(Context context, boolean suspensionEnable) {
        isSuspensionEnable = suspensionEnable;
        if (isSuspensionEnable) {
            mSuspensionView = new SuspensionView(context);
        }
    }

    public boolean isSuspensionEnable() {
        return isSuspensionEnable;
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * @return 悬浮窗显示
     */
    public boolean isEnableAndShowing(){
        return isSuspensionEnable() && isShowing();
    }

    /**
     * 开始悬浮窗
     */
    public void startFloatWindow() {
        if (!isSuspensionEnable || mIsShowing || mCurrentListVideoView == null) {
            return;
        }
        mIsShowing = true;

        removePlayerNotRelease();

        mSuspensionView.addView(mCurrentListVideoView);
        mSuspensionView.attachToWindow();
    }

    /**
     * 关闭悬浮窗
     */
    public void stopFloatWindow(boolean releasePlayer) {
        if (!isSuspensionEnable || !mIsShowing || mCurrentListVideoView == null) {
            return;
        }
        mIsShowing = false;
        if (releasePlayer) {
            mCurrentListVideoView.release();
            mSuspensionView.removeAllViews();
            mSuspensionView.detachFromWindow();
            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        } else {
            mSuspensionView.removeAllViews();
            mSuspensionView.detachFromWindow();
        }

    }







    public void reset() {
        mCurrentListVideoView = null;
        mCurrentVideoBean = null;
        mRecyclerView = null;
    }

}
