package com.snaps.mobile.order.order_v2.interfacies;

import com.snaps.common.data.img.MyPhotoSelectImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ysjeong on 2017. 3. 29..
 */

public interface SnapsOrderTaskImp {
    //원본 사진 업로드
    void performUploadOrgImages(final SnapsOrderResultListener orgImgUploadListener) throws Exception;

    //원본 사진 업로드 (백그라운드에서)
    void performUploadOrgImagesAtBackground(final ArrayList<MyPhotoSelectImageData> imageList, final SnapsImageUploadListener orgImgUploadListener) throws Exception;

    //썸네일 사진 업로드 (백그라운드에서)
    void performUploadThumbImagesAtBackground(final ArrayList<MyPhotoSelectImageData> imageList, final SnapsImageUploadListener orgImgUploadListener) throws Exception;

    void performUploadThumbImgAtBackground(final MyPhotoSelectImageData imageData, final SnapsImageUploadListener orgImgUploadListener) throws Exception;

    void suspendBackgroundOrgImageUpload() throws Exception;

    void suspendBackgroundThumbImageUpload() throws Exception;

    void cancelThumbnailImgUploadExecutor() throws Exception;

    void cancelOrgImgUploadExecutor() throws Exception;

    void removeBackgroundUploadingOrgImgDataList(List<MyPhotoSelectImageData> removeList) throws Exception;

    void removeBackgroundUploadingOrgImgData(MyPhotoSelectImageData removeData) throws Exception;

    void removeBackgroundUploadingThumbImgData(MyPhotoSelectImageData removeData) throws Exception;

    //메인 페이지 썸네일 생성요청
    void requestMakeMainPageThumbnailFile(final SnapsOrderResultListener makeThumbnailListener) throws Exception;

    //모든 페이지 썸네일 생성 요청
    void requestMakePagesThumbnailFile(final SnapsOrderResultListener makeThumbnailListener) throws Exception;

    //메인 썸네일 업로드
    void performUploadMainThumbnail(final SnapsOrderResultListener uploadThumbnailListener) throws Exception;

    //XML 생성
    void performMakeXML(final SnapsOrderResultListener makeXmlListener) throws Exception;
    
    //XML 업로드
    void performUploadXML(final SnapsOrderResultListener uploadXmlListener) throws Exception;

    boolean isContainedUploadingImageData(MyPhotoSelectImageData imageData) throws Exception;
}
