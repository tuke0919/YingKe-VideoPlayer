package com.yingke.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;


/**
 * Created by wangdp on 2016/11/18.
 */

public class TabListView extends ListView implements AbsListView.OnScrollListener {

    private float oldY;

    private int topHeight;

    private int paddingTop;

    private View tabHeader;

    private OnTabStatusChange onTabStatusChange = null;

    private boolean showTab = false;

    public TabListView(Context context) {
        super(context);
        init();
    }

    public TabListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);
        int action = event.getAction();
        String a = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                paddingTop = paddingTop + (int) (event.getY() - oldY);
                if (paddingTop < -topHeight) {
                    paddingTop = -topHeight;
                }
                if (paddingTop > 0) {
                    paddingTop = 0;
                }
                setPadding(0, paddingTop, 0, 0);
                oldY = event.getY();
                a = "move";
                break;
        }
        Log.d("scrolltest", "MyScrollView onTouchEvent : " + a + "   " + b + "  paddingTop " + paddingTop + "  topHeight: " + topHeight + "  oldY:" + oldY + "  event.getY():" + event.getY());
        return b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean b = super.onInterceptTouchEvent(event);
        int action = event.getAction();
        String a = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                a = "down";
                break;
            case MotionEvent.ACTION_CANCEL:
                a = "cancel";
                break;
            case MotionEvent.ACTION_UP:
                a = "up";
                break;
            case MotionEvent.ACTION_MOVE:
                a = "move";
                break;
        }
        Log.d("scrolltest", "MyScrollView onInterceptTouchEvent : " + a + "   " + b);
        return b;
    }

    public void setTopHeight(int h) {
        topHeight = h;
    }

    public int getTopY() {
        View c = getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        if (top < -topHeight)
            return -topHeight;
        if (top > 0)
            return 0;
        return top;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (scrollState == 0) {
            paddingTop = getTopY();
            setPadding(0, paddingTop, 0, 0);
            Log.d("scrolltest", "MyScrollView onScrollStateChanged : " + paddingTop);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d("scrolltest", "MyScrollView onScroll -getTopY(): " + -getTopY() + "  topHeight: " + topHeight + " firstVisibleItem: " + firstVisibleItem + " getChildAt(0):" + getChildAt(0) + " header: " + tabHeader + " :" + getHeaderViewsCount());
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (-getTopY() >= topHeight && firstVisibleItem <= 1 && !showTab) {
            if (onTabStatusChange != null)
                onTabStatusChange.onShowTab();
            showTab = true;
        } else if (-getTopY() < topHeight && firstVisibleItem <= 1 && showTab) {
            if (onTabStatusChange != null)
                onTabStatusChange.onHideTab();
            showTab = false;
        }
    }


    public interface OnTabStatusChange {
        void onShowTab();

        void onHideTab();
    }

    public void setOnTabChangeListener(OnTabStatusChange l) {
        onTabStatusChange = l;
    }


    OnScrollListener onScrollListener;

    public void setOnTabScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

}
