/*******************************************************************************
 * Copyright 2014 Dean Ding.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.yingke.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yingke.widget.R;
import com.yingke.widget.pulltorefresh.internal.FooterLayout;
import com.yingke.widget.pulltorefresh.wrapper.HeaderAndFooterWrapper;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * Support RecyclerView
 *
 * @author Dean.Ding
 *
 */
public class PullToRefreshRecyclerView extends PullToRefreshRecyclerViewBase<RecyclerView> {

    private FooterLayout footerLoadingView;

    private OnMoreListener onMoreListener = null;
    private boolean loadingMore = false;
    private boolean canLoading = false;


    public PullToRefreshRecyclerView(Context context) {
        super(context);
        initFooter(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFooter(context);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
        initFooter(context);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        initFooter(context);
    }

    private void initFooter(Context context){
        footerLoadingView = new FooterLayout(context);
        footerLoadingView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMoreListener != null && !loadingMore && canLoading) {
                    loadingMore = true;
                    setFooterLoading();
                    onMoreListener.onMoreListener();
                }
            }
        });
        setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (onMoreListener != null && !loadingMore && canLoading) {
                    loadingMore = true;
                    setFooterLoading();
                    onMoreListener.onMoreListener();
                }
            }
        });
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView;

        recyclerView = new RecyclerView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerView.setLayoutParams(params);
        recyclerView.setId(R.id.recyclerview);

        return recyclerView;
    }


    @Override
    protected boolean isReadyForPullStart() {
        if (mRefreshableView.getChildCount() <= 0)
            return true;
        int firstVisiblePosition = mRefreshableView.getChildPosition(mRefreshableView.getChildAt(0));
        if (firstVisiblePosition == 0)
            return mRefreshableView.getChildAt(0).getTop() == mRefreshableView.getPaddingTop();
        else
            return false;

    }

    @Override
    protected boolean isReadyForPullEnd() {
        int lastVisiblePosition = mRefreshableView.getChildPosition(mRefreshableView.getChildAt(mRefreshableView.getChildCount() -1));
        if (lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount()-1) {
            return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView.getBottom();
        }
        return false;
    }

    public void setOnLoadMoreListener(OnMoreListener onLoadMoreListener){
        this.onMoreListener = onLoadMoreListener;
    }

    private void setFooterLoading(){
        footerLoadingView.setLoading();
    }

    public void setFooterVisibility(int visibility){
        footerLoadingView.setVisibility(visibility);
    }

    private void setFooterClick(){
        footerLoadingView.showClick();
    }

    public void hideFooter(){
        HeaderAndFooterWrapper adapter = (HeaderAndFooterWrapper)mRefreshableView.getAdapter();
        adapter.removeFootView();
        canLoading = false;
    }

    public void showFooter(){
        HeaderAndFooterWrapper adapter = (HeaderAndFooterWrapper)mRefreshableView.getAdapter();
        adapter.removeFootView();
        adapter.addFootView(footerLoadingView);
        canLoading = true;
    }

    /**
     * 设置endView
     */
    public void setEndView(int layoutRes){
        footerLoadingView.setEndView(layoutRes);
    }


    public void showEnd() {
        HeaderAndFooterWrapper adapter = (HeaderAndFooterWrapper)mRefreshableView.getAdapter();
        adapter.removeFootView();
        adapter.addFootView(footerLoadingView);
        footerLoadingView.showEnd();
        canLoading = false;
    }

    public void showClick() {
        HeaderAndFooterWrapper adapter = (HeaderAndFooterWrapper)mRefreshableView.getAdapter();
        adapter.removeFootView();
        adapter.addFootView(footerLoadingView);
        footerLoadingView.showClick();
        canLoading = true;
    }

    public void setLoadFinish(LoadStatus status){
        loadingMore = false;
        switch(status){
            case SU:
                setFooterLoading();
                break;
            case ERR:
                setFooterClick();
                break;
        }
        // 重置手动下拉刷新数据
        resetManualPullToRefresh();
    }

    public enum LoadStatus{
        SU,ERR
    }
    public interface OnMoreListener{
         void onMoreListener();
    }

}