package com.snaps.mobile.order.order_v2.datas;

import com.snaps.common.data.img.MyPhotoSelectImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysjeong on 2017. 4. 24..
 */

public class SnapsUploadImageDataParams {

    public static SnapsUploadImageDataParams createInstanceWithImageList(List<MyPhotoSelectImageData> imgList) {
        return new SnapsUploadImageDataParams(imgList);
    }

    private List<MyPhotoSelectImageData> imageDataList = null;
    private int indexOfUploadTargetImgData = 0;
    private int indexOfPrevUploadedImgData = -1; //초기값이 indexOfUploadTargetImgData 와 같으면 안된다. (무한 로딩 체크용임.)

    private SnapsUploadImageDataParams(List<MyPhotoSelectImageData> imgList) {
        this.imageDataList = imgList;
    }

    private SnapsUploadImageDataParams() {
        imageDataList = new ArrayList<>();
    }

    public void addImageData(MyPhotoSelectImageData imageData) {
        if (getImageDataList() != null)
            getImageDataList().add(imageData);
    }

    public List<MyPhotoSelectImageData> getImageDataList() {
        return imageDataList;
    }

    public int getIndexOfUploadTargetImgData() {
        return indexOfUploadTargetImgData;
    }

    public void setIndexOfUploadTargetImgData(int indexOfUploadTargetImgData) {
        this.indexOfUploadTargetImgData = indexOfUploadTargetImgData;
    }

    public void addIndex() {
        ++this.indexOfUploadTargetImgData;
    }

    public int getIndexOfPrevUploadedImgData() {
        return indexOfPrevUploadedImgData;
    }

    public void setIndexOfPrevUploadedImgData(int indexOfPrevUploadedImgData) {
        this.indexOfPrevUploadedImgData = indexOfPrevUploadedImgData;
    }
}
