package com.snaps.mobile.activity.google_style_image_selector.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;

import com.snaps.common.utils.ui.BSize;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ifunbae on 2017. 1. 9..
 */

public class StaggeredLayoutCalculator {

    final float MAX_HEIGHT;

    ArrayList<BSize> leftOvers = null;
    SparseArray<BSize> cache = null;
    List<GalleryCursorRecord.PhonePhotoFragmentItem> data = null;

    Integer lastIndexed = 0;
    float contentWidth = 0;


    public StaggeredLayoutCalculator(Context context, List<GalleryCursorRecord.PhonePhotoFragmentItem> data) {

        MAX_HEIGHT = UIUtil.getScreenHeight(context) * .6f;

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        leftOvers = new ArrayList<BSize>();
        cache = new SparseArray<BSize>();

        this.contentWidth = metrics.widthPixels;
        this.data = data;

        int index = 0;
        for (GalleryCursorRecord.PhonePhotoFragmentItem item : data) {
            if (item == null) continue;
            BSize calculatedSize = getSizeWithIndex(index);
            item.setConvertedStaggeredSize(calculatedSize);
            index++;
        }
    }

    /**
     * 셀 사이즈를 반환하는 함수
     */
    public BSize getSizeWithIndex(Integer index) {
        if (cache.get(index) == null) {
            lastIndexed = index;
            computeSizes(index);
        }

        BSize size = cache.get(index);
        if (size == null || size.getWidth() == 0 || size.getHeight() == 0) {
            size = new BSize();
        }

        return size;
    }


    void computeSizes(int index) {

        GalleryCursorRecord.PhonePhotoFragmentItem item = data.get(index);

        int width = 0, height = 0;

        if (item.getPhoneDetailOrientation() == 90 || item.getPhoneDetailOrientation() == 270) {
            width = item.getImgOutHeight();
            height = item.getImgOutWidth();
        } else {
            width = item.getImgOutWidth();
            height = item.getImgOutHeight();
        }

        BSize photoSize = new BSize(width, height);

        if (photoSize.getWidth() < 1 || photoSize.getHeight() < 1) {
            photoSize.setHeight(MAX_HEIGHT);
            photoSize.setWidth(MAX_HEIGHT);
        }

        leftOvers.add(photoSize);
        boolean isEnoughContentForRow = false;
        float rowHeight = MAX_HEIGHT;

        float totalAspectRatio = 0;

        for (BSize s : leftOvers) {
            totalAspectRatio += (s.getWidth() / s.getHeight());
        }

        rowHeight = contentWidth / totalAspectRatio;
        isEnoughContentForRow = rowHeight < MAX_HEIGHT;

        // isEnoughContentForRow false인데 다음 사진이 없는 경우 처리..
        if (isEnoughContentForRow || data.size() - 1 <= index) {
            float availableSpace = contentWidth;
            for (BSize bSize : leftOvers) {
                float newWidth = (float) Math.floor((rowHeight * bSize.getWidth()) / bSize.getHeight());
                newWidth = Math.min(availableSpace, newWidth);
                cache.append(lastIndexed, new BSize(newWidth,rowHeight));

                availableSpace -= newWidth;
                lastIndexed = lastIndexed + 1;
            }
            leftOvers.clear();

        } else {
            computeSizes(index + 1);
        }
    }


}
