package com.snaps.common.structure.calendar;

import android.graphics.Point;

import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 2016. 11. 10..
 */

public class SnapsCalendarTemplateInfo {
    private static final String TAG = SnapsCalendarTemplateInfo.class.getSimpleName();
    private int gridWidth;
    private int gridHeight;

    private float cellWidth;
    private int cellHeight;

    private int rowCount;
    private int cellType; //0 : horizontal 1 : vertical

    private float dayFontDiffY;
    private float dayTitleFontDiffY;

    private Point startOffset;
    private Point dayOffset;
    private Point dayTitleOffset;

    private boolean isFrontType = false; //back or front

    public boolean isTitleVerticalType() {
        return getCellType() == 1 || getCellType() == 3;
    }

    public boolean isFrontType() {
        return isFrontType;
    }

    public void setFrontType(boolean frontType) {
        isFrontType = frontType;
    }

    public float getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(float cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getCellType() {
        return cellType;
    }

    public void setCellType(String cellTypeStr) {
        if (cellTypeStr == null)
            this.cellType = 0;

        try {
            this.cellType = Integer.parseInt(cellTypeStr);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    public Point getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Point startOffset) {
        this.startOffset = startOffset;
    }

    public Point getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(Point dayOffset) {
        this.dayOffset = dayOffset;
    }

    public Point getDayTitleOffset() {
        return dayTitleOffset;
    }

    public void setDayTitleOffset(Point dayTitleOffset) {
        this.dayTitleOffset = dayTitleOffset;
    }

    public float getDayFontDiffY() {
        return dayFontDiffY;
    }

    public void setDayFontDiffY(String fontSize) {
        dayFontDiffY = calculateFontDiffY(fontSize);
    }

    public float getDayTitleFontDiffY() {
        return dayTitleFontDiffY;
    }

    public void setDayTitleFontDiffY(String fontSize) {
        dayTitleFontDiffY = calculateFontDiffY(fontSize);
    }

    private float calculateFontDiffY(String fontSize) {
        if (fontSize != null && fontSize.length() > 0) {
            float fDayTitleFontSize = 0.f;
            try {
                fDayTitleFontSize = Float.parseFloat(fontSize);
            } catch (NumberFormatException e) { Dlog.e(TAG, e); }
            return (float) (Math.ceil(fDayTitleFontSize * 1.2f) - fDayTitleFontSize);
        }
        return 0.f;
    }
}
