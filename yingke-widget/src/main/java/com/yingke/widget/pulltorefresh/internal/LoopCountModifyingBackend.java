package com.yingke.widget.pulltorefresh.internal;

import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.backend.AnimationBackendDelegate;

import androidx.annotation.Nullable;

/**
 * Created by libowen on 2018/11/20.
 */
public class LoopCountModifyingBackend extends AnimationBackendDelegate {

    private int mLoopCount;

    public LoopCountModifyingBackend(
            @Nullable AnimationBackend animationBackend,
            int loopCount) {
        super(animationBackend);
        mLoopCount = loopCount;
    }

    @Override
    public int getLoopCount() {
        return mLoopCount;
    }
}
