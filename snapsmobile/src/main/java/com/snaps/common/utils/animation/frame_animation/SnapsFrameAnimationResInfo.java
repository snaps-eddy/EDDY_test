package com.snaps.common.utils.animation.frame_animation;

public class SnapsFrameAnimationResInfo {
    public static SnapsFrameAnimationResInfo createResInfo(int[] resIds, int[] during) {
        SnapsFrameAnimationResInfo resInfo = new SnapsFrameAnimationResInfo();
        resInfo.setResIds(resIds);
        resInfo.setDuring(during);
        return resInfo;
    }

    private int[] resIds = null;
    private int[] during = null;
    private boolean isRepeat = false;

    public int[] getResIds() {
        return resIds;
    }

    public void setResIds(int[] resIds) {
        this.resIds = resIds;
    }

    public int[] getDuring() {
        return during;
    }

    public void setDuring(int[] during) {
        this.during = during;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }
}
