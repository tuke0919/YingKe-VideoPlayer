package com.yingke.videoplayer.tiktok;

import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.PlayerLog;
import com.yingke.player.java.ScreenScale;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;
import com.yingke.videoplayer.tiktok.adapter.TikTokAdapter;
import com.yingke.videoplayer.tiktok.bean.ListTiktokBean;
import com.yingke.videoplayer.tiktok.player.ListTiktokVideoView;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/20
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TiktokFragment extends BaseFragment implements PagerLayoutManager.OnPageChangedListener {


    public static final String TAG = "TiktokFragment";

    public static TiktokFragment newInstance(){
        return new TiktokFragment();
    }

    private RecyclerView mRecyclerView;
    private List<ListTiktokBean> mTiktokBeans;
    private TikTokAdapter mTikTokAdapter;

    private int mPosition;
    private ListTiktokVideoView mListTiktokVideoView;

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_tiktok;
    }

    @Override
    protected void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        PagerLayoutManager layoutManager = new PagerLayoutManager(getContext(), OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setListener(this);
    }

    @Override
    protected void initData() {
        mTiktokBeans = new ArrayList<>();
        // 抖音列表数据
        String videoListJson = StringUtil.getJsonData(getContext(), "list_tiktok_video.json");

        if (!TextUtils.isEmpty(videoListJson)) {
            List<ListTiktokBean> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListTiktokBean>>() {}.getType());
            for (ListTiktokBean data : listVideoData) {
                File thumbFile = FileUtil.getVideoThumbFile(getContext(), data.getUrl(), EncryptUtils.TIKTOK_VIDEO);
                if (thumbFile.exists()) {
                    data.setCoverImage(thumbFile.getAbsolutePath());
                }
            }
            mTiktokBeans.clear();
            mTiktokBeans.addAll(listVideoData);

            mTikTokAdapter = new TikTokAdapter(getContext(), mTiktokBeans);
            mRecyclerView.setAdapter(mTikTokAdapter);

            // 注册粘性事件
            boolean isRegistered =  EventBus.getDefault().isRegistered(this);
            if (!isRegistered) {
                EventBus.getDefault().register(this);
            }

            // 实例化播放器
            mListTiktokVideoView = new ListTiktokVideoView(getContext());
            mListTiktokVideoView.setScreenScale(ScreenScale.SCREEN_SCALE_CENTER_CROP);
            mListTiktokVideoView.setLooping(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void doEventMainThread(ListTiktokBean videoData) {
        PlayerLog.e(TAG, "doEventMainThread: ");
        if (videoData == null) {
            return;
        }
        int index = mTiktokBeans.indexOf(videoData);
        if (index != -1) {
            mTiktokBeans.set(index, videoData);
            mTikTokAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onFirstPageAttached(View itemView) {
        PlayerLog.e(TAG, "onFirstPageAttached: position = " + 0);
        mPosition = 0;
        startPlay(0, itemView);
    }

    @Override
    public void onPageDetached(boolean isNext, int position, View itemView) {
        PlayerLog.e(TAG, "onPageDetached: position = " + position);

        if (mPosition == position) {
            if (mListTiktokVideoView != null) {
                mListTiktokVideoView.release();
            }
            ListTiktokBean tiktokBean = mTiktokBeans.get(position);
            TiktokVideoVH tiktokVideoVH = new TiktokVideoVH(itemView);
            tiktokVideoVH.removeVideoView(tiktokBean.getCoverImage());
        }
    }

    @Override
    public void onPageSelected(int position, View itemView,  boolean isBottom) {
        PlayerLog.e(TAG, "onPageSelected: position = " + position);
        if (mPosition != position) {
            mPosition = position;
            startPlay(position, itemView);
        }
    }

    /**
     * 开始播放
     * @param position
     */
    public void startPlay(int position,  View itemView){
        if (position < 0 || position >= mTiktokBeans.size()) {
            return;
        }

        // 数据
        ListTiktokBean tiktokBean = mTiktokBeans.get(position);

//        View itemView = mRecyclerView.getChildAt(0);
        TiktokVideoVH tiktokVideoVH = new TiktokVideoVH(itemView);
        tiktokVideoVH.addVideoView(mListTiktokVideoView, tiktokBean.getCoverImage());

        mListTiktokVideoView.setVideoOnline(tiktokBean);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListTiktokVideoView != null && !mListTiktokVideoView.isPlaying()) {
            mListTiktokVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListTiktokVideoView != null) {
            mListTiktokVideoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
