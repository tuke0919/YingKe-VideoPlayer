package com.yingke.videoplayer.tiktok;

import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;
import com.yingke.videoplayer.util.FrescoUtil;

import java.io.File;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/20
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TiktokFragment extends BaseFragment {

    public static TiktokFragment newInstance(){
        return new TiktokFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.frag_tiktok;
    }

    @Override
    protected void initView(View rootView) {
        // https://github.com/tuke0919/xiaoshujiang_image/blob/master/video/tiktok_1.mp4?raw=ture
        SimpleDraweeView mDraweeView = rootView.findViewById(R.id.tiktok_image);
        File file = new File("/data/data/com.yingke.videoplayer/cache/list_video_thumb/12be376071fdf0eeec4ab4a6864f0477.jpeg");
        FrescoUtil.displayImage(mDraweeView,file);
    }

    @Override
    protected void initData() {

    }
}
