package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResImageSelectTutorialNewPinch extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.tutorial_pinch_01, 500);
        resourceMap.put(R.drawable.tutorial_pinch_02, 250);
        resourceMap.put(R.drawable.tutorial_pinch_03, 250);
        resourceMap.put(R.drawable.tutorial_pinch_04, 250);
        resourceMap.put(R.drawable.tutorial_pinch_05, 250);
        resourceMap.put(R.drawable.tutorial_pinch_04, 250);
        resourceMap.put(R.drawable.tutorial_pinch_03, 250);
        resourceMap.put(R.drawable.tutorial_pinch_04, 250);
        resourceMap.put(R.drawable.tutorial_pinch_05, 250);
        resourceMap.put(R.drawable.tutorial_pinch_04, 250);
        resourceMap.put(R.drawable.tutorial_pinch_03, 250);
        resourceMap.put(R.drawable.tutorial_pinch_04, 250);
        resourceMap.put(R.drawable.tutorial_pinch_05, 250);
        resourceMap.put(R.drawable.tutorial_pinch_06, 500);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
