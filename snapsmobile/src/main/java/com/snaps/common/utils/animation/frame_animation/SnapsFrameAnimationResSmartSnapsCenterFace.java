package com.snaps.common.utils.animation.frame_animation;

import com.snaps.mobile.R;

import java.util.LinkedHashMap;

public class SnapsFrameAnimationResSmartSnapsCenterFace extends SnapsFrameAnimationBaseRes {
    @Override
    protected void createHashMap() {
        resourceMap = new LinkedHashMap<>();
        resourceMap.put(R.drawable.smart_face_01, 500);
        resourceMap.put(R.drawable.smart_face_02, 70);
        resourceMap.put(R.drawable.smart_face_03, 70);
        resourceMap.put(R.drawable.smart_face_04, 70);
        resourceMap.put(R.drawable.smart_face_05, 70);
    }

    @Override
    protected boolean isRepeat() {
        return false;
    }
}
