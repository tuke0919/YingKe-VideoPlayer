package com.yingke.videoplayer.home.util;

import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yingke.player.java.IVideoBean;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.item.ListVideoVH;
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
        releaseVideoPlayer();
        mCurrentVideoBean = bean;
        mCurrentListVideoView = listVideoView;
    }

    /**
     * 释放播放器
     */
    public void releaseVideoPlayer() {
        if (mCurrentListVideoView != null) {
            removePlayer();
            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        }
    }

    /**
     * 移除播放器
     */
    private void removePlayer() {
        ViewParent parent = mCurrentListVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ViewParent itemView = parent.getParent();
            if (itemView != null) {
                showIdleView((View) itemView);
            }
        }
    }

    /**
     * 显示空闲页
     * @param itemView
     */
    private void showIdleView(View itemView) {
        if (itemView == null) {
            return;
        }
        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(itemView);
        if (holder instanceof ListVideoAdapter.ListVideoHolder) {
            ListVideoVH listVideoVH = ((ListVideoAdapter.ListVideoHolder) holder).getListVideoVH();
            listVideoVH.showIdleView();
        }
    }

    /**
     * @return 当前播放数据
     */
    public IVideoBean getCurrentVideoBean() {
        return mCurrentVideoBean;
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


    public void reset() {
        mCurrentListVideoView = null;
        mCurrentVideoBean = null;
        mRecyclerView = null;
    }

}
