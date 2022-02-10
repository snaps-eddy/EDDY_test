package com.snaps.mobile.product_native_ui.interfaces;

import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;

/**
 * Created by ysjeong on 2016. 11. 2..
 */

public interface ISnapsProductListOpserver {
    void onRequestedProductListSort(SnapsNativeUIManager.PRODUCT_LIST_SORT_TYPE sortType);
    void onRequestedGridViewModeChange(boolean isLargeView);
}
