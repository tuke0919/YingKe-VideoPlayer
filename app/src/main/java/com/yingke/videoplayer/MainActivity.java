package com.yingke.videoplayer;

import android.os.Bundle;

import com.yingke.videoplayer.base.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_home);
    }
}
