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
package com.yingke.widget.pulltorefresh.internal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;

import com.yingke.widget.R;
import com.yingke.widget.util.DeviceUtil;
import com.yingke.widget.pulltorefresh.PullToRefreshBase;
import com.yingke.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.yingke.widget.pulltorefresh.PullToRefreshBase.Orientation;

/**
 * 自定义的下拉刷新loading布局
 *
 * @author tuke
 */
public class CustomLoadingLayout extends LoadingLayout {
    private String LOG_TAG = "CustomLoadingLayout";

    private final Matrix mHeaderImageMatrix;
    private Handler mTimeHandler = new Handler(Looper.getMainLooper());

    private float mRotationPivotX, mRotationPivotY;

    public CustomLoadingLayout(Context context, Mode mode,
                               Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

        mHeaderImage.setScaleType(ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        setBackgroundColor(0);
    }

    public void onLoadingDrawableSet(Drawable imageDrawable) {
        if (null != imageDrawable) {
            mRotationPivotX = Math
                    .round(imageDrawable.getIntrinsicWidth() / 2f);
            mRotationPivotY = Math
                    .round(imageDrawable.getIntrinsicHeight() / 2f);
        }
    }

    protected void onPullImpl(float scaleOfLayout) {
        if (PullToRefreshBase.DEBUG) {
            Log.d(LOG_TAG, "pull impl");
        }
        float scale = scaleOfLayout > 1.0 ? (float) 1.0 : scaleOfLayout;
//		mHeaderImageMatrix.setScale(scale, scale, mRotationPivotX,
//				mRotationPivotY);
//		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        mHeaderProgress.setScaleX(scale);
        mHeaderProgress.setScaleY(scale);
    }

    @Override
    protected void refreshingImpl() {
        if (PullToRefreshBase.DEBUG) {
            Log.d(LOG_TAG, "refresh impl");
        }
        mHeaderProgress.setVisibility(View.VISIBLE);
        mHeaderImage.setVisibility(View.GONE);
        // mHeaderImage.startAnimation(mRotateAnimation);
    }

    @Override
    protected void resetImpl() {
        if (PullToRefreshBase.DEBUG) {
            Log.d(LOG_TAG, "reset impl");
        }
        mHeaderImage.setVisibility(View.VISIBLE);
        mHeaderProgress.setVisibility(View.VISIBLE);
        // mHeaderImage.clearAnimation();
        if (null != mHeaderImageMatrix) {
//            mHeaderImageMatrix.reset();
//            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        }
    }

    @Override
    protected void pullToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected void releaseToRefreshImpl() {
        // NO-OP
    }

    @Override
    protected int getDefaultDrawableResId() {
        return R.drawable.ptr_loading_circle;
    }


    ValueAnimator mTipAnimator;

    @Override
    protected void showTipViewImpl(CharSequence message) {
        super.showTipViewImpl(message);
        if (mTipTextView != null) {
            mTipAnimator = ValueAnimator.ofInt(0, DeviceUtil.getScreenWidth(getContext()));
            mTipAnimator.setDuration(300);
            mTipAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int width = (int) animation.getAnimatedValue();
                    mTipTextView.getLayoutParams().width = width;
                    mTipTextView.requestLayout();
                }
            });
            mTipAnimator.start();
        }

    }

    @Override
    public void dissmissTipViewImpl() {
        super.dissmissTipViewImpl();
        if (mTipTextView != null) {
            if (mTipAnimator != null) {
                mTipAnimator.cancel();
                mTipAnimator = null;
            }
        }
    }

    /**
     * 设置Inner Layout的margin bottom
     *
     * @param marginBottom
     */
    public final void setInnerLayoutMarginBottom(int marginBottom) {
        if (PullToRefreshBase.DEBUG) {
            Log.d(LOG_TAG, "set inner layout margin bottom = " + marginBottom);
        }
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mInnerLayout
                .getLayoutParams();
        FrameLayout.LayoutParams newP = new FrameLayout.LayoutParams(p.width,
                p.height, p.gravity);
        newP.bottomMargin = marginBottom;
        mInnerLayout.setLayoutParams(newP);
    }

    public final int getInnerLayoutHeight() {
        return mInnerLayout.getHeight();
    }
}
