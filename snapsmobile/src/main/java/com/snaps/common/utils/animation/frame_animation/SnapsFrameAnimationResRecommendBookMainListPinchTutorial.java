package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResRecommendBookMainListPinchTutorial extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.tutorial_pinchzoom_01, 500);
        resourceMap.put(R.drawable.tutorial_pinchzoom_02, 250);
        resourceMap.put(R.drawable.tutorial_pinchzoom_03, 250);
        resourceMap.put(R.drawable.tutorial_pinchzoom_04, 250);
        resourceMap.put(R.drawable.tutorial_pinchzoom_05, 250);
        resourceMap.put(R.drawable.tutorial_pinchzoom_06, 250);
        resourceMap.put(R.drawable.tutorial_pinchzoom_07, 500);
        resourceMap.put(R.drawable.tutorial_pinchzoom_08, 250);
        resourceMap.put(R.drawable.tutorial_pinchzoom_09, 500);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
