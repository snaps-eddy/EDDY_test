package com.snaps.mobile.order.order_v2.util.handler;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.ICommonConfirmListener;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderUtilImp;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.task.prepare_process.SnapsOrderPrepareBaseHandler;
import com.snaps.mobile.order.order_v2.task.prepare_process.SnapsOrderPrepareHandlerDefault;
import com.snaps.mobile.order.order_v2.task.prepare_process.SnapsOrderPrepareHandlerFactory;
import com.snaps.mobile.order.order_v2.task.upload_task.handler.SnapsOrderTaskBaseHandler;
import com.snaps.mobile.order.order_v2.task.upload_task.handler.SnapsOrderTaskHandlerFactory;
import com.snaps.mobile.order.order_v2.util.SnapsPageThumbnailMakingChecker;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.common.utils.system.DateUtil;

import java.util.ArrayList;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;

import static com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants.SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE;
import static com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants.SETTING_VALUE_USE_CELLULAR_DENIED_DATE;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public abstract class SnapsOrderBaseHandler implements SnapsOrderUtilImp {
    private static final String TAG = SnapsOrderBaseHandler.class.getSimpleName();
    /**
     * ??????????????? ???????????? ???????????? ?????? ????????? ????????????.
     * ?????? ????????? ????????? ??????, ????????? -> ?????? ????????? ?????????
     * ????????? ?????? ????????? ????????? ???????????? ?????????.
     *
     * ?????? ?????? ?????? ??????????????? ??????????????? ???????????? ????????? ????????? ??????.
     * 1.??? ?????????(???????????????) ????????? ?????? : ????????? ?????????, ???????????? ?????? ???????????? ????????????.
     * 2.??? ????????? ????????? ????????? : ????????? ?????????, ???????????? ?????? ???????????? ????????????.
     * 3.?????? ?????? ?????????(????????????????????? ?????? ???????????? ?????? ???????????? ?????? ????????? ?????? ??????. ????????? ????????? ???????????? ????????? ???????????? ????????????.)
     * 4.XML ??????
     * 5.XML ?????????
     */
    protected abstract void startUploadProcess();

    /**
     * ?????? ???????????? ???????????? ????????????, ????????? ??????????????? ???????????? ?????? ????????????. (???????????? ????????? ???????????? ??????????????? ?????? ?????????.)
     */
    protected abstract void performMakeMainPageThumbnailFile();
    protected abstract void performUploadMainThumbnail();

    /**
     * ?????? ????????? ?????????
     */
    protected abstract void performUploadOrgImages();

    /**
     * ????????? ???????????? ???????????? ?????? ????????? ???????????? ????????? ?????? ??? ?????? ??????
     */
    protected abstract void onOverWaitTimeMakePageThumbnail();

    private SnapsOrderTaskBaseHandler snapsOrderTaskHandler = null;

    private SnapsOrderPrepareBaseHandler snapsOrderPrepareHandler = null;

    private SnapsOrderAttribute orderAttribute = null;

    private SnapsPageThumbnailMakingChecker pageThumbnailMakingChecker = null;    //????????? ????????? ?????? ??? ????????? ????????? ??????, ?????? ?????? ???????????? ?????? ?????? ?????? ??????.

    private SnapsOrderActivityBridge activityBridge = null;

    private SnapsOrderResultListener projectUploadResultListener = null;

    private CustomizeDialog cellularDataConfirmDialog = null;

    //????????? ????????? ????????? ?????? ????????? ???????????? ???????????? ??? ???????????? ????????? ???????????? ??????????????? ?????? ????????? ???????????????.
    //????????? ?????? ?????? ?????? ????????? ????????? ??????????????? ??? ?????? ????????? ???????????? ??? ????????? ????????????.
    private boolean isLockAfterMakeThumbnail = false;
    private boolean isUploadingXMLFiles = false;

    private Activity activity = null;

    SnapsOrderBaseHandler(@NonNull Activity activity, @NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        setActivity(activity);
        setActivityBridge(snapsOrderActivityBridge);

        initialize();
    }

    private void initialize() throws Exception {
        setOrderAttribute(getActivityBridge().getSnapsOrderAttribute());

        snapsOrderTaskHandler =  SnapsOrderTaskHandlerFactory.createOrderTaskHandlerWithAttribute(getOrderAttribute(), getActivityBridge());

        snapsOrderPrepareHandler = SnapsOrderPrepareHandlerFactory.createOrderPrepareHandler(getOrderAttribute(), getActivityBridge());

        setLockAfterMakeThumbnail(false);
        setUploadingXMLFiles(false);

        unLockAllImageUploadSyncObject();
    }

    public void finalizeSnapsOrderManager() {
        try {
            if (snapsOrderTaskHandler != null)
                snapsOrderTaskHandler.finalizeInstance();

            if (snapsOrderPrepareHandler != null)
                snapsOrderPrepareHandler.finalizeInstance();

            if (pageThumbnailMakingChecker != null)
                pageThumbnailMakingChecker.finalizeInstance();

            orderAttribute = null;
            activityBridge = null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(activity, e);
        }
    }

    public void performSaveToBasket(@NonNull DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
        snapsOrderPrepareHandler.performSaveToBasket(dialogInputNameClickListener);
    }

    /**
     * ????????? ?????? ?????? ????????? ????????? ?????????, ????????? ???????????? ?????? ?????? ?????? ??????.
     */
    private void unLockAllImageUploadSyncObject() throws Exception {
        if (getActivityBridge() == null) return;
        ArrayList<MyPhotoSelectImageData> uploadImageList = getActivityBridge().getUploadImageList();
        if (uploadImageList == null) return;
        for (MyPhotoSelectImageData imageData : uploadImageList) {
            if (imageData != null) imageData.finishUploadSyncLock();
        }
    }

    /**
     * ????????????????????? ?????? ???????????? ?????????.
     * {@link Exception : SnapsOrderNotExistProjectCodeException ??? ????????? ??????, tryUploadOrgImgOnBackgroundWithGetProjectCode??? ???????????? ??????.}
     *  (?????? ????????? ?????? ????????? ??????, ???????????? ????????? ?????? ?????????????????? ????????????, ???????????? ?????? ???????????? ???????????? ???????????? ???????????? ????????? ?????? ?????????.)
     */
    @Override
    public void performUploadOrgImgAtBackground(ArrayList<MyPhotoSelectImageData> imageList, SnapsImageUploadListener listener) throws Exception {
        SnapsOrderConstants.eSnapsOrderUploadResultMsg prepareMsg = getResultForPrepareToImageUploadOnBackground();
        if (prepareMsg != SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_READIED_BACKGROUND_IMG_UPLOAD) {
            if (listener != null) listener.onImageUploadFailed(SnapsImageUploadUtil.createImageUploadResultMsgData(null, prepareMsg));
            return;
        }

        snapsOrderTaskHandler.performUploadOrgImagesAtBackground(imageList, listener);
    }

    private SnapsOrderConstants.eSnapsOrderUploadResultMsg getResultForPrepareToImageUploadOnBackground() throws Exception {
        //???????????? ?????? ??????
        if (!SnapsImageUploadUtil.isAllowBackgroundImgUploadNetworkState(getOrderAttribute().getActivity())) {
            return SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE;
        } else if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            if (!SnapsDiaryDataManager.isExistDiarySeqNo()) {
                return SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_EXIST_DIARY_SEQ;
            }
        } else {
            if (!Config.isValidProjCodeWithStringCode(Config.getPROJ_CODE())) {
                return SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_EXIST_PROJECT_CODE;
            }
        }
        return SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_READIED_BACKGROUND_IMG_UPLOAD;
    }

    @Override
    public void performUploadThumbImgListAtBackground(ArrayList<MyPhotoSelectImageData> imageList, SnapsImageUploadListener listener) throws Exception {
        SnapsOrderConstants.eSnapsOrderUploadResultMsg prepareMsg = getResultForPrepareToImageUploadOnBackground();
        if (prepareMsg != SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_READIED_BACKGROUND_IMG_UPLOAD) {
            if (listener != null) listener.onImageUploadFailed(SnapsImageUploadUtil.createImageUploadResultMsgData(null, prepareMsg));
            return;
        }

        snapsOrderTaskHandler.performUploadThumbImagesAtBackground(imageList, listener);
    }

    public boolean isContainedUploadingImageData(MyPhotoSelectImageData imageData) throws Exception {
        if (snapsOrderTaskHandler == null) return false;
        return snapsOrderTaskHandler.isContainedUploadingImageData(imageData);
    }

    @Override
    public void performUploadThumbImgAtBackground(MyPhotoSelectImageData imageData, SnapsImageUploadListener listener) throws Exception {
        SnapsOrderConstants.eSnapsOrderUploadResultMsg prepareMsg = getResultForPrepareToImageUploadOnBackground();
        if (prepareMsg != SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_READIED_BACKGROUND_IMG_UPLOAD) {
            if (listener != null) listener.onImageUploadFailed(SnapsImageUploadUtil.createImageUploadResultMsgData(null, prepareMsg));
            return;
        }

        snapsOrderTaskHandler.performUploadThumbImgAtBackground(imageData, listener);
    }

    @Override
    public void suspendBackgroundImgUpload() throws Exception {
        snapsOrderTaskHandler.suspendBackgroundOrgImageUpload();

        snapsOrderTaskHandler.suspendBackgroundThumbImageUpload();
    }

    @Override
    public void cancelThumbnailImgUploadExecutor() throws Exception {
        snapsOrderTaskHandler.cancelThumbnailImgUploadExecutor();
    }

    @Override
    public void cancelOrgImgUploadExecutor() throws Exception {
        snapsOrderTaskHandler.cancelOrgImgUploadExecutor();
    }

    /**
     * ????????? ???????????? ??????, ????????????????????? ????????? ?????? ????????? ?????? ???????????? ????????????.
     */
    @Override
    public void removeBackgroundUploadingOrgImageDataList(List<MyPhotoSelectImageData> removeList) throws Exception {
        snapsOrderTaskHandler.removeBackgroundUploadingOrgImgDataList(removeList);
    }

    @Override
    public void removeBackgroundUploadingOrgImageData(MyPhotoSelectImageData removeData) throws Exception {
        snapsOrderTaskHandler.removeBackgroundUploadingOrgImgData(removeData);
    }

    @Override
    public void removeBackgroundUploadingThumbImageData(MyPhotoSelectImageData removeData) throws Exception {
        snapsOrderTaskHandler.removeBackgroundUploadingThumbImgData(removeData);
    }

    /**
     * ???????????? ?????? ??????
     */
    @Override
    public void getProjectCode(SnapsOrderResultListener listener) throws Exception {
        snapsOrderTaskHandler.getProjectCode(listener);
    }

    /**
     * ??????????????? ???????????? ?????? ???????????? ????????????.
     */
    @Override
    public void performInspectRequiredOptions(SnapsOrderResultListener inspectListener) throws Exception {
        snapsOrderPrepareHandler.setProductYN();

        int orderPrepareCheckResultCode = snapsOrderPrepareHandler.performInspectOrderOptionAndGetResultCode();
        if (orderPrepareCheckResultCode == SnapsOrderPrepareHandlerDefault.ORDER_PREPARE_INSPECT_RESULT_OK) {
            inspectListener.onSnapsOrderResultSucceed(null);
        } else {
            inspectListener.onSnapsOrderResultFailed(orderPrepareCheckResultCode, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_INSPECT_REQUIRED_TO_ORDER);
        }
    }

    /**
     * performInspectRequiredOrder??? ?????? ?????? ???, ??????????????? ????????? ????????? ?????? ????????? ????????????.
     */
    @Override
    public void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener listener) throws Exception {
        snapsOrderPrepareHandler.showSaveToBasketAlert(alertAttribute, listener);
    }

    @Override
    public void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener confirmClickListener) throws Exception {
        snapsOrderPrepareHandler.showCompleteUploadPopup(confirmClickListener);
    }

    @Override
    public void prepareProjectUpload(@NonNull SnapsOrderResultListener orderResultListener) throws Exception {

        setProjectUploadResultListener(orderResultListener);

        //??????????????? ??????????????? ???????????? ?????? ????????? ???????????? ??????????????? ????????????.
        snapsOrderPrepareHandler.setOrderBaseInfo(new SnapsOrderResultListener() {
            @Override
            public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_SET_BASE_OPTIONS);
                startUploadProcess();
            }

            @Override
            public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, orderType);
            }
        });
    }

    void performMakeThumbnailFile() {
        try {
            if (shouldReDrawTextControls()) {
                performMakePageThumbnailFiles();
                return;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            performMakeMainPageThumbnailFile();
            return;
        }

        performMakeMainPageThumbnailFile();  //?????? ???????????? ??????.
    }

    //????????? ???????????? ??? ?????? ????????? ??? ????????? ??? ???????????? ???????????? ??????, ?????? ????????? ?????? ?????? ????????????.
    private boolean shouldReDrawTextControls() throws Exception {
        if (!orderAttribute.isEditMode()) return false; //???????????? ?????? ????????? ?????? ???????????? ????????? ????????? ???????????? ?????????

        if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            ArrayList<SnapsPage> pageList = orderAttribute.getPageList();
            for (SnapsPage page : pageList) {
                if (!page.getTextControlList().isEmpty()) return true;
            }
        }

        return false;
    }

    void performMakeXML() {
        Dlog.d("performMakeXML() start make xml");
        if (isUploadingXMLFiles()) return;
        setUploadingXMLFiles(true);

        try {
            snapsOrderTaskHandler.performMakeXML(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performMakeXML()-onSnapsOrderResultSucceed()");
                    SnapsLogger.appendOrderLog("saveXML make point A");
                    SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_HANDLE_XML);
                    SnapsLogger.appendOrderLog("saveXML make point B");
                    performUploadXML();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performMakeXML()-onSnapsOrderResultFailed()");
                    getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_XML);
                    setUploadingXMLFiles(false);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (getProjectUploadResultListener() != null) getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_XML);
            SnapsAssert.assertException(activity, e);
            setUploadingXMLFiles(false);
        }
    }

    private void performUploadXML() {
        Dlog.d("performUploadXML() start upload xml");
        try {
            snapsOrderTaskHandler.performUploadXML(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performUploadXML()-onSnapsOrderResultSucceed()");
                    getProjectUploadResultListener().onSnapsOrderResultSucceed(resultMsgObj);
                    setUploadingXMLFiles(false);
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performUploadXML()-onSnapsOrderResultFailed()");
                    getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
                    setUploadingXMLFiles(false);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (getProjectUploadResultListener() != null) getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
            SnapsAssert.assertException(activity, e);
            setUploadingXMLFiles(false);
        }
    }

    private void performMakePageThumbnailFiles() {
        Dlog.d("performMakePageThumbnailFiles() start make MakePageThumbnailFiles");

        try {
            setLockAfterMakeThumbnail(false);

            snapsOrderTaskHandler.requestMakePagesThumbnailFile(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performMakePageThumbnailFiles()-onSnapsOrderResultSucceed()");
                    performMakeMainPageThumbnailFile();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performMakePageThumbnailFiles()-onSnapsOrderResultFailed()");
                    performMakeMainPageThumbnailFile();
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(activity, e);
            performMakeMainPageThumbnailFile();
        }
    }

    void suspendPageThumbnailMakeCheck() {
        if (pageThumbnailMakingChecker != null) {
            pageThumbnailMakingChecker.setCompletedMakingThumbnail(true);
        }
    }

    boolean isOverWaitTimeForPageThumbnailMake() {
        return pageThumbnailMakingChecker != null && pageThumbnailMakingChecker.isOverWaitTime();
    }

    void startMainPageThumbnailMakingCheck() {
        pageThumbnailMakingChecker = SnapsPageThumbnailMakingChecker.newInstance();
        pageThumbnailMakingChecker.startMainPageThumbnailMakingCheck(new SnapsPageThumbnailMakingChecker.SnapsPageThumbnailMakeErrListener() {
            @Override
            public void onOverWaitTime() {
                onOverWaitTimeMakePageThumbnail();
            }
        });
    }

    public void showConfirmBackgroundUploadByCellularData(final Activity activity, final ICommonConfirmListener confirmListener) {
        try {
            SnapsImageUploadUtil.showConfirmBackgroundUploadByCellularDataCheckDenied(activity, cellularDataConfirmDialog, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    if (clickedOk == ICustomDialogListener.OK) {
                        Setting.set(activity, SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE, DateUtil.getTodayDate());
                        if (confirmListener != null) confirmListener.onConfirmed();
                    } else {
                        Setting.set(activity, SETTING_VALUE_USE_CELLULAR_DENIED_DATE, DateUtil.getTodayDate());
                    }
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    SnapsOrderResultListener getProjectUploadResultListener() {
        return projectUploadResultListener;
    }

    private void setProjectUploadResultListener(SnapsOrderResultListener projectUploadResultListener) {
        this.projectUploadResultListener = projectUploadResultListener;
    }

    boolean isLockAfterMakeThumbnail() {
        return isLockAfterMakeThumbnail;
    }

    void setLockAfterMakeThumbnail(boolean lockAfterMakeThumbnail) {
        isLockAfterMakeThumbnail = lockAfterMakeThumbnail;
    }

    private boolean isUploadingXMLFiles() {
        return isUploadingXMLFiles;
    }

    private void setUploadingXMLFiles(boolean uploadingXMLFiles) {
        isUploadingXMLFiles = uploadingXMLFiles;
    }

    SnapsOrderTaskBaseHandler getSnapsOrderTaskHandler() {
        return snapsOrderTaskHandler;
    }

    private SnapsOrderAttribute getOrderAttribute() {
        return orderAttribute;
    }

    private void setOrderAttribute(SnapsOrderAttribute orderAttribute) {
        this.orderAttribute = orderAttribute;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private SnapsOrderActivityBridge getActivityBridge() {
        return activityBridge;
    }

    private void setActivityBridge(SnapsOrderActivityBridge activityBridge) {
        this.activityBridge = activityBridge;
    }
}
