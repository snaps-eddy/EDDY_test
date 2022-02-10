package com.snaps.mobile.order.order_v2.task.upload_task.diary_task;

import com.snaps.common.data.net.CustomMultiPartEntity;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryBaseResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryGsonUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryImgUploadResultJson;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadDiaryThumbnailTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderUploadDiaryThumbnailTask.class.getSimpleName();
    private SnapsOrderUploadDiaryThumbnailTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderUploadDiaryThumbnailTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderUploadDiaryThumbnailTask(attribute);
    }

    public void uploadMainThumbnail(final SnapsOrderResultListener listener) throws Exception {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            @Override
            public void onPre() {
            }

            @Override
            public boolean onBG() {
                String fileName = null;
                if(getPageList() != null && !getPageList().isEmpty()) {
                    fileName = getPageList().get(0).thumbnailPath;
                }

                String message = GetMultiPartMethod.getDiaryOrgImageUplad(fileName, true, new CustomMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num, long total) {
                    }
                }, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

                Dlog.d("uploadMainThumbnail() result:" + message);

                if (!StringUtil.isEmpty(message)) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(message, SnapsDiaryImgUploadResultJson.class);
                    if (result != null && result.isSuccess()) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onSnapsOrderResultSucceed(null);
                } else {
                    listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL);
                }
            }
        });
    }
}
