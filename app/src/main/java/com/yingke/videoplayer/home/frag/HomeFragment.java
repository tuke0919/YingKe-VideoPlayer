package com.yingke.videoplayer.home.frag;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;
import com.yingke.videoplayer.home.adapter.TopTabAdapter;
import com.yingke.videoplayer.home.bean.TopTabData;
import com.yingke.videoplayer.widget.ObservableXTabLayout;

import java.util.List;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/16
 */
public class HomeFragment extends BaseFragment {


    private LinearLayout mSearchView;
    private TextView mSearchText;

    private ObservableXTabLayout mXTabLayout;
    private ViewPager mViewPager;
    private TopTabAdapter mTabAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_home;
    }

    @Override
    protected void initView(View rootView) {
        mSearchView = mRootView.findViewById(R.id.search_view);
        mSearchText = mRootView.findViewById(R.id.search_text);
        mXTabLayout = mRootView.findViewById(R.id.tab_layout);
        mViewPager = mRootView.findViewById(R.id.home_view_pager);
    }

    @Override
    protected void initData() {
        mTabAdapter = new TopTabAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mXTabLayout.setupWithViewPager(mViewPager);

        refreshTopData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshTopData(){
        String topDataJson = TopTabData.mTopDataJson;
        if (!TextUtils.isEmpty(topDataJson)) {
            List<TopTabData> tabDataList = new Gson().fromJson(topDataJson, new TypeToken<List<TopTabData>>(){}.getType());
            notifyTabDataSetChanged(tabDataList);
        } else {
            notifyTabDataSetChanged(null);
        }
        mViewPager.setCurrentItem(1);
    }


    private void notifyTabDataSetChanged(List<TopTabData> topTabData) {
        if (mTabAdapter == null) {
            return;
        }
        mTabAdapter.setTopTabData(topTabData);
        mTabAdapter.notifyDataSetChanged();
    }

    /**
     * @return 当前fragment
     */
    public Fragment getCurrentFragment() {
       return mTabAdapter.getCurrentFragment();
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
    }
}
