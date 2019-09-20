package com.yingke.videoplayer.home;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.yingke.videoplayer.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/19
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TopTabAdapter extends FragmentPagerAdapter {

    private List<TopTabData> mTopTabData;


    public TopTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        TopTabData tabData = mTopTabData.get(i);
        if (tabData != null) {
            switch (tabData.getType()) {
                case TopTabData.TabType.TAB_SUBSCRIBE:

                    break;
                case TopTabData.TabType.TAB_RECOMMEND:

                    break;
                case TopTabData.TabType.TAB_SHORT_VIDEO:

                    break;
                case TopTabData.TabType.TAB_COURSE_MENU:

                    break;
                default:

                    break;

            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return CollectionUtil.isEmpty(mTopTabData) ? 0 : mTopTabData.size();
    }

    @Override
    public long getItemId(int position) {
        return mTopTabData.get(position).getId();
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
        mTopTabData.add(0, TopTabData.newTabData(TopTabData.TabType.TAB_SUBSCRIBE, "关注"));
        mTopTabData.add(1, TopTabData.newTabData(TopTabData.TabType.TAB_RECOMMEND, "推荐"));
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
