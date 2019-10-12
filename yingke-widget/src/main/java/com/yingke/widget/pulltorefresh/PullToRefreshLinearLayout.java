package com.yingke.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yingke.widget.R;

public class PullToRefreshLinearLayout extends PullToRefreshBase<LinearLayout> {

	public PullToRefreshLinearLayout(Context context) {
		super(context);
	}

	public PullToRefreshLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshLinearLayout(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshLinearLayout(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected LinearLayout createRefreshableView(Context context, AttributeSet attrs) {
		LinearLayout layout= new LinearLayout(context, attrs);
		layout.setId(R.id.linearlayout);
		return layout;
	}

	@Override
	protected boolean isReadyForPullStart() {
		LinearLayout container = getRefreshableView();
		for (int i = 0; i < container.getChildCount(); i++){
			View child = container.getChildAt(i);
			if (child.getVisibility() == View.VISIBLE &&
					child instanceof ListView){
				ListView lv = (ListView) child;
				if (lv.getChildCount() == 0){
					return true;
				}else{
					return lv.getChildAt(0).getTop() == 0;
				}
			}
		}
		return true;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		return false;
	}
}
