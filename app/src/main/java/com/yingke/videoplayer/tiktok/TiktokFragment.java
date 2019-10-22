package com.yingke.videoplayer.tiktok;

import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;
import com.yingke.videoplayer.home.bean.ListVideoData;
import com.yingke.videoplayer.tiktok.bean.ListTiktokBean;
import com.yingke.videoplayer.util.EncryptUtils;
import com.yingke.videoplayer.util.FileUtil;
import com.yingke.videoplayer.util.FrescoUtil;
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
public class TiktokFragment extends BaseFragment {

    public static final String TAG = "TiktokFragment";

    public static TiktokFragment newInstance(){
        return new TiktokFragment();
    }

    private RecyclerView mRecyclerView;
    private List<ListTiktokBean> mTiktokBeans;
    private TikTokAdapter mTikTokAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_tiktok;
    }

    @Override
    protected void initView(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        PagerLayoutManager layoutManager = new PagerLayoutManager(getContext(), OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void initData() {
        mTiktokBeans = new ArrayList<>();
        // 抖音列表数据
        String videoListJson = StringUtil.getJsonData(getContext(), "list_tiktok_video.json");

        if (!TextUtils.isEmpty(videoListJson)) {
            List<ListTiktokBean> listVideoData = new Gson().fromJson(videoListJson, new TypeToken<List<ListTiktokBean>>() {}.getType());
            for (ListTiktokBean data : listVideoData) {
                File thumbFile = FileUtil.getVideoThumbFile(getContext(), EncryptUtils.md5String(data.getUrl()));
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
