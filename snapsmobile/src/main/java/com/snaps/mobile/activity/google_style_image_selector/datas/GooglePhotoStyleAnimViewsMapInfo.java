package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.graphics.Point;
import android.util.SparseIntArray;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.Set;

/**
 * Created by ysjeong on 2017. 1. 11..
 */

public class GooglePhotoStyleAnimViewsMapInfo {
    private static final String TAG = GooglePhotoStyleAnimViewsMapInfo.class.getSimpleName();
    private SparseIntArray currentMapColumnCountInfo = null;
    private SparseIntArray nextTargetMapColumnCountInfo = null;
    private SparseIntArray prevTargetMapColumnCountInfo = null;

    private int currentAnimationOffsetRow = -1;
    private int nextTargetAnimationOffsetRow = -1;
    private int prevTargetAnimationOffsetRow = -1;

    private int row = -1;
    private int column = -1;

    private int prevX = Integer.MIN_VALUE;
    private int prevY = Integer.MIN_VALUE;

    public GooglePhotoStyleAnimViewsMapInfo() {
        currentMapColumnCountInfo = new SparseIntArray();
        nextTargetMapColumnCountInfo = new SparseIntArray();
        prevTargetMapColumnCountInfo = new SparseIntArray();
    }

    public void clear() {
        currentAnimationOffsetRow = -1;
        nextTargetAnimationOffsetRow = -1;
        prevTargetAnimationOffsetRow = -1;
        row = -1;
        column = -1;
        prevX = Integer.MIN_VALUE;
        prevY = Integer.MIN_VALUE;
        if (currentMapColumnCountInfo != null)
            currentMapColumnCountInfo.clear();
        if (nextTargetMapColumnCountInfo != null)
            nextTargetMapColumnCountInfo.clear();
        if (prevTargetMapColumnCountInfo != null)
            prevTargetMapColumnCountInfo.clear();
    }

    public void setNextTargetMapColumnCountInfo(Set<String> keySet) {
        setMapRowColumnCountInfo(keySet, ISnapsImageSelectConstants.ePINCH_TARGET_TYPE.NEXT);
    }

    public void setCurrentMapColumnCountInfo(Set<String> keySet) {
        setMapRowColumnCountInfo(keySet, ISnapsImageSelectConstants.ePINCH_TARGET_TYPE.CURRENT);
    }

    public void setPrevTargetMapColumnCountInfo(Set<String> keySet) {
        setMapRowColumnCountInfo(keySet, ISnapsImageSelectConstants.ePINCH_TARGET_TYPE.PREV);
    }

    public SparseIntArray getCurrentMapColumnCountInfo() {
        return currentMapColumnCountInfo;
    }

    public SparseIntArray getNextTargetMapColumnCountInfo() {
        return nextTargetMapColumnCountInfo;
    }

    public SparseIntArray getPrevTargetMapColumnCountInfo() {
        return prevTargetMapColumnCountInfo;
    }

    private void setMapRowColumnCountInfo(Set<String> keySet, ISnapsImageSelectConstants.ePINCH_TARGET_TYPE pinchType) {
        if (keySet == null || keySet.isEmpty()) return;

        try {
            int maxRow = 0;
            int maxColumn = 0;
            for (String key : keySet) {

                Point keyAndRow = ImageSelectUtils.getHolderRowAndColumnByKey(key);
                if (keyAndRow != null) {
                    int row = keyAndRow.y;
                    int column = keyAndRow.x;
                    maxRow = Math.max(row, maxRow);
                    maxColumn = Math.max(column, maxColumn);

                    switch (pinchType) {
                        case CURRENT:
                            if (currentMapColumnCountInfo == null) return;
                            currentMapColumnCountInfo.put(row, maxColumn + 1);
                            break;
                        case NEXT:
                            if (nextTargetMapColumnCountInfo == null) return;
                            nextTargetMapColumnCountInfo.put(row, maxColumn + 1);
                            break;
                        case PREV:
                            if (prevTargetMapColumnCountInfo == null) return;
                            prevTargetMapColumnCountInfo.put(row, maxColumn + 1);
                            break;
                    }
                }
            }
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    public int getCurrentAnimationOffsetRow() {
        return currentAnimationOffsetRow;
    }

    public void setCurrentAnimationOffsetRow(int currentAnimationOffsetRow) {
        this.currentAnimationOffsetRow = currentAnimationOffsetRow;
    }

    public int getPrevTargetAnimationOffsetRow() {
        return prevTargetAnimationOffsetRow;
    }

    public void setPrevTargetMapColumnCountInfo(SparseIntArray prevTargetMapColumnCountInfo) {
        this.prevTargetMapColumnCountInfo = prevTargetMapColumnCountInfo;
    }

    public void setPrevTargetAnimationOffsetRow(int prevTargetAnimationOffsetRow) {
        this.prevTargetAnimationOffsetRow = prevTargetAnimationOffsetRow;
    }

    public int getNextTargetAnimationOffsetRow() {
        return nextTargetAnimationOffsetRow;
    }

    public void setNextTargetAnimationOffsetRow(int nextTargetAnimationOffsetRow) {
        this.nextTargetAnimationOffsetRow = nextTargetAnimationOffsetRow;
    }

    public void setCurrentMapColumnCountInfo(SparseIntArray currentMapColumnCountInfo) {
        this.currentMapColumnCountInfo = currentMapColumnCountInfo;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void addRow() {
        this.row++;
    }

    public int getColumn() {
        return column;
    }

    public void addColumn() {
        this.column++;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getPrevX() {
        return prevX;
    }

    public void setPrevX(int prevX) {
        this.prevX = prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }
}
