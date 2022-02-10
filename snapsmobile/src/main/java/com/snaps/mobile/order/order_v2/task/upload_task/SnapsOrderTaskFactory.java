package com.snaps.mobile.order.order_v2.task.upload_task;

import androidx.annotation.NonNull;

import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderGetProjectCodeTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderMakePageThumbnailsTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadMainThumbnailTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadOrgImgTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadThumbImgTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadXMLTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderVerifyProjectCodeTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderDiaryMissionStateCheckTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderGetDiarySequenceTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderUploadDiaryThumbnailTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderUploadDiaryXMLTask;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderTaskFactory {
    public static SnapsOrderBaseTask createSnapsOrderTask(@NonNull SnapsOrderConstants.eSnapsOrderType snapsOrderType,
                                                          @NonNull SnapsOrderAttribute attribute) throws SnapsOrderException {
        switch (snapsOrderType) {
            case ORDER_TYPE_GET_PROJECT_CODE:
                return SnapsOrderGetProjectCodeTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_VERIFY_PROJECT_CODE:
                return SnapsOrderVerifyProjectCodeTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_UPLOAD_ORG_IMAGE:
                return SnapsOrderUploadOrgImgTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_UPLOAD_THUMB_IMAGE:
                return SnapsOrderUploadThumbImgTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_MAKE_PAGE_THUMBNAILS:
                return SnapsOrderMakePageThumbnailsTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL:
                return SnapsOrderUploadMainThumbnailTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_UPLOAD_XML:
                return SnapsOrderUploadXMLTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_GET_DIARY_SEQ_CODE:
                return SnapsOrderGetDiarySequenceTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_CHECK_DIARY_MISSION_STATE:
                return SnapsOrderDiaryMissionStateCheckTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_UPLOAD_DIARY_XML:
                return SnapsOrderUploadDiaryXMLTask.createInstanceWithAttribute(attribute);
            case ORDER_TYPE_UPLOAD_DIARY_THUMBNAIL:
                return SnapsOrderUploadDiaryThumbnailTask.createInstanceWithAttribute(attribute);
            default:
                throw new SnapsOrderException(SnapsOrderConstants.EXCEPTION_MSG_TASK_UNKNOWN);
        }
    }
}


