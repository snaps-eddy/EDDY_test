package com.snaps.mobile.order.order_v2.util.org_image_upload;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * Created by ysjeong on 16. 5. 31..
 *
 */
public class SnapsOrgImgBackgroundUploadExecutor extends SnapsBaseImgBackgroundUploadExecutor {

    public static SnapsOrgImgBackgroundUploadExecutor createBackgroundUploadExecutorWithRejectedExecutionHandler(Activity activity, RejectedExecutionHandler rejectedExecutionHandler) {
        return new SnapsOrgImgBackgroundUploadExecutor(activity, rejectedExecutionHandler);
    }

    private SnapsOrgImgBackgroundUploadExecutor(Activity activity, RejectedExecutionHandler rejectedExecutionHandler) {
        super(activity, rejectedExecutionHandler);
    }

    @Override
    protected boolean isUploadedImageData(MyPhotoSelectImageData orgData) {
        return SnapsImageUploadUtil.isOrgImgUploadedOnImageData(orgData);
    }

    @Override
    protected void handleUploadImageOnBackground(MyPhotoSelectImageData imageData, SnapsImageUploadListener listener) throws Exception {
        SnapsImageUploadUtil.startOrgImgUploadWithImageData(imageData, listener);
    }
}
