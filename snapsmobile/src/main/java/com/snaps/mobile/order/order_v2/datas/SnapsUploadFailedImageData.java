package com.snaps.mobile.order.order_v2.datas;

import com.snaps.common.data.img.MyPhotoSelectImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysjeong on 2017. 4. 21..
 */

public class SnapsUploadFailedImageData {

    public static SnapsUploadFailedImageData newInstance() {
        return new SnapsUploadFailedImageData();
    }

    private List<MyPhotoSelectImageData> uploadFailedImageList = null;

    private SnapsUploadFailedImageData() {}

    public void addUploadFailedImageData(MyPhotoSelectImageData imageData) {
        if (getUploadFailedImageList() == null)
            uploadFailedImageList = new ArrayList<>();
        getUploadFailedImageList().add(imageData);
    }

    public void clear() {
        if (getUploadFailedImageList() != null) getUploadFailedImageList().clear();
    }

    public List<MyPhotoSelectImageData> getUploadFailedImageList() {
        return uploadFailedImageList;
    }
}
