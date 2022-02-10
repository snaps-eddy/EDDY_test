package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies;


import android.graphics.Rect;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;

import java.util.Map;

public interface ISmartRecommendBookPageEditDragNDropBridge {
    Map<Rect, SnapsLayoutControl> getCurrentlyVisibleControlsRect();

    void onStopDragging(SnapsLayoutControl targetLayoutControl, MyPhotoSelectImageData newImageData);
}
