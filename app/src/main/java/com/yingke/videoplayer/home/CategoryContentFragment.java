package com.yingke.videoplayer.home;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/21
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class CategoryContentFragment extends BaseFragment {

    public static final String TAG = "CategoryContentFragment";

    public static CategoryContentFragment newInstance(String keyWords){
        CategoryContentFragment fragment = new CategoryContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("keyWords", keyWords );
        fragment.setArguments(bundle);
        return fragment;
    }

    private String keyWords;
    private TextView mKeyWords;
    @Override
    protected int getLayoutResId() {
        return R.layout.frag_subscribe;
    }

    @Override
    protected void initView(View rootView) {
        mKeyWords = rootView.findViewById(R.id.subscribe);
    }

    @Override
    protected void initData() {
        keyWords = getArguments().getString("keyWords");
        mKeyWords.setText(keyWords);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PlayerLog.d(TAG, "keyWords = " + keyWords);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onPause() {
        super.onPause();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        PlayerLog.d(TAG, "keyWords = " + keyWords);
    }
}
