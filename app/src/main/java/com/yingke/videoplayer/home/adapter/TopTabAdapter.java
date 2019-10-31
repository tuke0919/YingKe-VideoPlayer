package com.yingke.videoplayer.home.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;

import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.home.bean.TopTabData;
import com.yingke.videoplayer.home.frag.CategoryContentFragment;
import com.yingke.videoplayer.home.frag.RecommendFragment;
import com.yingke.videoplayer.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/19
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TopTabAdapter extends FragmentPagerAdapter {

    private List<TopTabData> mTopTabData;
    private Fragment mCurrentFragment;

    public TopTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        TopTabData tabData = mTopTabData.get(i);
        PlayerLog.d("Fragment", "getItem i = " + i +"_name_" +  tabData.getName() + "_type_"+ tabData.getType());
        Fragment fragment = null;
        if (tabData != null) {
            switch (tabData.getType()) {
                case TopTabData.TabType.TAB_SUBSCRIBE:
                    fragment = CategoryContentFragment.newInstance(tabData.getName());
                    break;
                case TopTabData.TabType.TAB_RECOMMEND:
                    fragment = RecommendFragment.newInstance();
                    break;
                case TopTabData.TabType.TAB_SHORT_VIDEO:
                    fragment = CategoryContentFragment.newInstance(tabData.getName());
                    break;
                case TopTabData.TabType.TAB_COURSE_MENU:
                    fragment = CategoryContentFragment.newInstance(tabData.getName());
                    break;
                default:
                    fragment = CategoryContentFragment.newInstance(tabData.getName());
                    break;

            }
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return CollectionUtil.isEmpty(mTopTabData) ? 0 : mTopTabData.size();
    }

    @Override
    public long getItemId(int position) {
        return mTopTabData.get(position).getId();
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mCurrentFragment = (Fragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        TopTabData tabData = mTopTabData.get(position);
        return getColorSpanTitle(tabData);
    }

    public List<TopTabData> getTopTabData() {
        return mTopTabData;
    }

    public void setTopTabData(List<TopTabData> topTabData) {
        mTopTabData = topTabData;
        if (mTopTabData == null) {
            mTopTabData = new ArrayList<>();
        }
        mTopTabData.add(0, TopTabData.newTabData(100, TopTabData.TabType.TAB_SUBSCRIBE, "关注", 100,""));
        mTopTabData.add(1, TopTabData.newTabData(101, TopTabData.TabType.TAB_RECOMMEND, "推荐", 99, ""));
    }

    private CharSequence getColorSpanTitle(TopTabData tabData){
        SpannableString titleSpan = new SpannableString(tabData.getName());
        int color = 0;
        if (!TextUtils.isEmpty(tabData.getFontColor()) && tabData.getFontColor().contains("#")) {
            try {
                color = Color.parseColor(tabData.getFontColor());
            } catch (NumberFormatException e) {
                color = 0;
            }
        }
        if (color != 0) {
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            titleSpan.setSpan(span, 0, tabData.getName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return titleSpan;
    }
}
