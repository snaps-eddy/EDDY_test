package com.snaps.mobile.order.order_v2.interfacies;

import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;

/**
 * Created by ysjeong on 2017. 4. 3..
 */

public interface SnapsImageUploadListener {

     enum eImageUploadState {
        START,
        FINISH,
        SUSPENDED,
        PROGRESS,
        FAILED,
        NOT_SUPPORT_THUMBNAIL_UPLOAD,
    }

    void onImageUploadStart();
    void onImageUploadAllBackgroundTaskFinished();
    void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData);
    void onImageUploadFailed(SnapsImageUploadResultData uploadResultData);
}
