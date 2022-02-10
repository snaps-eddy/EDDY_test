package com.snaps.mobile.activity.google_style_image_selector.datas;

import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;

/**
 * Created by ysjeong on 2017. 1. 9..
 */

public class ImageSelectPhonePhotoFragmentData {

    private ImageSelectPhonePhotoData phonePhotoData = null;

    private ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH currentUIDepth;

    private int currentUIDepthThumbnailSize = 0;

    public boolean isCreatedPhotoPhotosDataList() {
        return phonePhotoData != null && phonePhotoData.getArrCursor() != null && !phonePhotoData.getArrCursor().isEmpty();
    }

    public void releaseInstance() {
        if (phonePhotoData != null) {
            phonePhotoData.releaseInstace();
        }
    }

    public ImageSelectPhonePhotoData getPhonePhotoData() {
        return phonePhotoData;
    }

    public void setPhonePhotoData(ImageSelectPhonePhotoData phonePhotoData) {
        this.phonePhotoData = phonePhotoData;
    }

    public ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH getCurrentUIDepth() {
        return currentUIDepth;
    }

    public void setCurrentUIDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH currentUIDepth) {
        this.currentUIDepth = currentUIDepth;
    }

    public int getCurrentUIDepthThumbnailSize() {
        return currentUIDepthThumbnailSize;
    }

    public void setCurrentUIDepthThumbnailSize(int currentUIDepthThumbnailSize) {
        this.currentUIDepthThumbnailSize = currentUIDepthThumbnailSize;
    }
}
