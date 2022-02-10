package com.snaps.common.data.smart_snaps.interfacies;

import com.snaps.common.data.img.MyPhotoSelectImageData;

/**
 * Created by ysjeong on 2018. 1. 17..
 */

public interface SmartSnapsUploadResultListener {
    void onSmartSnapsImgUploadSuccess(MyPhotoSelectImageData uploadedImageData);
    void onSmartSnapsImgUploadFailed(MyPhotoSelectImageData uploadedImageData);
}
