package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadImageDataParams;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsBaseImgBackgroundUploadExecutor;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public abstract class SnapsOrderUploadBaseImgTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderUploadBaseImgTask.class.getSimpleName();

    SnapsBaseImgBackgroundUploadExecutor backgroundImgUploadExecutor = null;

    private SnapsImageUploadResultListener imageUploadResultListener = null;

    SnapsOrderUploadBaseImgTask(SnapsOrderAttribute attribute) {
        super(attribute);

        initBackgroundUploadExecutor();
    }

    protected abstract void initBackgroundUploadExecutor();

    protected abstract void prepareUploadAllImageList(ArrayList<MyPhotoSelectImageData> uploadList);

    protected abstract void startImageUploadByAsync(MyPhotoSelectImageData uploadTargetImageData, SnapsImageUploadResultListener currentImgUploadListener) throws Exception;

    protected abstract SnapsOrderConstants.eSnapsOrderType getOrderType();

    protected abstract void handleOnImageUploadSucceed(SnapsImageUploadResultData uploadResultData);

    protected abstract void handleOnImageUploadFailed(SnapsImageUploadResultData uploadResultData);

    protected abstract boolean isUploadedStateOnImageData(MyPhotoSelectImageData orgData);

    protected abstract void handleOnAllImageUploaded(SnapsImageUploadResultData uploadResultData, SnapsOrderResultListener orgImgUploadListener) throws Exception;

    protected abstract void handleOnTryUploadNextImageData(SnapsImageUploadResultData uploadResultData, SnapsUploadImageDataParams imageDataParams) throws Exception;

    @Override
    public void finalizeInstance() throws Exception {
        super.finalizeInstance();

        forceShutdownExecutor();

        imageUploadResultListener = null;
    }

    public void forceShutdownExecutor() {
        if (backgroundImgUploadExecutor != null) {
            backgroundImgUploadExecutor.suspendUpload();
            backgroundImgUploadExecutor.shutdownNow();
        }
    }

    /**
     *   각 사진 1장이 하나의 Thread로 처리 되며, ThreadPoolExcutor가 전체 리스트를 관리한다.
     */
    public void performUploadImagesAtBackground(ArrayList<MyPhotoSelectImageData> imageList, final SnapsImageUploadListener listener) throws Exception {
        if (!isUploadingOnBackground()) {
            setBackgroundImageUploadExecutorListener(listener);
        }

        startUploadImageListOnBackground(imageList);
    }

    public boolean isContainedUploadingImageData(MyPhotoSelectImageData imageData) throws Exception {
        if (backgroundImgUploadExecutor != null) {
            return backgroundImgUploadExecutor.isContainedUploadingImageData(imageData);
        }
        return false;
    }

    public void performUploadImageAtBackground(MyPhotoSelectImageData imageData, final SnapsImageUploadListener listener) throws Exception {
        SnapsImageUploadUtil.startThumbImgUploadOnNewThread(imageData, listener);
    }

    public void removeBackgroundUploadingImageDataList(List<MyPhotoSelectImageData> removeList) throws Exception {
        backgroundImgUploadExecutor.removeUploadImgDataList(removeList);
    }

    public void removeBackgroundUploadingImageData(MyPhotoSelectImageData removeImageData) throws Exception {
        backgroundImgUploadExecutor.removeUploadImgData(removeImageData);
    }

    public void setBackgroundImageUploadExecutorListener(SnapsImageUploadListener listener) {
        backgroundImgUploadExecutor.setImageUploadListener(listener);
    }

    private void startUploadImageListOnBackground(ArrayList<MyPhotoSelectImageData> imageList) throws Exception {
        backgroundImgUploadExecutor.startUploadImages(imageList);
    }

    public void uploadAllImageList(final ArrayList<MyPhotoSelectImageData> uploadList, final SnapsOrderResultListener orgImgUploadListener) throws Exception {
        prepareUploadAllImageList(uploadList);

        SnapsUploadImageDataParams uploadOrgImgDataParams = SnapsUploadImageDataParams.createInstanceWithImageList(uploadList);
        recursiveUploadImageData(uploadOrgImgDataParams, orgImgUploadListener);
    }

    private void recursiveUploadImageData(SnapsUploadImageDataParams uploadOrgImgDataParams, SnapsOrderResultListener orgImgUploadListener) throws Exception {
        int uploadStartIndex = getUploadIndexIfInfiniteThrowException(uploadOrgImgDataParams); //무한루프 돌지 않도록 업로드하고 나면, addIndex를 해주어야 한다.
        if (!checkValidUploadState(uploadOrgImgDataParams, orgImgUploadListener)) {
            return;
        }

        ArrayList<MyPhotoSelectImageData> uploadList = (ArrayList<MyPhotoSelectImageData>) uploadOrgImgDataParams.getImageDataList();
        MyPhotoSelectImageData uploadTargetImageData = uploadList.get(uploadStartIndex);

        SnapsImageUploadResultListener currentOrgImgUploadListener = getOrgImgUploadResultListener(uploadOrgImgDataParams, orgImgUploadListener);

        startImageUploadByAsync(uploadTargetImageData, currentOrgImgUploadListener);
    }

    private int getUploadIndexIfInfiniteThrowException(SnapsUploadImageDataParams uploadOrgImgDataParams) throws Exception {
        int uploadStartIndex = uploadOrgImgDataParams.getIndexOfUploadTargetImgData();
        Dlog.d("getUploadIndexIfInfiniteThrowException() Recursive img upload index : " + uploadStartIndex + ", prev : " + uploadOrgImgDataParams.getIndexOfPrevUploadedImgData() + " (total : "
                + (uploadOrgImgDataParams.getImageDataList() != null ? uploadOrgImgDataParams.getImageDataList().size() : "?") + ")" );

        if (uploadStartIndex == uploadOrgImgDataParams.getIndexOfPrevUploadedImgData()) throw new SnapsOrderException("org image uploading was repeat infinite.");
        uploadOrgImgDataParams.setIndexOfPrevUploadedImgData(uploadStartIndex);
        return uploadStartIndex;
    }

    public void suspendBackgroundImgUpload() throws Exception {
        backgroundImgUploadExecutor.suspendUpload();
    }

    private SnapsImageUploadResultListener getOrgImgUploadResultListener(SnapsUploadImageDataParams uploadOrgImgDataParams, SnapsOrderResultListener listener) {
        if (imageUploadResultListener == null) {
            imageUploadResultListener = createOrgImgUploadListener(listener);
        }

        imageUploadResultListener.setImageDataParams(uploadOrgImgDataParams);

        return imageUploadResultListener;
    }

    private SnapsImageUploadResultListener createOrgImgUploadListener(SnapsOrderResultListener listener) {
        SnapsImageUploadResultListener handler = new SnapsImageUploadResultListener();
        handler.setListener(listener);
        return handler;
    }

    public boolean isUploadingOnBackground() {
        return backgroundImgUploadExecutor != null && backgroundImgUploadExecutor.isUploading();
    }

    private boolean isAllowNetworkStateForImageUpload() {
        if (!CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) return false;
        else if (!SnapsOrderManager.isUploadingProject() && !SnapsImageUploadUtil.isAllowBackgroundImgUploadNetworkState(getActivity())) return false; //프로젝트 저장 중이 아닌 백그라운드에서 올릴때...
        return true;
    }
    
    private boolean checkValidUploadState(SnapsUploadImageDataParams uploadOrgImgDataParams, SnapsOrderResultListener orgImgUploadListener) throws Exception {
        if (!isAllowNetworkStateForImageUpload()) {
            orgImgUploadListener.onSnapsOrderResultFailed(SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE, getOrderType());
            return false;
        }

        int uploadIndex = uploadOrgImgDataParams.getIndexOfUploadTargetImgData();
        ArrayList<MyPhotoSelectImageData> uploadList = (ArrayList<MyPhotoSelectImageData>) uploadOrgImgDataParams.getImageDataList();

        if (uploadIndex < 0) {
            orgImgUploadListener.onSnapsOrderResultFailed(SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_OVER_ARR_IDX, getOrderType());
            return false;
        }

        if (uploadList == null || uploadList.size() == 0) { //이미지를 선택 하지 않은 경우
            orgImgUploadListener.onSnapsOrderResultSucceed(null);
            return false;
        }

        if (uploadIndex >= uploadList.size()) { //이미 다 올렸다고 판단 됨.
            handleOnAllImageUploaded(null, orgImgUploadListener);
            return false;
        }

        return true;
    }

    boolean isUploadErrorImageData(SnapsImageUploadResultData uploadResultData) {
        return uploadResultData != null
                && ( uploadResultData.getUploadResultMsg() == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_RETURN_VALUE_ERR
                || uploadResultData.getUploadResultMsg() == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_THUMBNAIL_RETURN_VALUE_ERR
                || uploadResultData.getUploadResultMsg() == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_EXIST_IMAGE);
    }

    public class SnapsImageUploadResultListener implements SnapsImageUploadListener {
        private SnapsUploadImageDataParams imageDataParams;
        private SnapsOrderResultListener listener;

        private SnapsUploadImageDataParams getImageDataParams() {
            return imageDataParams;
        }

        private void setImageDataParams(SnapsUploadImageDataParams imageDataParams) {
            this.imageDataParams = imageDataParams;
        }

        public void setListener(SnapsOrderResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void onImageUploadStart() {}

        @Override
        public void onImageUploadAllBackgroundTaskFinished() {}

        @Override
        public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
            handleOnImageUploadSucceed(uploadResultData);

            tryUploadNextImageData(uploadResultData);
        }

        @Override
        public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
            if (isNetworkError(uploadResultData)) {
                listener.onSnapsOrderResultFailed(uploadResultData, getOrderType());
                return;
            }

            handleOnImageUploadFailed(uploadResultData);

            tryUploadNextImageData(uploadResultData);
        }

        private boolean isNetworkError(SnapsImageUploadResultData uploadResultData) {
            return uploadResultData != null && uploadResultData.getUploadResultMsg() == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE;
        }

        private int getUploadIndex() {
            return getImageDataParams() != null ? getImageDataParams().getIndexOfUploadTargetImgData() : -1;
        }

        private ArrayList<MyPhotoSelectImageData> getUploadImageList() {
            return getImageDataParams() != null ? (ArrayList<MyPhotoSelectImageData>) getImageDataParams().getImageDataList() : null;
        }

        private boolean isAllImageUploaded() {
            return getUploadIndex() >= (getUploadImageList() != null ? getUploadImageList().size() : 0);
        }

        private int findNextIndexOfImageDataForUpload() throws Exception {
            ArrayList<MyPhotoSelectImageData> uploadList = (ArrayList<MyPhotoSelectImageData>) getImageDataParams().getImageDataList();

            int nextIndex = getImageDataParams().getIndexOfUploadTargetImgData() + 1;
            while (nextIndex < uploadList.size()) {
                MyPhotoSelectImageData uploadTargetImageData = uploadList.get(nextIndex);
                if (!isUploadedStateOnImageData(uploadTargetImageData))
                    break;
                nextIndex++;
            }
            return nextIndex;
        }

        private void setNextIndexOfImageDataForUpload() throws Exception {
            int nextUploadImgIndex = findNextIndexOfImageDataForUpload();
            getImageDataParams().setIndexOfUploadTargetImgData(nextUploadImgIndex);
        }

        private void tryUploadNextImageData(SnapsImageUploadResultData uploadResultData) {
            try {
                handleOnTryUploadNextImageData(uploadResultData, getImageDataParams());

//                Logg.y("# try upload next image data.");
                setNextIndexOfImageDataForUpload();

                if (isAllImageUploaded()) {
                    Dlog.d("tryUploadNextImageData() is last image data.");
                    handleOnAllImageUploaded(uploadResultData, listener);
                } else {
                    recursiveUploadImageData(getImageDataParams(), listener);
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsAssert.assertException(getActivity(), e);
                listener.onSnapsOrderResultFailed(uploadResultData, getOrderType());
            }
        }
    }

    public void cancelImageUploadExecutor() throws Exception {
        if (backgroundImgUploadExecutor != null) {
            backgroundImgUploadExecutor.suspendUpload();

            BlockingQueue<Runnable> blockingQueue = backgroundImgUploadExecutor.getQueue();
            if (blockingQueue != null) blockingQueue.clear();
        }
    }
}
