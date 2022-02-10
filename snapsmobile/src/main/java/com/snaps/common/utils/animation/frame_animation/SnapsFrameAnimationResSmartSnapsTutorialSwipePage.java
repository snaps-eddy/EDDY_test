package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResSmartSnapsTutorialSwipePage extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.img_tutorial_swipe_left_01, 300);
        resourceMap.put(R.drawable.img_tutorial_swipe_left_02, 300);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
