package com.yingke.widget.pulltorefresh.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yingke.widget.R;
import com.yingke.widget.base.BaseRecycleViewAdapter;
import com.yingke.widget.pulltorefresh.PullToRefreshBase;
import com.yingke.widget.pulltorefresh.PullToRefreshRecyclerView;
import com.yingke.widget.pulltorefresh.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 功能：PullToRefreshRecyclerView 代码实例
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/7
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class BaseRecyclerViewFragment<T> extends Fragment {

    protected View mRootView;
    protected PullToRefreshRecyclerView mRefreshView;
    protected RecyclerView mRecyclerView;

    protected BaseRecycleViewAdapter<T> mAdapter;
    protected List<T> mDataList;
    protected String mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResId(), container, false);
            findViews(mRootView);
            initViews();
            initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    /**
     * 资源id
     */
    protected abstract int getLayoutResId();

    /**
     * @param view
     */
    protected void findViews(View view) {
        // id固定为refresh_view
        mRefreshView = (PullToRefreshRecyclerView) view.findViewById(R.id.refresh_view);
    }

    /**
     * 初始化布局
     */
    protected void initViews() {
        initRefreshView();
    }

    /**
     * 断网重试
     */
    protected void onRetryListener(){
        loadData(true);
    }

    /**
     * 初始化 列表
     */
    protected void initRefreshView() {
        mRefreshView.setScrollingWhileRefreshingEnabled(true);
        mRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refreshView.setRefreshing();
                onPullToRefresh();
            }
        });
        mRefreshView.setOnLoadMoreListener(new PullToRefreshRecyclerView.OnMoreListener() {
            @Override
            public void onMoreListener() {
                onLoadMoreRefresh();
            }
        });
        mRecyclerView = mRefreshView.getRefreshableView();

        mDataList = new ArrayList<>();
        mAdapter = initAdapter();
        mAdapter.setDataList(mDataList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        HeaderAndFooterWrapper headerAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);

        if (hasHeader()) {
            headerAndFooterWrapper.addHeaderView(createHeaderView());
        }
        mRecyclerView.setAdapter(headerAndFooterWrapper);
    }

    /**
     * 是否显示加载页面
     */
    protected boolean isShowLoadingView() {
        return true;
    }

    /**
     * 初始化 适配器
     * @return
     */
    protected abstract BaseRecycleViewAdapter<T> initAdapter();

    /**
     * @return 是否有header
     */
    protected boolean hasHeader(){
        return false;
    }

    /**
     * @return header view
     */
    protected View createHeaderView(){
        return null;
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();


    /**
     * 下拉刷新
     */
    protected abstract void onPullToRefresh();

    /**
     * 上拉加载
     */
    protected abstract void onLoadMoreRefresh();

    /**
     * 获取网络 数据
     * @param refresh 是否刷新
     */
    protected void loadData(boolean refresh) {
        if (refresh) {
            mCursor = "";
            if (mRefreshView != null) {
                mRefreshView.showFooter();
            }
            if (mDataList != null && mDataList.size() == 0 && isShowLoadingView()) {
                showLoading();
            }
        }

        // 真正的加载网络
        requestNetWork();
    }

    /**
     * 请求网络数据
     */
    protected abstract void requestNetWork();


    /**
     * 显示loading
     */
    protected void showLoading() {

    }

    /**
     * 隐藏 loading
     */
    protected void hideLoading() {

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * 更新列表
     *
     * @param list    新增的列表
     * @param refresh 是否是重新加载
     */
    protected void updateListView(List<T> list, boolean refresh) {
        if (refresh) {
            mDataList.clear();
        }
        if (list != null) {
            mDataList.addAll(list);
        }
        // 刷新UI
        notifyDataSetChanged();
        if (isShowLoadingView()) {
            if (mDataList.size() != 0) {
                hideLoading();
            } else {
                showNoDataList();
            }
        }
    }

    /**
     * 刷新数据
     */
    protected void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 无列表数据提示
     */
    protected void showNoDataList() {

    }

    /**
     * 网络错误
     */
    protected void showNetErr() {

    }


    @Override
    public void onPause() {
        super.onPause();
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
