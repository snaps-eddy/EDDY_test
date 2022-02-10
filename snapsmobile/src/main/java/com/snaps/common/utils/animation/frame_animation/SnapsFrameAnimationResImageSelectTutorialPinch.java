package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResImageSelectTutorialPinch extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.img_sel_tutorial_pinch_01, 400);
        resourceMap.put(R.drawable.img_sel_tutorial_pinch_02, 400);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
