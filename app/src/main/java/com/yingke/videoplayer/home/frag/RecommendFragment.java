package com.yingke.videoplayer.home.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.ListVideoStickEvent;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.videoplayer.home.landscape.LandScapeActivity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
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
    // 点击视频位置 横竖屏切换用
    private int mPortPosition = -1;

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
                // 设置首播类型
                data.setCurrentType(data.getFirstType());

                File thumbFile = FileUtil.getVideoThumbFile(getContext(), data.getUrl(), EncryptUtils.PORT_REC_VIDEO);
                if (thumbFile.exists()) {
                    data.setThumbPath(thumbFile.getAbsolutePath());
                }

                File thumbAdFile = FileUtil.getVideoThumbFile(getContext(), data.getAdUrl(), EncryptUtils.AD_REC_VIDEO);
                if (thumbAdFile.exists()) {
                    data.setAdThumbPath(thumbAdFile.getAbsolutePath());
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
        SinglePlayerManager.getInstance().enablePip(true);

    }


    @Override
    public void onListVideoPlay(FrameLayout videoContainer, ListVideoData videoData, int position) {
        // 点击播放视频
        mPortPosition = position;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ListIjkVideoView ijkVideoView = new ListIjkVideoView(getContext());
        ijkVideoView.setTag("ijkVideoView");

        SinglePlayerManager.getInstance().attachVideoPlayer(videoData, ijkVideoView);
        // 设置数据
        videoContainer.addView(ijkVideoView, 0,  params);
        ijkVideoView.setVideoOnline(videoData);

    }

    /**
     * 点击位置 横竖屏切换用
     * @return
     */
    public int getPortPosition() {
        return mPortPosition;
    }

    /**
     * @param portPosition
     */
    public void setPortPosition(int portPosition) {
        mPortPosition = portPosition;
    }

    @Override
    public void onMoreClick(ListVideoData videoData) {
        // 分享点击
        SinglePlayerManager.getInstance().startFloatWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void doEventMainThread(ListVideoStickEvent event) {
        PlayerLog.e(TAG, "ListVideoStickEvent: ");
        if (event == null ||  mDataList == null) {
            return;
        }
        ListVideoData videoData = event.mListVideoData;
        boolean isStickAd = event.isStickAd;

        PlayerLog.e(TAG, "doEventMainThread: title = " + videoData.getTitle());
        int index = mDataList.indexOf(videoData);
        if (index != -1) {
            if (!isStickAd) {
                // 更新真实视频封面

                PlayerLog.e(TAG, "doEventMainThread: isStickAd = false");
                ListVideoData realVideoData = mDataList.get(index);
                realVideoData.setThumbPath(videoData.getThumbPath());
                SinglePlayerManager.getInstance().releaseVideoPlayer();
                mAdapter.notifyItemChanged(index);
            } else {
                // 更新广告封面图
                PlayerLog.e(TAG, "doEventMainThread: isStickAd = true");
                ListVideoData realVideoData = mDataList.get(index);
                realVideoData.setAdThumbPath(videoData.getAdThumbPath());
            }
        }
    }

    // 横屏滑动时背后更新竖屏列表数据
    private boolean mIsLandUpdatePort= false;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    public void doEventMainThread(LandScapeActivity.LandScapeVideoEvent event) {
        PlayerLog.e(TAG, "LandScapeVideoEvent: ");
        // 横屏切竖屏 通信
        if (event == null) {
            return;
        }

        int oldPosition = event.getOldPosition();
        IVideoBean landVideoBean = event.getLandVideoBean();
        BaseListVideoView landVideoView = event.getLandVideoView();

        PlayerLog.e(TAG, "oldPosition: " + oldPosition);
        PlayerLog.e(TAG, "landVideoBean: " + landVideoBean.getTitle());

        if (oldPosition != -1) {
            if (landVideoView != null) {
                mIsLandUpdatePort = false;
                PlayerLog.e(TAG, "landVideoView: " + landVideoView);
                // 绑定新的播放器
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(oldPosition);
                if (holder instanceof  ListVideoAdapter.ListVideoHolder) {
                    ListVideoVH listVideoVH = ((ListVideoAdapter.ListVideoHolder) holder).getListVideoVH();
                    if (listVideoVH != null) {
                        listVideoVH.attachVideoPlayer(landVideoView);
                    }

                    // 更新成当前列表
                    SinglePlayerManager.getInstance().attachRecycleView(mRecyclerView);
                    // 开启画中画（可选）
//                    SinglePlayerManager.getInstance().enablePip(true);

                }
            } else {
                // 更新竖屏位置
                mIsLandUpdatePort = true;
                mAdapter.mDataList.set(oldPosition, (ListVideoData) landVideoBean);
                mAdapter.notifyItemChanged(oldPosition);
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        SinglePlayerManager.getInstance().onPause();
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
        if (position <= RecyclerView.NO_POSITION || mIsLandUpdatePort) {
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
                    listVideoVH.attachVideoPlayer(listVideoView);
                }
            }
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        int position  = mRecyclerView.getChildAdapterPosition(view);
        if (position <= RecyclerView.NO_POSITION || mIsLandUpdatePort) {
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
