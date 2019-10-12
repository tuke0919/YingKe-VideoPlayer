package com.yingke.widget.pulltorefresh.internal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yingke.widget.R;

/**
 * Created by wangdp on 2015/8/24.
 */
public class FooterLayout extends RelativeLayout{

    private LinearLayout retryButton = null;
    private RelativeLayout loadingView = null;
    private SimpleDraweeView mPullDownAnim;
    private FrameLayout endViewContainer;

    public FooterLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.pull_down_foot, this);
        loadingView = (RelativeLayout) findViewById(R.id.loding);
        retryButton = (LinearLayout) findViewById(R.id.clicktoload);
        mPullDownAnim= (SimpleDraweeView) findViewById(R.id.pull_down_anim);
        endViewContainer = (FrameLayout) findViewById(R.id.end_view_container);
    }

    /**
     * 设置endView
     */
    public void setEndView(int layoutRes){
        FrameLayout.inflate(getContext(), layoutRes, endViewContainer);
    }

    public void showClick() {
        retryButton.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        endViewContainer.setVisibility(View.GONE);
    }


    public void showEnd() {
        retryButton.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        endViewContainer.setVisibility(View.VISIBLE);
    }


    public void setLoading() {
        ImageRequest request= ImageRequestBuilder.newBuilderWithResourceId(R.drawable.pull_down_anim).build();
        DraweeController controller= Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        mPullDownAnim.setController(controller);
        loadingView.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        endViewContainer.setVisibility(View.GONE);
    }

    public void setOnRetryListener(OnClickListener l){
        retryButton.setOnClickListener(l);
    }
}
