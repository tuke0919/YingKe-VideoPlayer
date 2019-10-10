package com.yingke.videoplayer.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.yingke.player.java.PlayerLog;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.YingKePlayerApp;


/**
 * 基类
 */
public class BaseActivity extends AppCompatActivity  {

    private static final String TAG = "BaseActivity";

    // 上下文
    public YingKePlayerApp mApp;
    // 工具栏
    protected Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initStatusBar();
        PlayerLog.d(getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);

        mApp = (YingKePlayerApp) this.getApplicationContext();
    }

    @Override
    public void setContentView(int layoutResID) {
        PlayerLog.d(getClass().getSimpleName(), "setContentView");
        super.setContentView(layoutResID);
        if (hasToolbar()) {
            initActionbar();
        }
    }

    /**
     * 初始化状态栏
     */
    public void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置亮色模式
            StatusBarUtil.StatusBarLightMode(this, isLightStatusBar());
            // 设置透明 状态栏，布局会顶到状态栏下方
            if (isTransStatusBar())
                StatusBarUtil.setTransStatusBar(this, isLightStatusBar());
        }
    }

    /**
     * 初始化toolbar
     */
    protected void initActionbar() {
//        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
//        if (mToolbar == null) {
//            throw new IllegalStateException("Toolbar_actionbar mToolbar has not be found in layout,be sure you have define mToolbar in the layout");
//        } else {
//            setSupportActionBar(mToolbar);
//            getSupportActionBar().setDisplayShowHomeEnabled(false);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            mToolbar.setBackgroundResource(getActionBarBg());
//            mToolbar.setNavigationIcon(R.mipmap.left_back);
//            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBack(v);
//                }
//            });
//            mToolbar.setTitleTextAppearance(this, R.style.toolbar_title_style);
//        }
    }

    /**
     * 是否有actionbar Toolbar
     *
     * @return
     */
    public boolean hasToolbar() {
        return false;
    }

    /**
     * 是否是浅色模式状态栏（深色文字）
     */
    public boolean isLightStatusBar() {
        return true;
    }

    /**
     * 是否是透明状态栏
     */
    protected boolean isTransStatusBar() {
        return true;
    }


    protected int getActionBarBg() {
        return R.drawable.toolbar_bg;
    }

    /**
     * 返回
     * @param view
     */
    public void onBack(View view){
        onBackPressed();
    }

    @Override
    protected void onRestart() {
        PlayerLog.d(getClass().getSimpleName(), "onRestart");
        super.onRestart();

    }

    @Override
    protected void onStart() {
        PlayerLog.d(getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        PlayerLog.d(getClass().getSimpleName(), "onResume");
        super.onResume();

    }

    @Override
    protected void onResumeFragments() {
        PlayerLog.d(getClass().getSimpleName(), "onResumeFragments");
        super.onResumeFragments();
    }

    @Override
    protected void onPause() {
        PlayerLog.d(getClass().getSimpleName(), "onPause");
        super.onPause();

    }

    @Override
    protected void onStop() {
        PlayerLog.d(getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        PlayerLog.d(getClass().getSimpleName(), "onDestroy");
        super.onDestroy();


    }

    @Override
    public boolean isDestroyed() {
        PlayerLog.d(getClass().getSimpleName(), "isDestroyed");
        return super.isDestroyed();

    }

    @Override
    public void finish() {
        PlayerLog.d(getClass().getSimpleName(), "finish");
        super.finish();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        PlayerLog.d(getClass().getSimpleName(), "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        PlayerLog.d(getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        PlayerLog.d(getClass().getSimpleName(), "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

    }


    public void setActionBarTitleText(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    public void setActionBarTitleText(int resource) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(resource);
    }

    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    public void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.show();
    }


    public void setActionBarBackBtnResource(int resource) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(resource);
        }
    }

    public void setActionBarLogo(int resource) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            getSupportActionBar().setLogo(resource);
    }

    /**
     * 是否全屏
     *
     * @param enable
     */
    public void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


}
