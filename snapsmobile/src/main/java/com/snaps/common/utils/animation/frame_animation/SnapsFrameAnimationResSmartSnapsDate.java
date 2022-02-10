package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResSmartSnapsDate extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.smart_time_01, 500);
        resourceMap.put(R.drawable.smart_time_02, 70);
        resourceMap.put(R.drawable.smart_time_03, 70);
        resourceMap.put(R.drawable.smart_time_04, 70);
        resourceMap.put(R.drawable.smart_time_05, 70);
        resourceMap.put(R.drawable.smart_time_06, 70);
        resourceMap.put(R.drawable.smart_time_07, 70);
    }

    @Override
    protected boolean isRepeat() {
        return false;
    }
}
