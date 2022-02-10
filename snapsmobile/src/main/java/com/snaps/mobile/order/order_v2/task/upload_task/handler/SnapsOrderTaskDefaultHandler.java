package com.snaps.mobile.order.order_v2.task.upload_task.handler;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderTaskFactory;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderGetProjectCodeTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderMakePageThumbnailsTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadMainThumbnailTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadOrgImgTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadThumbImgTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadXMLTask;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderVerifyProjectCodeTask;

import java.util.ArrayList;
import java.util.List;

import errorhandle.SnapsAssert;

/**
 * Created by ysjeong on 2017. 3. 29..
 */

public class SnapsOrderTaskDefaultHandler extends SnapsOrderTaskBaseHandler {
    private static final String TAG = SnapsOrderTaskDefaultHandler.class.getSimpleName();

    private SnapsOrderGetProjectCodeTask getProjectCodeTask = null;
    private SnapsOrderVerifyProjectCodeTask verifyProjectCodeTask = null;
    private SnapsOrderUploadMainThumbnailTask uploadMainThumbnailTask = null;
    private SnapsOrderUploadXMLTask uploadXMLTask = null;

    private SnapsOrderTaskDefaultHandler(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);

        createAllOrderTask(attribute);
    }

    private SnapsOrderTaskDefaultHandler(SnapsOrderAttribute attribute) throws SnapsOrderException {
        super(attribute, null);

        setGetProjectCodeTask((SnapsOrderGetProjectCodeTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_PROJECT_CODE, attribute));
        setVerifyProjectCodeTask((SnapsOrderVerifyProjectCodeTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_VERIFY_PROJECT_CODE, attribute));
    }

    public static SnapsOrderTaskDefaultHandler createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        return new SnapsOrderTaskDefaultHandler(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderTaskDefaultHandler createInstanceForGetPROJCodeWithAttribute(SnapsOrderAttribute attribute) throws Exception {
        return new SnapsOrderTaskDefaultHandler(attribute);
    }

    public void finalizeInstance() throws Exception {
        super.finalizeInstance();
        if (getProjectCodeTask != null)
            getProjectCodeTask.finalizeInstance();

        if (verifyProjectCodeTask != null)
            verifyProjectCodeTask.finalizeInstance();

        if (uploadMainThumbnailTask != null)
            uploadMainThumbnailTask.finalizeInstance();

        if (uploadXMLTask != null)
            uploadXMLTask.finalizeInstance();

        setGetProjectCodeTask(null);
        setVerifyProjectCodeTask(null);
        setOrgImgUploadTask(null);
        setUploadXMLTask(null);
    }

    @Override
    protected void createAllOrderTask(SnapsOrderAttribute attribute) {
        try {
            setCreatedTasks(attribute);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(attribute.getActivity(), e);
        }
    }

    private void setCreatedTasks(SnapsOrderAttribute attribute) throws Exception {
        setGetProjectCodeTask((SnapsOrderGetProjectCodeTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_PROJECT_CODE, attribute));

        setVerifyProjectCodeTask((SnapsOrderVerifyProjectCodeTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_VERIFY_PROJECT_CODE, attribute));

        setOrgImgUploadTask((SnapsOrderUploadOrgImgTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE, attribute));

        setThumbImgUploadTask((SnapsOrderUploadThumbImgTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_THUMB_IMAGE, attribute));

        setMakePageThumbnailsTask((SnapsOrderMakePageThumbnailsTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_PAGE_THUMBNAILS, attribute));

        getMakePageThumbnailsTask().setActivityBridge(getSnapsOrderActivityBridge());

        setUploadMainThumbnailTask((SnapsOrderUploadMainThumbnailTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL, attribute));

        setUploadXMLTask((SnapsOrderUploadXMLTask) SnapsOrderTaskFactory.createSnapsOrderTask(SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML, attribute));
    }

    @Override
    public void getProjectCode(final SnapsOrderResultListener getProjectCodeListener) {
        if (!Config.isValidProjCode()) {
            performGetProjectCode(getProjectCodeListener);
        } else {
            performVerifyProjectCode(getProjectCodeListener);
        }
    }

    private void performGetProjectCode(final SnapsOrderResultListener listener) {
        try {
            getGetProjectCodeTask().getProjectCode(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object msgObj) {
                    performVerifyProjectCode(listener);
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    if (listener != null) listener.onSnapsOrderResultFailed(resultMsgObj, orderType);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (listener != null) listener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_GET_PROJECT_CODE);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performVerifyProjectCode(SnapsOrderResultListener verifyProjectCodeListener) {
        try {
            getVerifyProjectCodeTask().verifyProjectCode(verifyProjectCodeListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (verifyProjectCodeListener != null) verifyProjectCodeListener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_VERIFY_PROJECT_CODE);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performUploadOrgImages(SnapsOrderResultListener orgImgUploadListener) {
        try {
            getOrgImgUploadTask().uploadAllImageList(getSnapsOrderActivityBridge().getUploadImageList(), orgImgUploadListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (orgImgUploadListener != null) orgImgUploadListener.onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performUploadOrgImagesAtBackground(ArrayList<MyPhotoSelectImageData> imageList, SnapsImageUploadListener orgImgUploadListener) throws Exception {
        try {
            getOrgImgUploadTask().performUploadImagesAtBackground(imageList, orgImgUploadListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (orgImgUploadListener != null) orgImgUploadListener.onImageUploadFailed(null);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performUploadThumbImagesAtBackground(ArrayList<MyPhotoSelectImageData> imageList, SnapsImageUploadListener imageUploadListener) throws Exception {
        try {
            getThumbImgUploadTask().performUploadImagesAtBackground(imageList, imageUploadListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (imageUploadListener != null) imageUploadListener.onImageUploadFailed(null);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void performUploadThumbImgAtBackground(MyPhotoSelectImageData imageData, SnapsImageUploadListener imgUploadListener) throws Exception {
        try {
            getThumbImgUploadTask().performUploadImageAtBackground(imageData, imgUploadListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (imgUploadListener != null) imgUploadListener.onImageUploadFailed(null);
            SnapsAssert.assertException(getAttribute().getActivity(), e);
        }
    }

    @Override
    public void suspendBackgroundOrgImageUpload() throws Exception {
        getOrgImgUploadTask().suspendBackgroundImgUpload();
    }

    @Override
    public void suspendBackgroundThumbImageUpload() throws Exception {
        getThumbImgUploadTask().suspendBackgroundImgUpload();
    }

    @Override
    public void cancelThumbnailImgUploadExecutor() throws Exception {
        getThumbImgUploadTask().cancelImageUploadExecutor();
    }

    @Override
    public void cancelOrgImgUploadExecutor() throws Exception {
        getOrgImgUploadTask().cancelImageUploadExecutor();
    }

    @Override
    public void removeBackgroundUploadingOrgImgDataList(List<MyPhotoSelectImageData> removeList) throws Exception {
        getOrgImgUploadTask().removeBackgroundUploadingImageDataList(removeList);
    }

    @Override
    public void removeBackgroundUploadingOrgImgData(MyPhotoSelectImageData removeData) throws Exception {
        getOrgImgUploadTask().removeBackgroundUploadingImageData(removeData);
    }

    @Override
    public void removeBackgroundUploadingThumbImgData(MyPhotoSelectImageData removeData) throws Exception {
        getThumbImgUploadTask().removeBackgroundUploadingImageData(removeData);
    }

    @Override
    public void requestMakePagesThumbnailFile(SnapsOrderResultListener makeThumbnailListener) throws Exception {
        getMakePageThumbnailsTask().requestMakePageThumbnailFiles(makeThumbnailListener);
    }

    @Override
    public void requestMakeMainPageThumbnailFile(SnapsOrderResultListener makeThumbnailListener) throws Exception {
        getMakePageThumbnailsTask().requestMakeOnlyMainPageThumbnailFile(makeThumbnailListener);
    }

    @Override
    public void performUploadMainThumbnail(SnapsOrderResultListener uploadThumbnailListener) throws Exception {
        getUploadMainThumbnailTask().uploadMainThumbnail(uploadThumbnailListener);
    }

    @Override
    public void performMakeXML(SnapsOrderResultListener makeXmlListener) throws Exception {
        getUploadXMLTask().performMakeXML(makeXmlListener);
    }

    @Override
    public void performUploadXML(SnapsOrderResultListener uploadXmlListener) throws Exception {
        getUploadXMLTask().performUploadXML(uploadXmlListener);
    }

    public SnapsOrderGetProjectCodeTask getGetProjectCodeTask() {
        return getProjectCodeTask;
    }

    public void setGetProjectCodeTask(SnapsOrderGetProjectCodeTask getProjectCodeTask) {
        this.getProjectCodeTask = getProjectCodeTask;
    }

    public SnapsOrderVerifyProjectCodeTask getVerifyProjectCodeTask() {
        return verifyProjectCodeTask;
    }

    public void setVerifyProjectCodeTask(SnapsOrderVerifyProjectCodeTask verifyProjectCodeTask) {
        this.verifyProjectCodeTask = verifyProjectCodeTask;
    }

    public SnapsOrderUploadMainThumbnailTask getUploadMainThumbnailTask() {
        return uploadMainThumbnailTask;
    }

    public void setUploadMainThumbnailTask(SnapsOrderUploadMainThumbnailTask uploadMainThumbnailTask) {
        this.uploadMainThumbnailTask = uploadMainThumbnailTask;
    }

    public SnapsOrderUploadXMLTask getUploadXMLTask() {
        return uploadXMLTask;
    }

    public void setUploadXMLTask(SnapsOrderUploadXMLTask uploadXMLTask) {
        this.uploadXMLTask = uploadXMLTask;
    }
}
