package com.yingke.videoplayer.tiktok;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-21
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class PagerLayoutManager extends LinearLayoutManager implements RecyclerView.OnChildAttachStateChangeListener {

    private PagerSnapHelper mPagerSnapHelper;
    private RecyclerView mRecyclerView;
    private OnPageChangedListener mListener;
    // 位移，用来判断移动方向
    private int mScrollBy;

    public PagerLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        init();
    }

    public PagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    public PagerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = view;
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnChildAttachStateChangeListener(this);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        mScrollBy = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        mScrollBy = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                View viewIdle = mPagerSnapHelper.findSnapView(this);
                int positionIdle = getPosition(viewIdle);
                // 页面选中
                if (mListener != null && getChildCount() == 1) {
                    mListener.onPageSelected(positionIdle, positionIdle == getItemCount() - 1);
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                View viewDrag = mPagerSnapHelper.findSnapView(this);
                int positionDrag = getPosition(viewDrag);
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                break;

        }
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        if (mListener != null && getChildCount() == 1) {
            mListener.onFirstPageAttached();
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        if (mListener != null) {
            mListener.onPageDetached(mScrollBy >= 0,getPosition(view));
        }
    }

    public void setListener(OnPageChangedListener listener) {
        mListener = listener;
    }

    public interface OnPageChangedListener{

        /**
         * 初始化
         */
        void onFirstPageAttached();

        /**
         * 页面离屏
         * @param isNext
         * @param position
         */
        void onPageDetached(boolean isNext, int position);

        /**
         * 页面选中
         * @param position
         * @param isBottom
         */
        void onPageSelected(int position, boolean isBottom);

    }
}
