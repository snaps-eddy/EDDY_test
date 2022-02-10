package com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public class SnapsOrgImgUploadHandlerFactory {
    public static SnapsImageBaseUploadHandler createOrgImgUploadHandler(MyPhotoSelectImageData imageData) {
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) return new SnapsOrgImgDiaryUploadHandler(imageData);
        else return new SnapsOrgImgDefaultUploadHandler(imageData);
    }

    public static SnapsImageBaseUploadHandler createThumbImgUploadHandler(MyPhotoSelectImageData imageData) {
        return new SnapsThumbImgUploadHandler(imageData);
    }


}
