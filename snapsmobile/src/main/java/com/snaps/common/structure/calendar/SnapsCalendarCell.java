package com.snaps.common.structure.calendar;

import android.graphics.Paint;

/**
 * Created by ysjeong on 2016. 11. 10..
 */

public class SnapsCalendarCell {
    private int x;
    private int y;
    private float width;
    private int height;

    private Paint.Align align;

    public Paint.Align getAlign() {
        return align;
    }

    public String getAlignStr() {
        switch (getAlign()) {
            case LEFT:
                return ISnapsCalendarConstants.TEXT_ALIGN_LEFT;
            case RIGHT:
                return ISnapsCalendarConstants.TEXT_ALIGN_RIGHT;
        }
        return ISnapsCalendarConstants.TEXT_ALIGN_CENTER;
    }

    public void setAlign(String alignStr) {
        if (alignStr == null)
            this.align = Paint.Align.CENTER;
        else {
            if (alignStr.equalsIgnoreCase(ISnapsCalendarConstants.TEXT_ALIGN_LEFT)) {
                this.align = Paint.Align.LEFT;
            } else if (alignStr.equalsIgnoreCase(ISnapsCalendarConstants.TEXT_ALIGN_RIGHT)) {
                this.align = Paint.Align.RIGHT;
            } else if (alignStr.equalsIgnoreCase(ISnapsCalendarConstants.TEXT_ALIGN_CENTER)) {
                this.align = Paint.Align.CENTER;
            }
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
