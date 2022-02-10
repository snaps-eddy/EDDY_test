package com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies;

import android.os.Handler;
import android.os.Looper;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public abstract class SnapsImageBaseUploadHandler {

    private MyPhotoSelectImageData imageData = null;

    public SnapsImageBaseUploadHandler(MyPhotoSelectImageData imageData) {
        setImageData(imageData);
    }

    public abstract String requestImageUpload() throws Exception;

    public abstract void handleAnalyzeUploadResultMsg(String message, final SnapsImageUploadListener orgImgUploadListener) throws Exception;

    protected void sendResultWithImageDataSyncUnLock(final boolean isSuccess, final SnapsImageUploadResultData resultData, final SnapsImageUploadListener orgImgUploadListener) {
        getImageData().finishUploadSyncLock();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    sendResultForUploadSuccess(resultData, orgImgUploadListener);
                } else {
                    sendResultForUploadFail(resultData, orgImgUploadListener);
                }
            }
        });
    }

    protected void sendResultForUploadFail(SnapsImageUploadResultData resultData, SnapsImageUploadListener orgImgUploadListener) {
        if (orgImgUploadListener != null)
            orgImgUploadListener.onImageUploadFailed(resultData);
    }

    protected void sendResultForUploadSuccess(SnapsImageUploadResultData resultData, SnapsImageUploadListener orgImgUploadListener) {
        if (orgImgUploadListener != null)
            orgImgUploadListener.onImageUploadSucceed(resultData);
    }

    public MyPhotoSelectImageData getImageData() {
        return imageData;
    }

    public void setImageData(MyPhotoSelectImageData imageData) {
        this.imageData = imageData;
    }
}
