package com.yingke.videoplayer.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.yingke.videoplayer.widget.xtablayout.XTabLayout;

/**
 * Created by libowen on 2018/11/14.
 */
public class ObservableXTabLayout extends XTabLayout {

    public ObservableXTabLayout(Context context) {
        super(context);
    }

    public ObservableXTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableXTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ScrollViewListener scrollViewListener = null;


    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(XTabLayout scrollView, int x, int y, int oldx, int oldy);
    }

}
