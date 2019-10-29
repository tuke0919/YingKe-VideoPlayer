package com.yingke.player.java.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yingke.player.java.R;


/**
 * 显示亮度，音量，进度
 */
public class GestureView extends LinearLayout {

    private ImageView ivIcon;
    private TextView tvPercent;
    private ProgressBar proPercent;

    public GestureView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_lightness_volume, this);
        ivIcon = view.findViewById(R.id.gesture_icon);
        tvPercent = view.findViewById(R.id.gesture_percent);
        proPercent = view.findViewById(R.id.gesture_progress);
    }

    public void setIcon(int icon) {
        if (ivIcon != null) {
            ivIcon.setImageResource(icon);
        }
    }

    public void setTextView(String text) {
        if (tvPercent != null) {
            tvPercent.setText(text);
        }
    }

    public void setProPercent(int percent) {
        if (proPercent != null) {
            proPercent.setProgress(percent);
        }
    }

    public void setProVisibility(int visibility) {
        if (proPercent != null) {
            proPercent.setVisibility(visibility);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_controller_gesture_view);
            this.startAnimation(animation);
        }
    }
}
