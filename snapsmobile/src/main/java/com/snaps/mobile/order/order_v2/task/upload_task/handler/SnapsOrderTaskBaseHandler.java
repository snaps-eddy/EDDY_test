package com.snaps.mobile.order.order_v2.task.upload_task.handler;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderGetPROJCodeTaskImp;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderTaskImp;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderMakePageThumbnailsTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadOrgImgTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadThumbImgTask;

/**
 * Created by ysjeong on 2017. 3. 29..
 */

public abstract class SnapsOrderTaskBaseHandler implements SnapsOrderTaskImp, SnapsOrderGetPROJCodeTaskImp {
    private static final String TAG = SnapsOrderTaskBaseHandler.class.getSimpleName();

    private SnapsOrderAttribute attribute = null;
    private SnapsOrderActivityBridge snapsOrderActivityBridge = null;

    private SnapsOrderUploadOrgImgTask orgImgUploadTask = null;
    private SnapsOrderUploadThumbImgTask thumbImgUploadTask = null;
    private SnapsOrderMakePageThumbnailsTask makePageThumbnailsTask = null;

    public SnapsOrderTaskBaseHandler(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        this.setAttribute(attribute);
        this.setSnapsOrderActivityBridge(snapsOrderActivityBridge);
    }

    protected abstract void createAllOrderTask(SnapsOrderAttribute attribute) throws Exception;

    public void finalizeInstance() throws Exception {
        attribute = null;

        snapsOrderActivityBridge = null;

        if (orgImgUploadTask != null) {
            orgImgUploadTask.finalizeInstance();
            orgImgUploadTask = null;
        }

        if (makePageThumbnailsTask != null) {
            makePageThumbnailsTask.finalizeInstance();
            makePageThumbnailsTask = null;
        }
    }

    public SnapsOrderAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(SnapsOrderAttribute attribute) {
        this.attribute = attribute;
    }

    public SnapsOrderUploadOrgImgTask getOrgImgUploadTask() {
        return orgImgUploadTask;
    }

    public void setOrgImgUploadTask(SnapsOrderUploadOrgImgTask orgImgUploadTask) {
        this.orgImgUploadTask = orgImgUploadTask;
    }

    public SnapsOrderUploadThumbImgTask getThumbImgUploadTask() {
        return thumbImgUploadTask;
    }

    public void setThumbImgUploadTask(SnapsOrderUploadThumbImgTask thumbImgUploadTask) {
        this.thumbImgUploadTask = thumbImgUploadTask;
    }

    public SnapsOrderMakePageThumbnailsTask getMakePageThumbnailsTask() {
        return makePageThumbnailsTask;
    }

    public void setMakePageThumbnailsTask(SnapsOrderMakePageThumbnailsTask makePageThumbnailsTask) {
        this.makePageThumbnailsTask = makePageThumbnailsTask;
    }

    public SnapsOrderActivityBridge getSnapsOrderActivityBridge() {
        return snapsOrderActivityBridge;
    }

    public void setSnapsOrderActivityBridge(SnapsOrderActivityBridge snapsOrderActivityBridge) {
        this.snapsOrderActivityBridge = snapsOrderActivityBridge;
    }

    @Override
    public boolean isContainedUploadingImageData(MyPhotoSelectImageData imageData) throws Exception {
        try {
            return getThumbImgUploadTask().isContainedUploadingImageData(imageData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }
}
