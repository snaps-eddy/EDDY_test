package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderMakePageThumbnailsTask extends SnapsOrderBaseTask implements ISnapsCaptureListener {

    private SnapsOrderResultListener listener = null;

    private SnapsOrderMakePageThumbnailsTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderMakePageThumbnailsTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderMakePageThumbnailsTask(attribute);
    }

    //메인 썸네일만 딴다.
    public void requestMakePageThumbnailFiles(final SnapsOrderResultListener listener) {
        this.listener = listener;
        getActivityBridge().requestMakePagesThumbnailFile(this);
    }

    public void requestMakeOnlyMainPageThumbnailFile(final SnapsOrderResultListener listener) throws Exception {
        this.listener = listener;
        getActivityBridge().requestMakeMainPageThumbnailFile(this);
    }

    @Override
    public void onFinishPageCapture(boolean result) {
        if (result) {
            this.listener.onSnapsOrderResultSucceed(null);
        } else {
            this.listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_PAGE_THUMBNAILS);
        }
    }

    @Override
    public void finalizeInstance() throws Exception {
        super.finalizeInstance();

        listener = null;
    }
}
