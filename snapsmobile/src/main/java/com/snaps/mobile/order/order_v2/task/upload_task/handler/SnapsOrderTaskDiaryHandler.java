package com.snaps.mobile.order.order_v2.task.upload_task.handler;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderTaskFactory;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderMakePageThumbnailsTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadOrgImgTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderDiaryMissionStateCheckTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderGetDiarySequenceTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderUploadDiaryThumbnailTask;
import com.snaps.mobile.order.order_v2.task.upload_task.diary_task.SnapsOrderUploadDiaryXMLTask;

import java.util.ArrayList;
import java.util.List;

import errorhandle.SnapsAssert;

/**
 * Created by ysjeong on 2017. 3. 29..
 */

public class SnapsOrderTaskDiaryHandler extends SnapsOrderTaskBaseHandler {
    private static final String TAG = SnapsOrderTaskDiaryHandler.class.getSimpleName();

    private SnapsOrderGetDiarySequenceTask getDiarySeqTask = null;
    private SnapsOrderDiaryMissionStateCheckTask diaryMissionStateCheckTask = null;
    private SnapsOrderUploadDiaryThumbnailTask diaryThumbnailUploadTask = null;
    private SnapsOrderUploadDiaryXMLTask diaryXMLTask = null;

    private SnapsOrderTaskDiaryHandler(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        super(attribute, snapsOrderActivityBridge);

        createAllOrderTask(attribute);
    }

