package com.snaps.mobile.order.order_v2.interfacies;

import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;

public interface SnapsImageUploadStateListener {
    void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData);

    void onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData);
}
