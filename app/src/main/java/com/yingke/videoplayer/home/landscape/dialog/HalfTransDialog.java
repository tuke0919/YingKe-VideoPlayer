package com.yingke.videoplayer.home.landscape.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yingke.videoplayer.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-31
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class HalfTransDialog extends Dialog {

    public static final String TAG = "HalfTransDialog";

    public HalfTransDialog(@NonNull Context context) {
        this(context, R.style.DialogRight);
    }

    public HalfTransDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }


    public void init() {
        setCanceledOnTouchOutside(true);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_half_trans, null, false);
        setContentView(rootView);
        adjustParams(rootView);
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    protected void adjustParams(View rootView) {

    }

}
