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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import com.yingke.widget.R;
import com.yingke.widget.pulltorefresh.internal.EmptyViewMethodAccessor;
import com.yingke.widget.pulltorefresh.internal.FooterLayout;
import com.yingke.widget.pulltorefresh.internal.LoadingLayout;

public class PullToRefreshExpandableListView extends PullToRefreshAdapterViewBase<ExpandableListView> {

	private LoadingLayout mHeaderLoadingView;
	private LoadingLayout mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;
	private FooterLayout footerLoadingView;

	private OnMoreListener onMoreListener = null;
	private boolean loadingMore = false;
	private boolean canLoading = false;

	private boolean mAutoRefresh = false;

	public PullToRefreshExpandableListView(Context context) {
		super(context);
		initFooter(context);
	}

	public PullToRefreshExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFooter(context);
	}

	public PullToRefreshExpandableListView(Context context, Mode mode) {
		super(context, mode);
		initFooter(context);
	}

	public PullToRefreshExpandableListView(Context context, Mode mode, AnimationStyle style) {
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
	protected ExpandableListView createRefreshableView(Context context, AttributeSet attrs) {
		final ExpandableListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalExpandableListViewSDK9(context, attrs);
		} else {
			lv = new InternalExpandableListView(context, attrs);
		}

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);

		mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

		if (mListViewExtrasEnabled) {
			final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			FrameLayout frame = new FrameLayout(getContext());
			mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
			mHeaderLoadingView.setVisibility(View.GONE);
			frame.addView(mHeaderLoadingView, lp);
			mRefreshableView.addHeaderView(frame, null, false);

			mLvFooterLoadingFrame = new FrameLayout(getContext());
			mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
			mFooterLoadingView.setVisibility(View.GONE);
			mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
				setScrollingWhileRefreshingEnabled(true);
			}
		}
	}


	@TargetApi(9)
	final class InternalExpandableListViewSDK9 extends InternalExpandableListView {

		public InternalExpandableListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshExpandableListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	protected class InternalExpandableListView extends ExpandableListView implements EmptyViewMethodAccessor {

		private boolean mAddedLvFooter = false;

		public InternalExpandableListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
				addFooterView(mLvFooterLoadingFrame, null, false);
				mAddedLvFooter = true;
			}

			super.setAdapter(adapter);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshExpandableListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

	}


	public void setOnLoadMoreListener(PullToRefreshExpandableListView.OnMoreListener onLoadMoreListener){
		this.onMoreListener = onLoadMoreListener;
	}

	private void setFooterLoading(){
		footerLoadingView.setLoading();
	}

	private void setFooterClick(){
		footerLoadingView.showClick();
	}

	/**
	 * 设置endView
	 */
	public void setEndView(int layoutRes){
		footerLoadingView.setEndView(layoutRes);
	}

	private void setFooterEnd(){
		canLoading = false;
		footerLoadingView.showEnd();
	}

	public void hideFooter(){
		getRefreshableView().removeFooterView(footerLoadingView);
		canLoading = false;
	}

	public void showFooter(){
		getRefreshableView().removeFooterView(footerLoadingView);
		getRefreshableView().addFooterView(footerLoadingView);
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
			case END: //已显示全部
				setFooterEnd();
				break;
		}
		// 重置手动下拉刷新数据
		resetManualPullToRefresh();
	}

	public enum LoadStatus{
		SU,ERR,END
	}
	public interface OnMoreListener{
		public void onMoreListener();
	}

	public void setAutoRefresh(boolean autoRefresh){
		mAutoRefresh=autoRefresh;
		setRefreshing();
	}
}
