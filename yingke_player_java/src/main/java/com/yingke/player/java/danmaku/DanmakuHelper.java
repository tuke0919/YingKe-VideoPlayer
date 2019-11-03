package com.yingke.player.java.danmaku;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;

import com.yingke.player.java.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.util.IOUtils;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-11-03
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class DanmakuHelper {

    private Context mContext;
    private DanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mDanmakuParser;


    /**
     * 初始化弹幕
     * @param context
     */
    public void initDanMuView(Context context) {

        // 新建实例
        mDanmakuView = new DanmakuView(context);
        mContext = context;

        initDammakuContext();
        initDamakuParser(0);

        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                // 准备完成 开始启动弹幕
                mDanmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        mDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {
            @Override
            public boolean onDanmakuClick(IDanmakus danmakus) {
                return false;
            }

            @Override
            public boolean onDanmakuLongClick(IDanmakus danmakus) {
                return false;
            }

            @Override
            public boolean onViewClick(IDanmakuView view) {
                return false;
            }
        });
        mDanmakuView.showFPS(true);
        mDanmakuView.enableDanmakuDrawingCache(true);

    }

    /**
     * 初始化弹幕上下文
     */
    public void initDammakuContext() {

        // 设置最大行数
        HashMap<Integer,Integer> maxLinesPair = new HashMap<>();
        // 滚动弹幕最大显示5行
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL,5);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        // 初始化上下文
        mDanmakuContext = DanmakuContext.create();
        // 设置弹幕类型
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN,3);
        // 设置是否合并重复弹幕
        mDanmakuContext.setDuplicateMergingEnabled(false);
        // 设置弹幕滚动速度
        mDanmakuContext.setScrollSpeedFactor(1.2f);
        // 设置弹幕字体大小
        mDanmakuContext.setScaleTextSize(1.2f);
        // 设置弹幕间隔
        mDanmakuContext.setDanmakuMargin(40);
        // 设置缓存绘制填充器 图文混排使用SpannedCacheStuffer
        mDanmakuContext.setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter);
        // 设置最大行数
        mDanmakuContext.setMaximumLines(maxLinesPair);
        // 设置是否禁止重叠
        mDanmakuContext.preventOverlapping(overlappingEnablePair);

    }

    /**
     * 初始化弹幕解析器
     * @param resId 弹幕文件资源id 可以是http，file
     */
    public void initDamakuParser(int resId) {
        int danmuFile = R.raw.your_name;
        if (resId != 0) {
            danmuFile = resId;
        }
        // 加载弹幕资源文件
        mDanmakuParser = createParser(mDanmakuView.getContext().getResources().openRawResource(R.raw.your_name));

    }

    /**
     * 从弹幕文件中提起弹幕
     * @param stream
     * @return
     */
    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        // 创建一个BiliDanmakuLoader实例来加载弹幕流文件
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        // Bili 弹幕解析器
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) {
                // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if(drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if(mDanmakuView != null) {
                                mDanmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };


    /**
     * 添加文字弹幕
     */
    private void addDanmaku() {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }

        danmaku.text = "这是一条弹幕" + System.nanoTime();
        danmaku.padding = 5;
        // 可能会被各种过滤器过滤并隐藏显示
        danmaku.priority = 0;
        danmaku.isLive = false;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);

    }

    /**
     * 添加图文混排弹幕
     */
    private void addDanmaKuShowTextAndImage() {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        // 一定会显示, 一般用于本机发送的弹幕
        danmaku.priority = 1;
        danmaku.isLive = false;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 1200);
        danmaku.textSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.textShadowColor = 0;
        danmaku.underlineColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }




    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        CenteredImageSpan span = new CenteredImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 准备弹幕
     */
    public void prepareDanmaku() {
        if (mDanmakuParser == null) {
            throw new IllegalArgumentException("mDanmakuParser is null");
        }

        if (mDanmakuContext == null) {
            throw new IllegalArgumentException("mDanmakuContext is null");
        }

        if (mDanmakuView != null) {
            mDanmakuView.prepare(mDanmakuParser, mDanmakuContext);
        }
    }

    /**
     * 显示弹幕
     */
    public void showDanmaku() {
        if (mDanmakuView != null) {
            mDanmakuView.show();
        }
    }

    /**
     * 隐藏弹幕
     */
    public void hideDanmaku() {
        if (mDanmakuView != null) {
            mDanmakuView.hide();
        }
    }

    /**
     * 暂停弹幕
     */
    public void pauseDanmaku() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    /**
     * 恢复弹幕
     */
    public void resumeDanmaku() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    /**
     * 跳过
     * @param pos
     */
    public void seekToDanmaku(long pos) {
        if (mDanmakuView != null) {
            mDanmakuView.seekTo(pos);
        }
    }

    /**
     * 释放弹幕
     */
    public void releaseDanmaku() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public DanmakuView getDanmakuView() {
        return mDanmakuView;
    }

    public DanmakuContext getDanmakuContext() {
        return mDanmakuContext;
    }

    public BaseDanmakuParser getDanmakuParser() {
        return mDanmakuParser;
    }
}
