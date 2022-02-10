package com.snaps.mobile.order.order_v2.util.org_image_upload;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * Created by ysjeong on 16. 5. 31..
 *
 */
public class SnapsThumbImgBackgroundUploadExecutor extends SnapsBaseImgBackgroundUploadExecutor {

    public static SnapsThumbImgBackgroundUploadExecutor createBackgroundUploadExecutorWithRejectedExecutionHandler(Activity activity, RejectedExecutionHandler rejectedExecutionHandler) {
        return new SnapsThumbImgBackgroundUploadExecutor(activity, rejectedExecutionHandler);
    }

    private SnapsThumbImgBackgroundUploadExecutor(Activity activity, RejectedExecutionHandler rejectedExecutionHandler) {
        super(activity, rejectedExecutionHandler);
    }

    @Override
    protected boolean isUploadedImageData(MyPhotoSelectImageData imageData) {
        return SnapsImageUploadUtil.isThumbImgUploadedOnImageData(imageData);
    }

    @Override
    protected void handleUploadImageOnBackground(MyPhotoSelectImageData imageData, SnapsImageUploadListener listener) throws Exception {
        SnapsImageUploadUtil.startThumbImgUploadWithImageData(imageData, listener);
    }

    @Override
    void sortImageList(ArrayList<MyPhotoSelectImageData> addImageList) {
        if (addImageList.size() > 1) {
            Collections.sort(addImageList, new Comparator<MyPhotoSelectImageData>() {
                @Override
                public int compare(MyPhotoSelectImageData lhs, MyPhotoSelectImageData rhs) {
                    return lhs.getUploadPriority() > rhs.getUploadPriority() ? -1 : (lhs.getUploadPriority() < rhs.getUploadPriority() ? 1 : 0);
                }
            });
        }
    }
}
