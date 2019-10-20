package com.yingke.videoplayer.home;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;
import com.yingke.videoplayer.bean.VideoBean;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.PlayerUtil;
import com.yingke.videoplayer.util.StringUtil;
import com.yingke.videoplayer.widget.ListIjkVideoView;
import com.yingke.widget.base.BaseRecycleViewAdapter;
import com.yingke.widget.pulltorefresh.PullToRefreshBase;
import com.yingke.widget.pulltorefresh.fragment.BaseRecyclerViewFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

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
public class RecommendFragment extends BaseRecyclerViewFragment<ListVideoData> implements ListVideoAdapter.OnListVideoClickListener {

    public static final String TAG = "RecommendFragment";

    public static RecommendFragment newInstance(){
        return new RecommendFragment();
    }

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


//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        ListIjkVideoView ijkVideoView = new ListIjkVideoView(getContext());
//        mContainer.addView(ijkVideoView, params);
//        VideoBean videoBean = new VideoBean();
//        ijkVideoView.setVideoOnline(videoBean);

        loadData(true);
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
//        String videoListJson = getString(R.string.list_video_bean_json);
        String videoListJson = StringUtil.getJsonData(getContext(), "listvideojson.json");

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
    }

    @Override
    public void onListVideoPlay(FrameLayout videoContainer) {
        // 点击

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
            mAdapter.notifyItemChanged(index);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
