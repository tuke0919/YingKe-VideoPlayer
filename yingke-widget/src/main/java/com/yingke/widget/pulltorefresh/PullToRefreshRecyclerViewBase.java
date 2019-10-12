/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
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
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.yingke.widget.pulltorefresh.wrapper.HeaderAndFooterWrapper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Support RecyclerView LoadMore
 *
 * @author bjmadewei
 */
public abstract class PullToRefreshRecyclerViewBase<T extends RecyclerView> extends PullToRefreshBase<T> {

    private boolean mLastItemVisible;
    private OnLastItemVisibleListener mOnLastItemVisibleListener;

    private RecyclerView.LayoutManager mLayoutManager;

    public PullToRefreshRecyclerViewBase(Context context) {
        super(context);
        mRefreshableView.addOnScrollListener(mOnScrollListener);
    }

    public PullToRefreshRecyclerViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRefreshableView.addOnScrollListener(mOnScrollListener);
    }

    public PullToRefreshRecyclerViewBase(Context context, Mode mode) {
        super(context, mode);
        mRefreshableView.addOnScrollListener(mOnScrollListener);
    }

    public PullToRefreshRecyclerViewBase(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        mRefreshableView.addOnScrollListener(mOnScrollListener);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
                mOnLastItemVisibleListener.onLastItemVisible();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mLayoutManager == null) {
                mLayoutManager = recyclerView.getLayoutManager();
                return;
            }
            /**
             * Set whether the Last Item is Visible. lastVisibleItemIndex is a
             * zero-based index, so we minus one totalItemCount to check
             */
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItem;
            int visibleItemCount = mLayoutManager.getChildCount();
            if (mLayoutManager instanceof GridLayoutManager) {
                firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                firstVisibleItem = findMax(((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(null));
            } else {
                firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            }
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        }
    };


    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
     * getRefreshableView()}.
     * {@link AdapterView#setAdapter(Adapter)}
     * setAdapter(adapter)}. This is just for convenience!
     *
     * @param adapter - Adapter to set
     */
    public void setAdapter(HeaderAndFooterWrapper adapter) {
        mRefreshableView.setAdapter(adapter);
    }


    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private boolean isFirstItemVisible() {

        final RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
            }
            return true;

        } else {
            int firstVisiblePosition;
            if (mLayoutManager instanceof GridLayoutManager) {
                firstVisiblePosition = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                firstVisiblePosition = findMax(((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(null));
            } else {
                firstVisiblePosition = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
            }
            /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (firstVisiblePosition <= 1) {
                final View firstVisibleChild = mRefreshableView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {

        final RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return true;
        } else {
            int lastVisiblePosition = 0;
            if (mLayoutManager instanceof GridLayoutManager) {
                lastVisiblePosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] positions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                lastVisiblePosition = positions[positions.length - 1];
            } else {
                lastVisiblePosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }
            final int lastItemPosition = mRefreshableView.getChildCount() - 1;

            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
                        + lastVisiblePosition);
            }

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                int firstVisiblePosition = 0;
                if (mLayoutManager instanceof GridLayoutManager) {
                    firstVisiblePosition = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                    firstVisiblePosition = findMax(((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(null));
                } else {
                    firstVisiblePosition = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                }
                final int childIndex = lastVisiblePosition - firstVisiblePosition;
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }
        return false;
    }

}
