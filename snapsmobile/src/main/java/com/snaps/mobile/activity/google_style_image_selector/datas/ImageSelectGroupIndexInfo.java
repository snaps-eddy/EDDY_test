package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.util.SparseArray;

import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

/**
 * Created by ysjeong on 2017. 1. 5..
 */

public class ImageSelectGroupIndexInfo {
    private SparseArray<String> photoIndexList; //그룹 내 사진이 선택 되었는 지

    private int startIdx; //그룹에서 첫번째 사진의 인덱스 (섹션은 포함하지 않는다.)
    private int endIdx; //마지막 사진 인덱스

    public void addPhotoIndex(int index, String imageKey) {
        if (photoIndexList == null) photoIndexList = new SparseArray<>();

        photoIndexList.put(index, imageKey);
    }

    public boolean isSelectedItem(int index) {
        return !(photoIndexList == null || photoIndexList.get(index) == null) && ImageSelectUtils.isContainsInImageHolder(photoIndexList.get(index));
    }

    public int getTotalCount() {
        return (endIdx - startIdx) + 1;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    public int getEndIdx() {
        return endIdx;
    }

    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }
}
