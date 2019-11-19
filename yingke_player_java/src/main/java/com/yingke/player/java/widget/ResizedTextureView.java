package com.yingke.player.java.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.yingke.player.java.ScreenScale;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/13
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ResizedTextureView extends TextureView {

    private int mTextureWidth;
    private int mTextureHeight;
    private int mScreenScale;

    public ResizedTextureView(Context context) {
        super(context);
    }

    public ResizedTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizedTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置 surface宽高
     * @param width
     * @param height
     */
    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
    }

    /**
     * 设置屏幕缩放
     * @param screenScale
     */
    public void setScreenScale(int screenScale) {
        mScreenScale = screenScale;
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getRotation() == 90 || getRotation() == 270) {
            // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        int width = getDefaultSize(mTextureWidth, widthMeasureSpec);
        int height = getDefaultSize(mTextureHeight, heightMeasureSpec);

        // 如果设置了比例
        switch (mScreenScale) {
            case ScreenScale.SCREEN_SCALE_ORIGINAL:
                width = mTextureWidth;
                height = mTextureHeight;
                break;
            case ScreenScale.SCREEN_SCALE_16_9:
                if (height > width / 16 * 9) {
                    height = width / 16 * 9;
                } else {
                    width = height / 9 * 16;
                }
                break;
            case ScreenScale.SCREEN_SCALE_4_3:
                if (height > width / 4 * 3) {
                    height = width / 4 * 3;
                } else {
                    width = height / 3 * 4;
                }
                break;
            case ScreenScale.SCREEN_SCALE_MATCH_PARENT:
                width = widthMeasureSpec;
                height = heightMeasureSpec;
                break;
            case ScreenScale.SCREEN_SCALE_CENTER_CROP:
                if (mTextureWidth > 0 && mTextureHeight > 0) {
                    if (mTextureWidth * height > width * mTextureHeight) {
                        width = height * mTextureWidth / mTextureHeight;
                    } else {
                        height = width * mTextureHeight / mTextureWidth;
                    }
                }
                break;
            default:
                if (mTextureWidth > 0 && mTextureHeight > 0) {

                    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
                    int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
                    int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
                    int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

                    if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                        // the size is fixed
                        width = widthSpecSize;
                        height = heightSpecSize;

                        // for compatibility, we adjust size based on aspect ratio
                        if (mTextureWidth * height < width * mTextureHeight) {
                            //Log.i("@@@", "image too wide, correcting");
                            width = height * mTextureWidth / mTextureHeight;
                        } else if (mTextureWidth * height > width * mTextureHeight) {
                            //Log.i("@@@", "image too tall, correcting");
                            height = width * mTextureHeight / mTextureWidth;
                        }
                    } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                        // only the width is fixed, adjust the height to match aspect ratio if possible
                        width = widthSpecSize;
                        height = width * mTextureHeight / mTextureWidth;
                        if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                            // couldn't match aspect ratio within the constraints
                            height = heightSpecSize;
                        }
                    } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                        // only the height is fixed, adjust the width to match aspect ratio if possible
                        height = heightSpecSize;
                        width = height * mTextureWidth / mTextureHeight;
                        if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                            // couldn't match aspect ratio within the constraints
                            width = widthSpecSize;
                        }
                    } else {
                        // neither the width nor the height are fixed, try to use actual video size
                        width = mTextureWidth;
                        height = mTextureHeight;
                        if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                            // too tall, decrease both width and height
                            height = heightSpecSize;
                            width = height * mTextureWidth / mTextureHeight;
                        }
                        if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                            // too wide, decrease both width and height
                            width = widthSpecSize;
                            height = width * mTextureHeight / mTextureWidth;
                        }
                    }
                } else {
                    // no size yet, just adopt the given spec sizes
                }
                break;
        }
        setMeasuredDimension(width, height);
    }

}
