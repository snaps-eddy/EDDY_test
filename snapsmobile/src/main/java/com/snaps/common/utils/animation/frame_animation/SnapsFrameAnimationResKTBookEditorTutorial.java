package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResKTBookEditorTutorial extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.tutorial_kt_photobook_01, 500);
        resourceMap.put(R.drawable.tutorial_kt_photobook_02, 200);
        resourceMap.put(R.drawable.tutorial_kt_photobook_03, 200);
        resourceMap.put(R.drawable.tutorial_kt_photobook_04, 200);
        resourceMap.put(R.drawable.tutorial_kt_photobook_05, 200);
        resourceMap.put(R.drawable.tutorial_kt_photobook_06, 500);
        resourceMap.put(R.drawable.tutorial_kt_photobook_07, 500);
        resourceMap.put(R.drawable.tutorial_kt_photobook_08, 200);
        resourceMap.put(R.drawable.tutorial_kt_photobook_09, 200);
        resourceMap.put(R.drawable.tutorial_kt_photobook_10, 300);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
