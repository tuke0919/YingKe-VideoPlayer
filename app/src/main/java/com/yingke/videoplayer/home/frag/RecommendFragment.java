package com.yingke.videoplayer.home.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.videoplayer.home.util.SinglePlayerManager;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.StringUtil;
import com.yingke.videoplayer.home.player.ListIjkVideoView;
import com.yingke.videoplayer.widget.BaseListVideoView;
import com.yingke.widget.base.BaseRecycleViewAdapter;
import com.yingke.widget.pulltorefresh.PullToRefreshBase;
import com.yingke.widget.pulltorefresh.fragment.BaseRecyclerViewFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/23
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class RecommendFragment extends BaseRecyclerViewFragment<ListVideoData> implements
        ListVideoAdapter.OnListVideoClickListener,
        RecyclerView.OnChildAttachStateChangeListener {

    public static final String TAG = "RecommendFragment";

    public static RecommendFragment newInstance(){
        return new RecommendFragment();
    }

    private boolean isInited = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_recommend;
    }

    @Override
    protected BaseRecycleViewAdapter<ListVideoData> initAdapter() {
        return new ListVideoAdapter(getContext());
    }

    @Override
    protected void initData() {
        mRefreshView.setMode(PullToRefreshBase.Mode.DISABLED);
        if (mAdapter instanceof ListVideoAdapter) {
            ((ListVideoAdapter) mAdapter).setListener(this);
        }
        mRecyclerView.addOnChildAttachStateChangeListener(this);

        loadData(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isInited) {
            return;
        }

        if (!isVisibleToUser ) {
            SinglePlayerManager.getInstance().onPause();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isInited = true;
    }

    @Override
    protected void onPullToRefresh() {

    }

    @Override
    protected void onLoadMoreRefresh() {

    }

    @Override
    protected void requestNetWork() {
        // 假装有网络
        String videoListJson = StringUtil.getJsonData(getContext(), "list_rec_video.json");

        if (!TextUtils.isEmpty(videoListJson)) {
            List<ListVideoData> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListVideoData>>() {}.getType());
            for (ListVideoData data : listVideoData) {
                File thumbFile = FileUtil.getVideoThumbFile(getContext(), EncryptUtils.md5String(data.getUrl()));
                if (thumbFile.exists()) {
                    data.setThumbPath(thumbFile.getAbsolutePath());
                }
            }
            mDataList.clear();
            mDataList.addAll(listVideoData);
            notifyDataSetChanged();

            // 注册粘性事件
            boolean isRegistered =  EventBus.getDefault().isRegistered(this);
            if (!isRegistered) {
                EventBus.getDefault().register(this);
            }
        }

        // 去掉加载更多
        if (TextUtils.isEmpty(mCursor)) {
            mRefreshView.hideFooter();
        } else {
            mRefreshView.showFooter();
        }
        // 绑定列表
        SinglePlayerManager.getInstance().attachRecycleView(mRecyclerView);
        // 开启悬浮窗
        SinglePlayerManager.getInstance().enablePip(getContext(), true);

    }

    @Override
    public void onListVideoPlay(View rootView, FrameLayout videoContainer, ListVideoData videoData) {
        // 点击播放视频

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ListIjkVideoView ijkVideoView = new ListIjkVideoView(getContext());
        ijkVideoView.setTag("ijkVideoView");

        SinglePlayerManager.getInstance().attachVideoPlayer(videoData, ijkVideoView);
        // 设置数据
        videoContainer.addView(ijkVideoView, 0,  params);
        ijkVideoView.setVideoOnline(videoData);

    }

    @Override
    public void onMoreClick(ListVideoData videoData) {
        // 分享点击
        SinglePlayerManager.getInstance().startFloatWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void doEventMainThread(ListVideoData videoData) {
        PlayerLog.e(TAG, "doEventMainThread: ");
        if (videoData == null ||  mDataList == null) {
            return;
        }
        PlayerLog.e(TAG, "doEventMainThread: " + "\n"
                + "title = " + videoData.getTitle() + "\n"
                + "thumbPath = " + videoData.getThumbPath());

        int index = mDataList.indexOf(videoData);
        if (index != -1) {
            mDataList.set(index, videoData);

            SinglePlayerManager.getInstance().releaseVideoPlayer();
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SinglePlayerManager.getInstance().onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        if (SinglePlayerManager.getInstance().isPipEnable()) {
            SinglePlayerManager.getInstance().stopFloatWindow();
        }
        SinglePlayerManager.getInstance().releaseVideoPlayer();
        SinglePlayerManager.getInstance().reset();
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        int position  = mRecyclerView.getChildAdapterPosition(view);
        if (position <= RecyclerView.NO_POSITION) {
            return;
        }
        ListVideoData videoBean = mDataList.get(position);
        if (videoBean.equals(SinglePlayerManager.getInstance().getCurrentVideoBean())) {
            // 当前正在播放
            if (SinglePlayerManager.getInstance().isEnableAndShowing()) {
                // 有显示悬浮窗

                // 停止悬浮窗
                SinglePlayerManager.getInstance().stopFloatWindow(false);
                // 插入列表
                ListVideoVH listVideoVH = getListVideoVH(view);
                if (listVideoVH != null) {
                    BaseListVideoView listVideoView = SinglePlayerManager.getInstance().getCurrentListVideoView();
                    listVideoVH.addVideoPlayer(listVideoView);
                }
            }
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        int position  = mRecyclerView.getChildAdapterPosition(view);
        if (position <= RecyclerView.NO_POSITION) {
            return;
        }

        ListVideoData videoBean = mDataList.get(position);
        if (videoBean.equals(SinglePlayerManager.getInstance().getCurrentVideoBean())) {
            // 当前正在播放

            if (SinglePlayerManager.getInstance().isPipEnable()) {

                if (!SinglePlayerManager.getInstance().isShowing()) {
                    // 有显示悬浮窗
                    SinglePlayerManager.getInstance().startFloatWindow();
                }

            } else {
                // 没有显示悬浮窗
                SinglePlayerManager.getInstance().releaseVideoPlayer();
            }
        }

    }

    /**
     *
     * @param itemView Holder根布局
     * @return
     */
    public ListVideoVH getListVideoVH(View itemView) {
        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(itemView);
        if (holder instanceof ListVideoAdapter.ListVideoHolder) {
            ListVideoVH listVideoVH = ((ListVideoAdapter.ListVideoHolder) holder).getListVideoVH();
            return listVideoVH;
        }
        return null;
    }
}
