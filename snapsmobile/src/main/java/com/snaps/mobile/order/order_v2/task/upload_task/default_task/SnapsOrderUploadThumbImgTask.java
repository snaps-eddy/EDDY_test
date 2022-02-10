package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadImageDataParams;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.util.CallerRunsByThreadPolicy;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsBaseImgBackgroundUploadExecutor;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsThumbImgBackgroundUploadExecutor;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadThumbImgTask extends SnapsOrderUploadBaseImgTask {

    public static SnapsOrderUploadThumbImgTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderUploadThumbImgTask(attribute);
    }

    private SnapsOrderUploadThumbImgTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    @Override
    protected void initBackgroundUploadExecutor() {
        backgroundImgUploadExecutor = SnapsThumbImgBackgroundUploadExecutor.createBackgroundUploadExecutorWithRejectedExecutionHandler(getActivity(), new CallerRunsByThreadPolicy(SnapsBaseImgBackgroundUploadExecutor.USE_WORK_THREAD_COUNT));
    }

    @Override
    protected void prepareUploadAllImageList(ArrayList<MyPhotoSelectImageData> uploadList) {}

    @Override
    protected void startImageUploadByAsync(MyPhotoSelectImageData uploadTargetImageData, SnapsImageUploadResultListener currentOrgImgUploadListener) throws Exception {
        SnapsImageUploadUtil.startThumbImgUploadByAsync(uploadTargetImageData, currentOrgImgUploadListener);
    }

    @Override
    protected SnapsOrderConstants.eSnapsOrderType getOrderType() {
        return SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_THUMB_IMAGE;
    }

    @Override
    protected void handleOnImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {}

    @Override
    protected void handleOnImageUploadFailed(SnapsImageUploadResultData uploadResultData) {}

    @Override
    protected boolean isUploadedStateOnImageData(MyPhotoSelectImageData orgData) {
        return SnapsImageUploadUtil.isThumbImgUploadedOnImageData(orgData);
    }

    @Override
    protected void handleOnAllImageUploaded(SnapsImageUploadResultData uploadResultData, SnapsOrderResultListener orgImgUploadListener) throws Exception {
        orgImgUploadListener.onSnapsOrderResultSucceed(null);
    }

    @Override
    protected void handleOnTryUploadNextImageData(SnapsImageUploadResultData uploadResultData, SnapsUploadImageDataParams imageDataParams) throws Exception {}
}
