package com.snaps.common.utils.animation.frame_animation;

import java.util.LinkedHashMap;

public abstract class SnapsFrameAnimationBaseRes {
    protected LinkedHashMap<Integer, Integer> resourceMap;

    public SnapsFrameAnimationBaseRes() {
        createHashMap();
    }

    /**
     * 애니메이션 resource와 during 을 셋팅해 주어야 함.
     */
    protected abstract void createHashMap();

    protected abstract boolean isRepeat();

    public LinkedHashMap<Integer, Integer> getResourceMap() {
        return resourceMap;
    }
}