    public static SnapsOrderTaskDiaryHandler createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        return new SnapsOrderTaskDiaryHandler(attribute, snapsOrderActivityBridge);
    }

    public void finalizeInstance() throws Exception {
        super.finalizeInstance();

        if (getDiarySeqTask != null)
            getDiarySeqTask.finalizeInstance();

        if (diaryMissionStateCheckTask != null)
            diaryMissionStateCheckTask.finalizeInstance();

        if (diaryThumbnailUploadTask != null)
            diaryThumbnailUploadTask.finalizeInstance();

        if (diaryXMLTask != null)
            diaryXMLTask.finalizeInstance();

        setGetDiarySeqTask(null);
        setDiaryMissionStateCheckTask(null);
        setOrgImgUploadTask(null);
        setDiaryXMLTask(null);
    }

    @Override
    protected void createAllOrderTask(SnapsOrderAttribute attribute) throws Exception {
        setGetDiarySeqTask((SnapsOrderGetDiarySequenceTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_DIARY_SEQ_CODE, attribute));

        setDiaryMissionStateCheckTask((SnapsOrderDiaryMissionStateCheckTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_CHECK_DIARY_MISSION_STATE, attribute));

        setOrgImgUploadTask((SnapsOrderUploadOrgImgTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE, attribute));

        setMakePageThumbnailsTask((SnapsOrderMakePageThumbnailsTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_PAGE_THUMBNAILS, attribute));

        getMakePageThumbnailsTask().setActivityBridge(getSnapsOrderActivityBridge());

        setDiaryThumbnailUploadTask((SnapsOrderUploadDiaryThumbnailTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_DIARY_THUMBNAIL, attribute));

        setDiaryXMLTask((SnapsOrderUploadDiaryXMLTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_DIARY_XML, attribute));
    }

    private void performGetDiarySeq(final SnapsOrderResultListener getProjectCodeListener) throws Exception {
        getGetDiarySeqTask().getDiarySequenceCode(new SnapsOrderResultListener() {
            @Override
            public void onSnapsOrderResultSucceed(Object msgObj) {
                try {
                    performVerifyProjectCode(getProjectCodeListener);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    if (getProjectCodeListener != null) getProjectCodeListener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_VERIFY_PROJECT_CODE);
                    SnapsAssert.assertException(getAttribute().getActivity(), e);
                }
            }

            @Override
            public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                if (getProjectCodeListener != null) getProjectCodeListener.onSnapsOrderResultFailed(resultMsgObj, orderType);
            }
        });
    }

    @Override
    public void getProjectCode(final SnapsOrderResultListener getProjectCodeListener) {
        try {
            if (!SnapsDiaryDataManager.isExistDiarySeqNo()) {
                performGetDiarySeq(getProjectCodeListener);
            } else {
                performVerifyProjectCode(getProjectCodeListener);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (getProjectCodeListener != null) getProjectCodeListener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_DIARY_SEQ_CODE);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performVerifyProjectCode(SnapsOrderResultListener verifyProjectCodeListener) throws Exception {
        if (getDiaryMissionStateCheckTask() != null)
            getDiaryMissionStateCheckTask().getDiaryMissionState(verifyProjectCodeListener);
    }

    @Override
    public void performUploadOrgImages(SnapsOrderResultListener orgImgUploadListener) throws Exception {
        if (getOrgImgUploadTask() != null)
            getOrgImgUploadTask().uploadAllImageList(getSnapsOrderActivityBridge().getUploadImageList(), orgImgUploadListener);
    }

    @Override
    public void performUploadOrgImagesAtBackground(ArrayList<MyPhotoSelectImageData> imageList, SnapsImageUploadListener orgImgUploadListener) throws Exception {
        if (getOrgImgUploadTask() != null)
            getOrgImgUploadTask().performUploadImagesAtBackground(imageList, orgImgUploadListener);
    }

    @Override
    public void performUploadThumbImagesAtBackground(ArrayList<MyPhotoSelectImageData> imageList, SnapsImageUploadListener orgImgUploadListener) throws Exception {
        try {
            if (getThumbImgUploadTask() != null)
                getThumbImgUploadTask().performUploadImagesAtBackground(imageList, orgImgUploadListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (orgImgUploadListener != null) orgImgUploadListener.onImageUploadFailed(null);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performUploadThumbImgAtBackground(MyPhotoSelectImageData imageData, SnapsImageUploadListener orgImgUploadListener) throws Exception {
        try {
            if (getThumbImgUploadTask() != null)
                getThumbImgUploadTask().performUploadImageAtBackground(imageData, orgImgUploadListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (orgImgUploadListener != null) orgImgUploadListener.onImageUploadFailed(null);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void suspendBackgroundOrgImageUpload() throws Exception {
        if (getOrgImgUploadTask() != null)
            getOrgImgUploadTask().suspendBackgroundImgUpload();
    }

    @Override
    public void suspendBackgroundThumbImageUpload() throws Exception {
        if (getThumbImgUploadTask() != null)
            getThumbImgUploadTask().suspendBackgroundImgUpload();
    }

    @Override
    public void removeBackgroundUploadingOrgImgDataList(List<MyPhotoSelectImageData> removeList) throws Exception {
        if (getOrgImgUploadTask() != null)
            getOrgImgUploadTask().removeBackgroundUploadingImageDataList(removeList);
    }

    @Override
    public void removeBackgroundUploadingOrgImgData(MyPhotoSelectImageData removeData) throws Exception {
        if (getOrgImgUploadTask() != null)
            getOrgImgUploadTask().removeBackgroundUploadingImageData(removeData);
    }

    @Override
    public void removeBackgroundUploadingThumbImgData(MyPhotoSelectImageData removeData) throws Exception {
        if (getThumbImgUploadTask() != null)
            getThumbImgUploadTask().removeBackgroundUploadingImageData(removeData);
    }

    @Override
    public void requestMakePagesThumbnailFile(SnapsOrderResultListener makeThumbnailListener) throws Exception {}

    @Override
    public void requestMakeMainPageThumbnailFile(SnapsOrderResultListener makeThumbnailListener) throws Exception {
        if (getMakePageThumbnailsTask() != null)
            getMakePageThumbnailsTask().requestMakeOnlyMainPageThumbnailFile(makeThumbnailListener);
    }

    @Override
    public void performUploadMainThumbnail(SnapsOrderResultListener uploadThumbnailListener) throws Exception {
        if (getDiaryThumbnailUploadTask() != null)
            getDiaryThumbnailUploadTask().uploadMainThumbnail(uploadThumbnailListener);
    }

    @Override
    public void performMakeXML(SnapsOrderResultListener makeXmlListener) throws Exception {
        if (getDiaryXMLTask() != null)
            getDiaryXMLTask().performMakeXML(makeXmlListener);
    }

    @Override
    public void performUploadXML(SnapsOrderResultListener uploadXmlListener) throws Exception {
        if (getDiaryXMLTask() != null)
            getDiaryXMLTask().performUploadXML(uploadXmlListener);
    }

    public SnapsOrderGetDiarySequenceTask getGetDiarySeqTask() {
        return getDiarySeqTask;
    }

    public void setGetDiarySeqTask(SnapsOrderGetDiarySequenceTask getDiarySeqTask) {
        this.getDiarySeqTask = getDiarySeqTask;
    }

    public SnapsOrderDiaryMissionStateCheckTask getDiaryMissionStateCheckTask() {
        return diaryMissionStateCheckTask;
    }

    public void setDiaryMissionStateCheckTask(SnapsOrderDiaryMissionStateCheckTask diaryMissionStateCheckTask) {
        this.diaryMissionStateCheckTask = diaryMissionStateCheckTask;
    }

    public SnapsOrderUploadDiaryThumbnailTask getDiaryThumbnailUploadTask() {
        return diaryThumbnailUploadTask;
    }

    public void setDiaryThumbnailUploadTask(SnapsOrderUploadDiaryThumbnailTask diaryThumbnailUploadTask) {
        this.diaryThumbnailUploadTask = diaryThumbnailUploadTask;
    }

    public SnapsOrderUploadDiaryXMLTask getDiaryXMLTask() {
        return diaryXMLTask;
    }

    public void setDiaryXMLTask(SnapsOrderUploadDiaryXMLTask diaryXMLTask) {
        this.diaryXMLTask = diaryXMLTask;
    }

    @Override
    public void cancelThumbnailImgUploadExecutor() throws Exception {}

    @Override
    public void cancelOrgImgUploadExecutor() throws Exception {}
}
