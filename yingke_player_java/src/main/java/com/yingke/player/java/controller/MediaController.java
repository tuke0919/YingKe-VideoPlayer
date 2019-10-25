package com.yingke.player.java.controller;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 功能：控制器基类  与业务无关
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/21
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class MediaController extends BaseMediaController {

    public MediaController(Context context) {
        super(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
