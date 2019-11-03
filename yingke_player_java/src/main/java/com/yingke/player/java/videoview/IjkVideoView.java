package com.yingke.player.java.videoview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yingke.player.java.danmaku.BiliDanmukuParser;
import com.yingke.player.java.danmaku.DanmakuHelper;
import com.yingke.player.java.util.PlayerUtils;
import com.yingke.player.java.R;
import com.yingke.player.java.controller.BaseMediaController;
import com.yingke.player.java.widget.ResizedSurfaceView;
import com.yingke.player.java.widget.ResizedTextureView;

import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;

import static com.yingke.player.java.ScreenScale.SCREEN_SCALE_DEFAULT;

/**
 * 功能：播放器视图 添加Surface View ，弹幕相关 和 小屏播放逻辑
 *
 * 与业务无关
 *
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/13
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class IjkVideoView extends IjkBaseVideoView {

    protected ResizedSurfaceView mSurfaceView;
    protected ResizedTextureView mTextureView;
    protected SurfaceTexture mSurfaceTexture;
    // 容器
    protected FrameLayout mPlayerContainer;
    // 视屏大小
    protected int[] mVideoSize = {0, 0};

    // 当前屏幕模式
    protected int mCurrentScreenScale = SCREEN_SCALE_DEFAULT;
    // 小屏状态
    protected boolean mIsTinyScreen;
    // 小屏尺寸
    protected int[] mTinyScreenSize = {0, 0};

    // 弹幕实现相关
    protected boolean mIsDanmakuEnable = false;
    protected DanmakuHelper mDanmakuHelper;


    public IjkVideoView(Context context) {
        this(context, null);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPlayerContainer = new FrameLayout(getContext());
        mPlayerContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);

    }

    /**
     * 添加 显示视图
     */
    @Override
    public void addDisplay() {
        if (mUsingSurfaceView || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            addSurfaceView();
        } else {
            addTextureView();
        }
        if (mIsDanmakuEnable) {
            if (mDanmakuHelper != null) {
                mDanmakuHelper.releaseDanmaku();
                mDanmakuHelper = null;
            }

            mDanmakuHelper = new DanmakuHelper();
            mDanmakuHelper.initDanMuView(getContext());

            // 添加弹幕库
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPlayerContainer.addView(mDanmakuHelper.getDanmakuView(), params);
        }
    }

    /**
     * 添加SurfaceView
     */
    private void addSurfaceView() {
        mPlayerContainer.removeAllViews();
        mSurfaceView = new ResizedSurfaceView(getContext());
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        // 加入布局
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mPlayerContainer.addView(mSurfaceView, 0, params);
    }

    /**
     * 添加TextureView
     */
    private void addTextureView() {
        mPlayerContainer.removeAllViews();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mTextureView = new ResizedTextureView(getContext());
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                if (mSurfaceTexture != null) {
                    mTextureView.setSurfaceTexture(mSurfaceTexture);
                } else {
                    mSurfaceTexture = surfaceTexture;
                    mMediaPlayer.setSurface(new Surface(surfaceTexture));
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return mSurfaceTexture == null;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        // 加入布局
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mPlayerContainer.addView(mTextureView,  0, params);
    }

    @Override
    public void setMediaController(BaseMediaController baseMediaController) {
        super.setMediaController(baseMediaController);
    }

    @Override
    protected void preparePlayer(boolean needReset) {
        super.preparePlayer(needReset);

        // 开始准备弹幕
        if (mIsDanmakuEnable) {
            if (mDanmakuHelper != null) {
                mDanmakuHelper.prepareDanmaku();
            }
        }
    }

    @Override
    public void pause() {
        super.pause();
        if (mIsDanmakuEnable && isInPlaybackState()) {
            if (mDanmakuHelper != null) {
                mDanmakuHelper.pauseDanmaku();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        if (mIsDanmakuEnable && isInPlaybackState()) {
            if (mDanmakuHelper != null) {
                mDanmakuHelper.resumeDanmaku();
            }
        }
    }

    @Override
    public void seekTo(long pos) {
        super.seekTo(pos);
        if (mIsDanmakuEnable && isInPlaybackState()) {
            if (mDanmakuHelper != null) {
                mDanmakuHelper.seekToDanmaku(pos);
            }
        }
    }

    @Override
    public void release() {
        super.release();
        mPlayerContainer.removeView(mTextureView);
        mPlayerContainer.removeView(mSurfaceView);
        // 释放弹幕
        if (mDanmakuHelper != null) {
            mDanmakuHelper.releaseDanmaku();
            mDanmakuHelper = null;
        }
        mPlayerContainer.removeAllViews();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentScreenScale = SCREEN_SCALE_DEFAULT;
    }

    /**
     * @return 屏幕缩放
     */
    public int getCurrentScreenScale() {
        return mCurrentScreenScale;
    }

    /**
     * 旋转视频画面
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        if (mTextureView != null) {
            mTextureView.setRotation(rotation);
            mTextureView.requestLayout();
        }

        if (mSurfaceView != null) {
            mSurfaceView.setRotation(rotation);
            mSurfaceView.requestLayout();
        }
    }

    /**
     * 设置 屏幕缩放类型
     * @param screenScale
     */
    @Override
    public void setScreenScale(int screenScale) {
        this.mCurrentScreenScale = screenScale;
        if (mSurfaceView != null) {
            mSurfaceView.setScreenScale(screenScale);
        } else if (mTextureView != null) {
            mTextureView.setScreenScale(screenScale);
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     * @param enable
     */
    @Override
    public void setMirrorRotation(boolean enable) {
        if (mTextureView != null) {
            mTextureView.setScaleX(enable ? -1 : 1);
            return;
        }

        if (mSurfaceView != null) {
            mSurfaceView.setScaleX(enable ? -1 : 1);
            return;
        }
    }

    /**
     * 截图，暂不支持SurfaceView
     * @return
     */
    @Override
    public Bitmap doScreenShot() {
        if (mTextureView != null) {
            return mTextureView.getBitmap();
        }
        return null;
    }

    @Override
    public int[] getVideoSize() {
        return mVideoSize;
    }

    /**
     * 视频播放器内部 原视频宽高 回调
     * @param width
     * @param height
     */
    @Override
    public void onVideoSizeChanged(int width, int height) {
        mVideoSize[0] = width;
        mVideoSize[1] = height;
        if (mUsingSurfaceView || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            mSurfaceView.setScreenScale(mCurrentScreenScale);
            mSurfaceView.setSurfaceSize(width, height);
        } else {
            mTextureView.setScreenScale(mCurrentScreenScale);
            mTextureView.setTextureSize(width, height);
        }
    }

    /**
     * 设置是否启用弹幕 非显示和隐藏
     * @param danmakuEnable
     */
    public void setDanmakuEnable(boolean danmakuEnable) {
        mIsDanmakuEnable = danmakuEnable;
    }


    /**
     * 显示弹幕 已经启动才是 外部调用
     */
    public void showDamaku() {
        if (mIsDanmakuEnable && mDanmakuHelper != null) {
            mDanmakuHelper.showDanmaku();
        }
    }

    /**
     * 隐藏弹幕 已经启动才是 外部调用
     */
    public void hideDamaku() {
        if (mIsDanmakuEnable && mDanmakuHelper != null) {
            mDanmakuHelper.hideDanmaku();
        }
    }

    @Deprecated
    @Override
    public void startTinyScreen() {
        // 这里和业务没关系，应该add和业务相关的播放器，很多业务情况不是很满足
        if (mIsTinyScreen){
            return;
        }
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null){
            return;
        }
        mOrientationEventListener.disable();
        // 移除
        this.removeView(mPlayerContainer);

        // 实际是添加到contentView上
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        int width = mTinyScreenSize[0];
        if (width <= 0) {
            width = PlayerUtils.getScreenWidth(getContext()) / 2;
        }

        int height = mTinyScreenSize[1];
        if (height <= 0) {
            height = width * 9 / 16;
        }

        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        // 添加到ContentView
        contentView.addView(mPlayerContainer, params);
        mIsTinyScreen = true;
        notifyScreenModeChanged(SCREEN_MODE_TINY);
    }

    @Deprecated
    @Override
    public void stopTinyScreen() {
        if (!mIsTinyScreen) {
            return;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) {
            return;
        }

        ViewGroup contentView = activity.findViewById(android.R.id.content);
        // 移除
        contentView.removeView(mPlayerContainer);

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // 添加到当前 布局
        this.addView(mPlayerContainer, params);
        if (mAutoRotate) {
            mOrientationEventListener.enable();
        }
        mIsTinyScreen = false;
        notifyScreenModeChanged(SCREEN_MODE_NORMAL);
    }

    @Override
    public boolean isTinyScreen() {
        return mIsTinyScreen;
    }

    /**
     * 设置小屏
     * @param tinyScreen
     */
    public void setTinyScreen(boolean tinyScreen) {
        mIsTinyScreen = tinyScreen;
        notifyScreenModeChanged(tinyScreen ? SCREEN_MODE_TINY: SCREEN_MODE_NORMAL);
    }
}
