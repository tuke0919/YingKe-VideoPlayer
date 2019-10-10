package com.yingke.videoplayer.base;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yingke.player.java.PlayerLog;


/**
 *
 */
public abstract class BaseFragment extends Fragment {

    protected View mRootView;

    public BaseFragment() {
    }


    @Override
    public void onAttach(Activity activity) {
        PlayerLog.d(getClass().getSimpleName(), "onAttach to " + activity.getClass().getSimpleName());
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PlayerLog.d(getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        PlayerLog.d(getClass().getSimpleName(), "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PlayerLog.d(getClass().getSimpleName(), "onCreateView");

        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResId(), container, false);
            initView(mRootView);
            initData();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    /**
     * @return
     */
    protected abstract int getLayoutResId();

    /**
     *
     */
    protected abstract void initView(View rootView);

    /**
     *
     */
    protected abstract void initData();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        PlayerLog.d(getClass().getSimpleName(), "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        PlayerLog.d(getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        PlayerLog.d(getClass().getSimpleName(), "onResume");
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        PlayerLog.d(getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        PlayerLog.d(getClass().getSimpleName(), "onPause");
        super.onPause();


    }

    @Override
    public void onStop() {
        PlayerLog.d(getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        PlayerLog.d(getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        PlayerLog.d(getClass().getSimpleName(), "onDestroy");
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        PlayerLog.d(getClass().getSimpleName(), "onDetach from "
                + getActivity().getClass().getSimpleName());
        super.onDetach();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        PlayerLog.d(getClass().getSimpleName(), "setUserVisibleHint = " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        PlayerLog.d(getClass().getSimpleName(), "onHiddenChanged");
        super.onHiddenChanged(hidden);
    }
}
