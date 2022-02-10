package com.snaps.mobile.activity.diary.recoder;

/**
 * Created by ysjeong on 16. 3. 7..
 */
public class SnapsInkIcon {
    private int iconId = 0;
    private boolean isEnable = false;

    public SnapsInkIcon(int id) {
        setIconId(id);
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }
}
