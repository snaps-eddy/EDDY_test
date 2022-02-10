package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResNativeDetailDefaultPage extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.img_loading_01, 250);
        resourceMap.put(R.drawable.img_loading_02, 250);
        resourceMap.put(R.drawable.img_loading_03, 250);
        resourceMap.put(R.drawable.img_loading_04, 250);
    }

    @Override
    protected boolean isRepeat() {
        return true;
    }
}
