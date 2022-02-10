package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadImageDataParams;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsOrgImgBackgroundUploadExecutor;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadOrgImgTask extends SnapsOrderUploadBaseImgTask {

    public static SnapsOrderUploadOrgImgTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderUploadOrgImgTask(attribute);
    }

    private SnapsOrderUploadOrgImgTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    @Override
    protected void initBackgroundUploadExecutor() {
        backgroundImgUploadExecutor = SnapsOrgImgBackgroundUploadExecutor.createBackgroundUploadExecutorWithRejectedExecutionHandler(getActivity(), new ThreadPoolExecutor.DiscardPolicy()); //최대 쓰레드 초과 시 무시 정책
    }

    @Override
    protected void prepareUploadAllImageList(ArrayList<MyPhotoSelectImageData> uploadList) {
        SnapsTimerProgressView.initUploadImageList(uploadList);
    }

    @Override
    protected void startImageUploadByAsync(MyPhotoSelectImageData uploadTargetImageData, SnapsImageUploadResultListener currentOrgImgUploadListener) throws Exception {
        SnapsImageUploadUtil.startOrgImgUploadByAsync(uploadTargetImageData, currentOrgImgUploadListener);
    }

    @Override
    protected SnapsOrderConstants.eSnapsOrderType getOrderType() {
        return SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE;
    }

    @Override
    protected void handleOnImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {}

    @Override
    protected void handleOnImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
        if (isUploadErrorImageData(uploadResultData))
            collectFailedUploadImageData(uploadResultData); //업로드 실패한 이미지는 모아 놓고 마지막에 실패한 리스트를 보여 준다.
    }

    private void collectFailedUploadImageData(SnapsImageUploadResultData uploadResultData) {
        if (uploadResultData == null || uploadResultData.getImageData() == null) return;
        SnapsUploadFailedImageDataCollector.addUploadFailedImageData(SnapsOrderManager.getProjectCode(), uploadResultData.getImageData());
    }

    @Override
    protected boolean isUploadedStateOnImageData(MyPhotoSelectImageData orgData) {
        return SnapsImageUploadUtil.isOrgImgUploadedOnImageData(orgData);
    }

    @Override
    protected void handleOnAllImageUploaded(SnapsImageUploadResultData uploadResultData, SnapsOrderResultListener orgImgUploadListener) throws Exception {
        if (SnapsUploadFailedImageDataCollector.isExistFailedImageData(SnapsOrderManager.getProjectCode())) {
            orgImgUploadListener.onSnapsOrderResultFailed(uploadResultData, getOrderType());
        } else {
            orgImgUploadListener.onSnapsOrderResultSucceed(null);
        }
    }

    @Override
    protected void handleOnTryUploadNextImageData(SnapsImageUploadResultData uploadResultData, SnapsUploadImageDataParams imageDataParams) throws Exception {
        if (isAllSamePhoto()) { //증명 사진 같은 경우에는 1장의 사진이 모두 같은 사진이기 때문에 여러장을 올린 필요가 없다.
            copyUploadInfoToAllPhotos(uploadResultData, imageDataParams);
        }
    }

    private boolean isAllSamePhoto() {
        return Config.isIdentifyPhotoPrint();
    }

    private void copyUploadInfoToAllPhotos(SnapsImageUploadResultData uploadResultData, SnapsUploadImageDataParams imageDataParams) throws Exception {
        MyPhotoSelectImageData uploadedImageData = uploadResultData.getImageData();
        ArrayList<MyPhotoSelectImageData> uploadList = (ArrayList<MyPhotoSelectImageData>) imageDataParams.getImageDataList();
        for (MyPhotoSelectImageData imageData : uploadList) {
            imageData.F_IMG_YEAR = uploadedImageData.F_IMG_YEAR;
            imageData.F_IMG_SQNC = uploadedImageData.F_IMG_SQNC;
            imageData.F_UPLOAD_PATH = uploadedImageData.F_UPLOAD_PATH;
            imageData.THUMBNAIL_PATH = uploadedImageData.THUMBNAIL_PATH;
            imageData.ORIGINAL_PATH = uploadedImageData.ORIGINAL_PATH;
        }
    }
}
