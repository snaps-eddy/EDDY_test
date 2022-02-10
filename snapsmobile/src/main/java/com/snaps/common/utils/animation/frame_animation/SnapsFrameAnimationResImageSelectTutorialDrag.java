package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResImageSelectTutorialDrag extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.tutorial_select_01, 400);
        resourceMap.put(R.drawable.tutorial_select_02, 200);
        resourceMap.put(R.drawable.tutorial_select_03, 200);
        resourceMap.put(R.drawable.tutorial_select_04, 200);
        resourceMap.put(R.drawable.tutorial_select_05, 200);
        resourceMap.put(R.drawable.tutorial_select_06, 200);
        resourceMap.put(R.drawable.tutorial_select_07, 200);
        resourceMap.put(R.drawable.tutorial_select_08, 400);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
