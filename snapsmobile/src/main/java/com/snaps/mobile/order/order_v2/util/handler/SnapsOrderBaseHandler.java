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
     * 본격적으로 프로젝트 업로드를 하는 시점에 호출된다.
     * 일반 상품은 썸네일 생성, 업로드 -> 원본 이미지 업로드
     * 일기는 원본 사진을 올리고 썸네일을 올린다.
     *
     * 일반 상품 군의 프로젝트를 장바구니에 저장하는 순서는 아래와 같다.
     * 1.첫 페이지(메인페이지) 썸네일 생성 : 필수가 아니니, 실패하면 원본 업로드로 넘어간다.
     * 2.첫 페이지 썸네일 업로드 : 필수가 아니니, 실패하면 원본 업로드로 넘어간다.
     * 3.원본 사진 업로드(백그라운드에서 동시 진행되며 이미 업로드가 완료 되었을 수도 있다. 업로드 실패한 이미지가 있다면 팝업으로 노출한다.)
     * 4.XML 생성
     * 5.XML 업로드
     */
    protected abstract void startUploadProcess();

    /**
     * 메인 썸네일을 생성하는 부분인데, 일기를 제외하고는 실패해도 그냥 넘어간다. (웹팀에서 디폴트 이미지를 보여주기로 협의 되었다.)
     */
    protected abstract void performMakeMainPageThumbnailFile();
    protected abstract void performUploadMainThumbnail();

    /**
     * 원본 이미지 업로드
     */
    protected abstract void performUploadOrgImages();

    /**
     * 페이지 썸네일을 만들려고 시도 했는데 오래동안 반응이 없을 때 호출 된다
     */
    protected abstract void onOverWaitTimeMakePageThumbnail();

    private SnapsOrderTaskBaseHandler snapsOrderTaskHandler = null;

    private SnapsOrderPrepareBaseHandler snapsOrderPrepareHandler = null;

    private SnapsOrderAttribute orderAttribute = null;

    private SnapsPageThumbnailMakingChecker pageThumbnailMakingChecker = null;    //페이지 썸네일 생성 중 오류가 발생할 경우, 그냥 다음 스텝으로 건너 뛰기 위해 추가.

    private SnapsOrderActivityBridge activityBridge = null;

    private SnapsOrderResultListener projectUploadResultListener = null;

    private CustomizeDialog cellularDataConfirmDialog = null;

    //업로드 로그를 추적해 보니 업로드 중임에도 불구하고 또 호출되는 해괴한 케이스가 발견되어서 예외 처리를 추가하였다.
    //오류를 틀어 막기 위한 더러운 코드니 원초적으로 왜 그런 현상이 발생하는 지 파악이 필요하다.
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
     * 업로드 락이 걸린 객체가 있을수 있으니, 초기화 과정에서 모두 해제 처리 한다.
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
     * 백그라운드에서 원본 이미지를 올린다.
     * {@link Exception : SnapsOrderNotExistProjectCodeException 가 발생할 경우, tryUploadOrgImgOnBackgroundWithGetProjectCode를 호출해야 한다.}
     *  (편집 화면에 처음 진입할 때는, 프로젝트 번호를 따고 원본이미지를 올리지만, 이후에는 해당 메서드만 호출해야 중복으로 프로젝트 번호를 따지 않는다.)
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
        //네트워크 상태 체크
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
     * 사진을 삭제했을 경우, 백그라운드에서 업로드 하지 않도록 작업 목록에서 삭제한다.
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
     * 프로젝트 번호 발급
     */
    @Override
    public void getProjectCode(SnapsOrderResultListener listener) throws Exception {
        snapsOrderTaskHandler.getProjectCode(listener);
    }

    /**
     * 장바구니에 저장하기 위한 조건들을 검사한다.
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
     * performInspectRequiredOrder를 통과 했을 때, 장바구니에 저장할 것인지 묻는 알럿을 생성한다.
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

        //본격적으로 장바구니에 저장하기 전에 필요한 정보들을 셋팅해주고 넘어간다.
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

        performMakeMainPageThumbnailFile();  //메인 썸네일만 딴다.
    }

    //페이지 썸네일을 다 따는 이유는 재 저장할 때 텍스트가 그려지지 않아, 영역 계산이 되지 않기 때문이다.
    private boolean shouldReDrawTextControls() throws Exception {
        if (!orderAttribute.isEditMode()) return false; //재편집이 아닌 경우는 이미 텍스트가 한번은 화면에 그려졌을 것이다

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
