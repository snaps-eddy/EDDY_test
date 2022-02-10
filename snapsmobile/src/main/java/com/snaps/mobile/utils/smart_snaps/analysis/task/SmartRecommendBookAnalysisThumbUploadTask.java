package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadThumbImgTask;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.smart_snaps.analysis.exception.SmartSnapsAnalysisException;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookAnalysisThumbUploadTask extends SmartRecommendBookAnalysisBaseTask {
    private static final String TAG = SmartRecommendBookAnalysisThumbUploadTask.class.getSimpleName();

    private SnapsOrderUploadThumbImgTask uploadThumbImgTask = null;

    SmartRecommendBookAnalysisThumbUploadTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        super(activity, analysisListener);
    }

    @Override
    public SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType() {
        return SmartSnapsConstants.eSmartSnapsAnalysisTaskType.UPLOAD_THUMBNAILS;
    }

    @Override
    public void perform() {
        super.perform();

        try {
            if (!Config.isValidProjCode()) throw new SmartSnapsAnalysisException("is not validProjCode.");

            startThumbnailsUpload();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            sendException(e);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (uploadThumbImgTask != null) {
            try {
//                uploadThumbImgTask.cancelImageUploadExecutor();
                uploadThumbImgTask.forceShutdownExecutor();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private void startThumbnailsUpload() throws Exception {
        ArrayList<MyPhotoSelectImageData> uploadImageList = null;
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            uploadImageList = dataTransManager.getPhotoImageDataList();
            SmartSnapsUtil.setSmartSnapsImgInfoWithImageList(uploadImageList);
        }

        final SnapsOrderAttribute attribute = new SnapsOrderAttribute.Builder().setActivity(getActivity()).create();
        uploadThumbImgTask = SnapsOrderUploadThumbImgTask.createInstanceWithAttribute(attribute);
        uploadThumbImgTask.performUploadImagesAtBackground(uploadImageList, new SnapsImageUploadListener() {
            @Override
            public void onImageUploadStart() {}

            @Override
            public void onImageUploadAllBackgroundTaskFinished() {
                sendComplete();
            }

            @Override
            public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
                if (uploadResultData != null) {
                    sendProgress(uploadResultData.getTotalImgCnt(), uploadResultData.getFinishedCnt());
                }
            }

            @Override
            public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
                if (uploadResultData != null
                        && uploadResultData.getUploadResultMsg() == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE) {
                    sendFailed(SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE.name());
                } else {
                    String errMsg = "onImageUploadFailed.";
                    if (uploadResultData != null && !StringUtil.isEmpty(uploadResultData.getDetailMsg())) {
                        errMsg = uploadResultData.getDetailMsg();
                    }
                    sendFailed(errMsg);
                }
            }
        });
    }
}
