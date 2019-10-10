package com.yingke.videoplayer.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseActivity;

public class TestLifeCycleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afragment_testctivity);
    }
}
