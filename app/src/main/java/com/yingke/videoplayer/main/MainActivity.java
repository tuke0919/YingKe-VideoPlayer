package com.yingke.videoplayer.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseActivity;
import com.yingke.videoplayer.center.CommunityFragment;
import com.yingke.videoplayer.home.HomeFragment;
import com.yingke.videoplayer.personal.PersonalFragment;
import com.yingke.videoplayer.tiktok.TiktokFragment;

import java.util.HashMap;

public class MainActivity extends BaseActivity {


    public static final String TAB_HOME = "tab_home";
    public static final String TAB_TIKTOK = "tab_tiktok";
    public static final String TAB_COMMUNITY = "tab_community";
    public static final String TAB_ME = "tab_me";

    private BottomTabData[] mBottomTabDatas = {
            new BottomTabData(TAB_HOME, HomeFragment.class, R.mipmap.icon_home_tab,0,R.string.tab_home),
            new BottomTabData(TAB_TIKTOK, TiktokFragment.class, R.mipmap.icon_video_tab,0,R.string.tab_tiktok),
            new BottomTabData(TAB_COMMUNITY, CommunityFragment.class, R.mipmap.icon_community_tab,0,R.string.tab_community),
            new BottomTabData(TAB_ME, PersonalFragment.class, R.mipmap.icon_me_tab,0,R.string.tab_me),

    };

    private HashMap<String, BottomTabView> mBottomTabViewHashMap = new HashMap<>();

    private FragmentTabHost mTabHost;
    private TabWidget mTabWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    private void initViews(){
        initTabHost();
    }

    private void initTabHost(){
        if (isFinishing() || isDestroyed()) {
            return;
        }
        mTabHost = findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.frag_container);
        mTabWidget = mTabHost.getTabWidget();
        mTabWidget.setDividerDrawable(null);
    }
    private void initData(){
        addTabHostTab();
        mTabHost.setCurrentTab(0);
    }

    private void addTabHostTab(){
        mTabHost.clearAllTabs();
        mBottomTabViewHashMap.clear();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int  index = 0; index < mBottomTabDatas.length ; index ++) {
            BottomTabData tabData = mBottomTabDatas[index];

            String tag = tabData.getTag();
            Class fragClass = tabData.getFragClass();
            int tabImageId = tabData.getInImageId();
            int tabTextId = tabData.getTextId();

            ViewGroup rootView  = (ViewGroup) inflater.inflate(R.layout.main_bottom_tab_item, mTabHost.getTabWidget(), false);
            BottomTabView bottomTabView = new BottomTabView(rootView, index);
            bottomTabView.initTabView(tabImageId, tabTextId);

            mTabHost.addTab(
                    mTabHost.newTabSpec(tag).setIndicator(bottomTabView.getRootView()),
                    fragClass,
                    null);

            mBottomTabViewHashMap.put(tag, bottomTabView);
        }
    }

    private static class BottomTabView{
        private ViewGroup mRootView;

        private View mTouchView;
        private ImageView mImageIcon;
        private TextView mTabText;

        private int mTabIndex;

        public BottomTabView(ViewGroup rootView, int tabIndex) {
            mRootView = rootView;
            mTabIndex = tabIndex;
            mTouchView = rootView.findViewById(R.id.tab_content);
            mImageIcon = rootView.findViewById(R.id.bottom_icon);
            mTabText = rootView.findViewById(R.id.bottom_title);
        }

        public void initTabView(@DrawableRes int drawableId, @StringRes int tabText){
            mImageIcon.setImageResource(drawableId);
            mTabText.setText(tabText);
        }

        public int getTabIndex() {
            return mTabIndex;
        }

        public View getTouchView() {
            return mTouchView;
        }

        public ViewGroup getRootView() {
            return mRootView;
        }
    }
}
