package com.snaps.mobile.order.order_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.NonNull;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.ICommonConfirmListener;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.order.ISnapsOrderStateListener;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderException;
import com.snaps.mobile.order.order_v2.exceptions.SnapsOrderInitializeException;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderFileCreateExceptionCreator;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsOrderUnkownHostExceptionCreator;
import com.snaps.mobile.order.order_v2.interfacies.NetworkStateChangeListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadStateListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.util.NetworkStateChangeReceiver;
import com.snaps.mobile.order.order_v2.util.handler.SnapsOrderBaseHandler;
import com.snaps.mobile.order.order_v2.util.handler.SnapsOrderHandlerFactory;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.order.order_v2.util.thumb_image_upload.SnapsThumbnailMakeExecutor;
import com.snaps.mobile.utils.network.ip.SnapsIPManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_UPLOAD_ORG_IMAGES;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_UPLOAD_THUMB_IMAGES;

/**
 * Created by ysjeong on 2017. 4. 5..
 */

public class SnapsOrderManager implements NetworkStateChangeListener, ISnapsHandler {
    private static final String TAG = SnapsOrderManager.class.getSimpleName();

    private static volatile SnapsOrderManager gInstance = null;

    private SnapsOrderBaseHandler snapsOrderHandler = null;

    private SnapsOrderActivityBridge snapsOrderActivityBridge = null;

    private SnapsImageUploadStateListener imageUploadStateListener = null;

    private NetworkStateChangeReceiver networkChangeReceiver = null;

    private SnapsOrderState snapsOrderState = null;

    private SnapsHandler snapsHandler = null;

    private boolean isInitialized = false;

    private boolean isRegisterNetworkChangeReceiver = false;

    private boolean isBlockMultipleUploadError = false;

    private boolean isEditorActivityOnResumeLock = false; //OnResume 탈 때 네트워크 리시버가 동작하면서 쓸데없이 업로드가 진행되서 막는다

    private SnapsThumbnailMakeExecutor snapsThumbnailMakeExecutor = null;

    private SnapsOrderManager() {
        //TODO  굳이 이런 코드가 필요한가~?
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            SnapsAssert.assertTrue(false);
//            return;
//        }

        this.snapsHandler = new SnapsHandler(this);
        this.snapsThumbnailMakeExecutor = new SnapsThumbnailMakeExecutor();
        this.setInitialized(false);
    }

    public static SnapsOrderManager getInstance() {
        if (gInstance == null)
            createInstance();

        return gInstance;
    }

    public static void createInstance() {
        if (gInstance == null) {
            synchronized (SnapsOrderManager.class) {
                if (gInstance == null) {
                    gInstance = new SnapsOrderManager();
                }
            }
        }
    }

    /**
     * 액비비티가 종료될 때 꼭 호출해 줄 것.
     */
    public static void finalizeInstance() {
        try {
            releaseAllInstances();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void handleUploadImageCommonMessage(Message msg) {
        if (msg == null) return;
        switch (msg.what) {
            case HANDLER_MSG_UPLOAD_THUMB_IMAGES:
                Object msgObj = msg.obj;
                if (msgObj != null && msgObj instanceof MyPhotoSelectImageData)
                    SnapsOrderManager.uploadThumbImgOnBackground((MyPhotoSelectImageData) msgObj);
                else
                    SnapsOrderManager.uploadThumbImgListOnBackground();
                break;
            case HANDLER_MSG_UPLOAD_ORG_IMAGES:
                SnapsOrderManager.uploadOrgImgOnBackground();
                break;
        }
    }

    public static void unRegisterNetworkChangeReceiver() {
        Activity activity = getOrderActivity();
        if (activity != null) {
            if (gInstance.networkChangeReceiver != null && gInstance.isRegisterNetworkChangeReceiver()) {
                try {
                    activity.unregisterReceiver(gInstance.networkChangeReceiver);
                    gInstance.setRegisterNetworkChangeReceiver(false);
                } catch (IllegalArgumentException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    public static void registerNetworkChangeReceiverOnResume() {
        Activity activity = getOrderActivity();
        SnapsOrderManager orderManager = getInstance();
        if (orderManager.networkChangeReceiver == null || activity == null || activity.isFinishing())
            return;
        activity.registerReceiver(orderManager.networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        gInstance.setRegisterNetworkChangeReceiver(true);
    }

    private static void releaseAllInstances() throws Exception {
        if (gInstance == null) return;

        gInstance.suspendBackgroundImgUpload();

        if (gInstance.snapsOrderHandler != null) {
            gInstance.snapsOrderHandler.finalizeSnapsOrderManager();
            gInstance.snapsOrderHandler = null;
        }

        unRegisterNetworkChangeReceiver();

        gInstance.networkChangeReceiver = null;

        gInstance.snapsOrderActivityBridge = null;

        if (gInstance.baseBackgroundOrgImageUploadListener != null) {
            gInstance.baseBackgroundOrgImageUploadListener = null;
        }

        if (gInstance.baseBackgroundThumbImageUploadListener != null) {
            gInstance.baseBackgroundThumbImageUploadListener = null;
        }

        if (gInstance.snapsHandler != null) {
            gInstance.snapsHandler = null;
        }

        getInstance().setInitialized(false);

        gInstance = null;
    }

    /**
     * 초기화 하지 않고서는 아무 기능도 사용 할 수 없다.
     */
    public static void initialize(@NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        SnapsOrderManager snapsOrderManager = getInstance();
        if (snapsOrderManager.isInitialized()) return;

        snapsOrderManager.setSnapsOrderActivityBridge(snapsOrderActivityBridge);

        snapsOrderManager.initSnapsOrderHandler();

        snapsOrderManager.initSnapsOrderState();

        snapsOrderManager.setInitialized(true);
    }

    public static void setImageUploadStateListener(SnapsImageUploadStateListener imageUploadStateListener) {
        SnapsOrderManager snapsOrderManager = getInstance();
        if (!snapsOrderManager.isInitialized()) return;
        snapsOrderManager.imageUploadStateListener = imageUploadStateListener;
    }

    /**
     * 백그라운드에서 이미지 업로드를 하기 위해 네트워크 상태를 감지 한다.
     */
    public static void startSenseBackgroundImageUploadNetworkState() {
        SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");
            snapsOrderManager.registerNetworkStateChangeHandler();
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    /**
     * 장바구니에 저장하기 버튼을 클릭했을 때 호출 해 준다.
     */
    public static void performSaveToBasket(@NonNull final ISnapsOrderStateListener snapsOrderStateListener) {
        final SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            //왜 이런 증상이 나오는 지 모르겠는데, 동시에 2번 업로드가 진행 되서 업로드 오류가 발생하는 경우가 종종 발생하여, 블락 처리 추가 함
            if (SnapsOrderManager.isBlockMultipleUploadError()) return;
            SnapsOrderManager.setBlockMultipleUploadError(true);

            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            orderHandler.performSaveToBasket(new DialogInputNameFragment.IDialogInputNameClickListener() {
                @Override
                public void onClick(boolean isOk) {
                    if (isOk) {
                        snapsOrderManager.setSnapsOrderStateListener(snapsOrderStateListener);
                        snapsOrderManager.startInspectRequiredOptionsWithProgressView();
                    } else {
                        SnapsOrderManager.setBlockMultipleUploadError(false);
                        snapsOrderStateListener.onOrderStateChanged(ISnapsOrderStateListener.ORDER_STATE_CANCEL);

                        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_completeconfirm_clickCancel)
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                    }
                }

                @Override
                public void onCanceled() {
                }
            });
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
            SnapsOrderManager.setBlockMultipleUploadError(false);
        }
    }

    /**
     * 주문을 진행하기 전에 갖추어야 할 필수 요소들을 점검하고 통과해야만 진행 한다.
     * 만약, 실패 했다면 handleOrderFailureReasonByFailedCode 에서 실패한 원인에 대한 핸들링을 해 준다.
     */
    private void startInspectRequiredOptionsWithProgressView() {
        try {
            hideTimerProgress();

            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_PREPARING);

            snapsOrderHandler.performInspectRequiredOptions(preInspectListener);
        } catch (Exception e) {
            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
            SnapsOrderManager.setBlockMultipleUploadError(false);
        }
    }

    private SnapsOrderResultListener preInspectListener = new SnapsOrderResultListener() {
        @Override
        public void onSnapsOrderResultSucceed(Object resultMsgObj) {
            Dlog.d("onSnapsOrderResultSucceed()");
            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);

            startUploadWithProgressBar();
        }

        @Override
        public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
            Dlog.w(TAG, "onSnapsOrderResultFailed()");

            if (resultMsgObj != null && resultMsgObj instanceof Integer) {
                boolean isCriticalError = handleOrderFailureReasonByFailedCode((int) resultMsgObj);
                if (isCriticalError) {
                    changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);
                }
            }

            SnapsOrderManager.setBlockMultipleUploadError(false);
        }
    };

    /**
     * 본격적으로 장바구니에 업로드를 시작한다.
     */
    private void startUploadWithProgressBar() {
        try {
            cancelThumbnailUpload();

            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_UPLOADING);

            //여기 까지 넘어왔다는 것은 이미 프로젝트 코드를 받은 상태라는 것을 의미하니, 프로그래스바를 진행 시켜 준다.
            SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_GET_PROJECT_CODE);

            snapsOrderHandler.prepareProjectUpload(projectUploadResultListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);
            SnapsOrderManager.setBlockMultipleUploadError(false);
        }
    }

    private void cancelThumbnailUpload() {
        SnapsOrderManager orderManager = SnapsOrderManager.getInstance();
        orderManager.cancelThumbnailUploadExecutor();
    }

    private SnapsOrderResultListener projectUploadResultListener = new SnapsOrderResultListener() {
        @Override
        public void onSnapsOrderResultSucceed(Object resultMsgObj) {
            Dlog.d("onSnapsOrderResultSucceed()");
            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);

            exportAutoSaveTemplateFile();

            handleSaveSuccessOnUIThread(resultMsgObj);

            SnapsOrderManager.setBlockMultipleUploadError(false);
        }

        @Override
        public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
            Dlog.w(TAG, "onSnapsOrderResultFailed()");
            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);
            handleSaveFailOnUIThread(resultMsgObj, orderType);

            SnapsOrderManager.setBlockMultipleUploadError(false);
        }
    };

    /**
     * 백그라운드에서 원본 이미지를 업로드 하도록 요청한다.
     * (그냥 이거만 호출하면 알아서 업로드 이미지 리스트를 갱신도 하고 다 한다.)
     */
    public static synchronized void uploadOrgImgOnBackground() {
        Dlog.d("uploadOrgImgOnBackground()");
        SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            if (SmartSnapsManager.isFirstSmartAreaSearching()) return;

            if (!isExistNotUploadOrgImage()) return;

            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            if (orderHandler == null) return;

            //LTE로 올릴 때는 사용자의 컨펌이 필요 하다
            if (SnapsImageUploadUtil.shouldConfirmUseCellularData(getOrderActivity())) {
                orderHandler.showConfirmBackgroundUploadByCellularData(getOrderActivity(), new ICommonConfirmListener() {
                    @Override
                    public void onConfirmed() {
                        SnapsOrderManager.uploadOrgImgOnBackground();
                    }
                });
                return;
            }

            if (SnapsImageUploadUtil.isAllowBackgroundImgUploadNetworkState(getOrderActivity())) {
                orderHandler.performUploadOrgImgAtBackground(snapsOrderManager.getSnapsOrderActivityBridge().getUploadImageList(), snapsOrderManager.baseBackgroundOrgImageUploadListener);
            }
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    /**
     * 백그라운드에서 썸네일 이미지를 업로드 하도록 요청한다.
     */
    public static synchronized void uploadThumbImgListOnBackground() {
        Dlog.d("uploadThumbImgListOnBackground()");
        if (!checkThumbImgUploadState()) return;

        try {
            SnapsOrderManager snapsOrderManager = getInstance();
            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            if (orderHandler != null)
                orderHandler.performUploadThumbImgListAtBackground(snapsOrderManager.getSnapsOrderActivityBridge().getUploadImageList(), snapsOrderManager.baseBackgroundThumbImageUploadListener);
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    public static boolean isUploadingImageData(MyPhotoSelectImageData imageData) {
        try {
            SnapsOrderManager snapsOrderManager = getInstance();
            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            if (orderHandler != null)
                return orderHandler.isContainedUploadingImageData(imageData);
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
        return false;
    }

    public static synchronized void uploadThumbImgOnBackground(MyPhotoSelectImageData imageData) {
        Dlog.d("uploadThumbImgOnBackground()");
        if (!checkThumbImgUploadState()) return;

        try {
            SnapsOrderManager snapsOrderManager = getInstance();
            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            if (orderHandler != null)
                orderHandler.performUploadThumbImgAtBackground(imageData, snapsOrderManager.baseBackgroundThumbImageUploadListener);
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
            ;
        }
    }

    private static boolean checkThumbImgUploadState() {
        SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            if (!SmartSnapsManager.isSupportSmartSnapsProduct()) {
                if (snapsOrderManager.getImageUploadStateListener() != null)
                    snapsOrderManager.getImageUploadStateListener().onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState.NOT_SUPPORT_THUMBNAIL_UPLOAD, null);
                return false;
            }

            if (!isExistNotUploadThumbImage()) {
                if (snapsOrderManager.getImageUploadStateListener() != null)
                    snapsOrderManager.getImageUploadStateListener().onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState.FINISH, null);
                return false;
            }

            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            if (orderHandler == null) return false;

            //LTE로 올릴 때는 사용자의 컨펌이 필요 하다
            if (SnapsImageUploadUtil.shouldConfirmUseCellularData(getOrderActivity())) {
                orderHandler.showConfirmBackgroundUploadByCellularData(getOrderActivity(), new ICommonConfirmListener() {
                    @Override
                    public void onConfirmed() {
                        SnapsOrderManager.uploadThumbImgListOnBackground();
                    }
                });
                return false;
            }

            if (!SnapsImageUploadUtil.isAllowBackgroundImgUploadNetworkState(getOrderActivity())) {
                return false;
            }
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
            return false;
        }

        return true;
    }

    public static synchronized void cancelCurrentImageUploadExecutor() {
        SnapsOrderManager orderManager = getInstance();
        try {
            SnapsOrderBaseHandler orderHandler = orderManager.snapsOrderHandler;
            if (orderHandler != null) {
                orderHandler.cancelThumbnailImgUploadExecutor();
                orderHandler.cancelOrgImgUploadExecutor();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void cancelThumbnailUploadExecutor() {
        try {
            if (snapsOrderHandler != null) snapsOrderHandler.cancelThumbnailImgUploadExecutor();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static int getUploadImageListSize() {
        try {
            return getInstance().getSnapsOrderActivityBridge().getUploadImageList().size();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return 0;
        }
    }

    private static boolean isExistNotUploadOrgImage() {
        SnapsOrderManager snapsOrderManager = getInstance();
        SnapsOrderActivityBridge bridge = snapsOrderManager.getSnapsOrderActivityBridge();
        if (bridge != null) {
            ArrayList<MyPhotoSelectImageData> imageList = bridge.getUploadImageList();
            if (imageList == null || imageList.isEmpty()) return false;
            try {
                for (MyPhotoSelectImageData imageData : imageList) {
                    if (SnapsImageUploadUtil.shouldBeOrgImgUploadWithImageData(imageData))
                        return true;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    private static boolean isExistNotUploadThumbImage() {
        SnapsOrderManager snapsOrderManager = getInstance();
        SnapsOrderActivityBridge bridge = snapsOrderManager.getSnapsOrderActivityBridge();
        if (bridge != null) {
            ArrayList<MyPhotoSelectImageData> imageList = bridge.getUploadImageList();
            if (imageList == null || imageList.isEmpty()) return false;
            try {
                for (MyPhotoSelectImageData imageData : imageList) {
                    if (SnapsImageUploadUtil.shouldBeThumbImgUploadWithImageData(imageData))
                        return true;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

    /**
     * 업로드 중에 사진을 삭제할 경우, 백그라운드에서 업로드 하지 않도록 제거 해 준다.
     */
    public static void removeBackgroundUploadThumbImageData(@NonNull MyPhotoSelectImageData imageData) {
        SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            orderHandler.removeBackgroundUploadingThumbImageData(imageData);
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    /**
     * 업로드 중에 사진을 삭제할 경우, 백그라운드에서 업로드 하지 않도록 제거 해 준다.
     */
    public static void removeBackgroundUploadOrgImageData(@NonNull MyPhotoSelectImageData imageData) {
        SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            orderHandler.removeBackgroundUploadingOrgImageData(imageData);
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    /**
     * 업로드 중에 사진을 삭제할 경우, 백그라운드에서 업로드 하지 않도록 제거 해 준다.
     */
    public static void removeBackgroundUploadOrgImagesInPage(@NonNull SnapsPage page) {
        SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            List<MyPhotoSelectImageData> removeImageDataList = PhotobookCommonUtils.getImageDataListInSnapsPage(page);
            if (removeImageDataList != null && !removeImageDataList.isEmpty()) {
                SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
                orderHandler.removeBackgroundUploadingOrgImageDataList(removeImageDataList);
            }
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    /**
     * 네트워크 상태가 변경될 때, 자동으로 백그라운드 원본 이미지 기능을 가동 시킨다.
     */
    @Override
    public void onChangeNetworkState(@NonNull NetworkInfo networkInfo) {

        if (isEditorActivityOnResumeLock()) {
            Dlog.d("onChangeNetworkState()");
            return;
        }

        if (isConnectedBackgroundImgUploadNetworkState(networkInfo)) {
            SnapsIPManager.getInstance().setIPAddress();

            SnapsLogger.appendTextLog("onChangeNetworkState", "connected network. : " + CNetStatus.getInstance().getNetType(getOrderActivity()));

            if (SmartSnapsManager.isSmartAreaSearching()) {
                if (!SnapsImageUploadUtil.isAllowBackgroundImgUploadNetworkState(getOrderActivity())) {
                    stopBackgroundImgUpload();
                }
                return;
            }

            if (snapsHandler != null) {
                int uploadType = SmartSnapsManager.isSupportSmartSnapsProduct() ? HANDLE_MSG_PERFORM_THUMB_IMG_UPLOAD : HANDLE_MSG_PERFORM_ORG_IMG_UPLOAD;
                snapsHandler.sendEmptyMessageDelayed(uploadType, SnapsOrderConstants.DELAY_TIME_FOR_BACKGROUND_IMG_UPLOAD_PREPARE);
            }
        } else {
            SnapsLogger.appendTextLog("onChangeNetworkState", "disconnected network. : " + CNetStatus.getInstance().getNetType(getOrderActivity()));

            stopBackgroundImgUpload();
        }
    }

    private void stopBackgroundImgUpload() {
        suspendBackgroundImgUpload();

        if (getSnapsOrderActivityBridge() != null)
            getSnapsOrderActivityBridge().onDisconnectNetwork();
    }

    private boolean isEditorActivityOnResumeLock() {
        return isEditorActivityOnResumeLock;
    }

    public void lockEditorActivityOnResume() {
        isEditorActivityOnResumeLock = true;
        if (snapsHandler != null) {
            snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_UNLOCK_EDITOR_ACTIVITY_ON_RESUME, 200);
        }
    }

    /**
     * 저장하다 말고 홈키 눌러서 페이지 썸네일을 못 딸 경우를 대비해서
     */
    public static String getSnapsOrderStatePauseCode() {
        SnapsOrderManager orderManager = getInstance();
        return orderManager.snapsOrderState != null && orderManager.snapsOrderState.getPauseStateCode() != null ? orderManager.snapsOrderState.getPauseStateCode() : "";
    }

    public static void setSnapsOrderStatePauseCode(String code) {
        SnapsOrderManager orderManager = getInstance();
        if (orderManager.snapsOrderState != null)
            orderManager.snapsOrderState.setPauseStateCode(code);
    }

    private void setOrderStatePreparing() throws Exception {
        showTimerProgress(SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING, getStringFromOrderActivity(R.string.preparing_upload));
    }

    private void setOrderStateUploading() throws Exception {
        showTimerProgress(SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_UPLOAD, getStringFromOrderActivity(R.string.project_uploading));
        setUploadingState(true);
    }

    private void setOrderStateStop() throws Exception {
        setUploadingState(false);
        hideTimerProgress();
    }

    public static void showCompleteUploadPopup() {
        final SnapsOrderManager snapsOrderManager = getInstance();
        try {
            if (!snapsOrderManager.isInitialized())
                throw new SnapsOrderException("should initialize.");

            OrientationSensorManager.getInstance().setBlockSensorEvent(true);

            SnapsOrderBaseHandler orderHandler = snapsOrderManager.snapsOrderHandler;
            orderHandler.showCompleteUploadPopup(new DialogConfirmFragment.IDialogConfirmClickListener() {
                @Override
                public void onClick(boolean isOk) {
                    if (isOk) {
                        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_completenotice_clickMovecart)
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

                        PhotobookCommonUtils.initProductEditInfo();

                        snapsOrderManager.finishAutoSaveMode();
                        snapsOrderManager.goToCartPage();
                    } else {
                        //계속 편집하기 버튼을 눌렀다. 이 경우는 프로젝트 재저장이다.
                        snapsOrderManager.selectedEditContinueButton();

                        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_completenotice_clickContinue)
                                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                    }

                    OrientationSensorManager.getInstance().setBlockSensorEvent(false);
                }

                @Override
                public void onCanceled() {
                    //계속 편집하기 버튼을 눌렀다. 이 경우는 프로젝트 재저장이다.
                    snapsOrderManager.selectedEditContinueButton();
                }
            });

            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_completeconfirm_clickConfirm)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.AURA_XML, PhotobookCommonUtils.getLastSavedAuraOrderXmlContents())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

            AppEventsLogger logger = AppEventsLogger.newLogger(getOrderActivity());
            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
            if (snapsTemplateManager != null) {
                SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
                if (snapsTemplate != null) {
                    SnapsTemplateInfo templateInfo = snapsTemplate.info;
                    if (templateInfo != null) {
                        String prod_name = templateInfo.F_PROD_NAME;
                        String prod_Code = templateInfo.F_PROD_CODE;
                        Bundle params = new Bundle();
                        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, prod_name);
                        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, prod_Code);
                        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, 0, params);
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    public static boolean isUploadingProject() {
        SnapsOrderManager snapsOrderManager = getInstance();
        return snapsOrderManager.snapsOrderState != null && snapsOrderManager.snapsOrderState.isUploadingProject();
    }

    private static Activity getOrderActivity() {
        SnapsOrderManager snapsOrderManager = SnapsOrderManager.getInstance();
        return snapsOrderManager.getSnapsOrderActivityBridge() != null ? snapsOrderManager.getSnapsOrderActivityBridge().getActivity() : null;
    }

    public static String getProjectCode() {
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            return SnapsDiaryDataManager.getDiarySeq();
        }

        return Config.getPROJ_CODE();
    }

    private void showTimerProgress(@NonNull SnapsTimerProgressViewFactory.eTimerProgressType progressType, String title) {
        try {
            hideTimerProgress();

            Activity activity = getOrderActivity();
            if (activity != null && !activity.isFinishing())
                SnapsTimerProgressView.showProgress(activity, progressType, title);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private static void hideTimerProgress() {
        try {
            SnapsTimerProgressView.destroyProgressView();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private void selectedEditContinueButton() {
        Config.setPROJ_UTYPE("m");
    }

    private void changeOrderStateAndHandleProgressBar(int orderState) {
        if (getSnapsOrderStateListener() != null)
            getSnapsOrderStateListener().onOrderStateChanged(orderState);

        handleProgressBarState(orderState);
    }

    private void handleProgressBarState(int orderState) {
        try {
            switch (orderState) {
                case ISnapsOrderStateListener.ORDER_STATE_PREPARING:
                    setOrderStatePreparing();
                    break;
                case ISnapsOrderStateListener.ORDER_STATE_UPLOADING:
                    setOrderStateUploading();
                    break;
                case ISnapsOrderStateListener.ORDER_STATE_STOP:
                    setOrderStateStop();
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void handleSaveSuccessOnUIThread(final Object resultMsgObj) {
        Activity activity = getOrderActivity();
        if (activity == null || activity.isFinishing()) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleSaveSuccess(resultMsgObj);
            }
        });
    }

    private void clearDiaryUploadInfo() {
        SnapsDiaryDataManager diaryDataManager = SnapsDiaryDataManager.getInstance();
        diaryDataManager.clearUploadInfo();
    }

    private void handleSaveSuccess(Object resultMsgObj) {
        try {
            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);

            Activity activity = getOrderActivity();
            if (activity != null && !activity.isFinishing()) {
                SystemUtil.toggleScreen(activity.getWindow(), false);
            }

            if (getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                // 현재 Destory 상태이면 멈추고 index 값을 줄인다.
                setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_UPLOAD_COMPLETE);
            } else {
                if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                    clearDiaryUploadInfo();

                    showDiaryUploadCompletePopup(resultMsgObj);
                } else {
                    showCompleteUploadPopup();
                }
            }
        } catch (Exception e) {
            SnapsAssert.assertException(getOrderActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    private void showDiaryUploadCompletePopup(Object resultMsgObj) {
        try {
            final boolean isIssuedInk = (resultMsgObj != null && resultMsgObj instanceof Boolean) && (boolean) resultMsgObj;

            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            String msg = getStringFromOrderActivity(isIssuedInk ? R.string.diary_upload_complete_desc_get_ink : R.string.diary_upload_complete_desc_no_get_ink);
            String title = getStringFromOrderActivity(R.string.diary_upload_complete_title);
            if (dataManager.getWriteMode() == SnapsDiaryConstants.EDIT_MODE_MODIFY) {
                title = getStringFromOrderActivity(R.string.diary_modify_complete_title);
                msg = "";
            }

            SnapsDiaryDialog.showDialogOneBtn(getOrderActivity(),
                    title,
                    msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                            dataManager.notifyDiaryUploadOpservers(isIssuedInk, dataManager.getWriteMode() == SnapsDiaryConstants.EDIT_MODE_NEW_WRITE);
                        }
                    });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private void handleSaveFailOnUIThread(final Object msg, final SnapsOrderConstants.eSnapsOrderType orderType) {
        Activity activity = getOrderActivity();
        if (activity == null || activity.isFinishing()) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handleSaveFail(msg, orderType);
            }
        });
    }

    private void handleSaveFail(final Object msg, SnapsOrderConstants.eSnapsOrderType orderType) {
        try {
            initSaveMode();

            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);

            showSaveFailMsgAlert(msg, orderType);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private boolean isNetworkError(Object errContents) throws Exception {
        if (errContents == null) return false;

        if (errContents instanceof SnapsImageUploadResultData) {
            SnapsImageUploadResultData resultData = (SnapsImageUploadResultData) errContents;
            return resultData.getUploadResultMsg() == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE;
        }
        return errContents == SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE;
    }

    private boolean isUnknownHostError(Object errContents) throws Exception {
        if (errContents == null) return false;
        return errContents instanceof String && errContents == SnapsOrderConstants.EXCEPTION_MSG_UNKNOWN_HOST_ERROR;
    }

    private void showSaveFailMsgAlert(Object errContents, @NonNull SnapsOrderConstants.eSnapsOrderType orderType) throws Exception {
        if (isNetworkError(errContents)) {
            MessageUtil.alertnoTitleOneBtn(getOrderActivity(), getStringFromOrderActivity(R.string.common_network_error_msg), null);
            return;
        } else if (isUnknownHostError(errContents)) {
            SnapsLogger.sendLogException("order/" + orderType.toString(), SnapsOrderUnkownHostExceptionCreator.createExceptionWithMessage("project Code : " + Config.getPROJ_CODE()));
            MessageUtil.alertnoTitleOneBtn(getOrderActivity(), getStringFromOrderActivity(R.string.unknown_host_exception_alert_msg), null);
            return;
        }

        String alertMsg = "";
        String pageNum = "";

        switch (orderType) {
            case ORDER_TYPE_GET_PROJECT_CODE:
            case ORDER_TYPE_VERIFY_PROJECT_CODE:
                alertMsg = getStringFromOrderActivity(R.string.cart_upload_error_message_send_err_get_proj_code);
                pageNum = "366001";
                break;
            case ORDER_TYPE_UPLOAD_ORG_IMAGE:
                alertMsg = getStringFromOrderActivity(R.string.cart_upload_error_message_send_err_org_img_upload);
                pageNum = "366003";
                break;
            case ORDER_TYPE_MAKE_PAGE_THUMBNAILS:
            case ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL:
                alertMsg = getStringFromOrderActivity(R.string.upload_failed_cause_main_page_thumbnail);
                pageNum = "366002";
                break;
            case ORDER_TYPE_MAKE_XML:
                alertMsg = getStringFromOrderActivity(R.string.cart_upload_error_message_send_err_xml_make);
                pageNum = "366005";
                break;
            case ORDER_TYPE_UPLOAD_XML:
                alertMsg = getStringFromOrderActivity(R.string.cart_upload_error_message_send_err_xml_upload);
                pageNum = "366005";
                break;
        }

        if (errContents != null && errContents instanceof String) {
            alertMsg += ("\n" + (String) errContents);
        }

        Config.setOrderExceptionDesc((orderType != null ? orderType.toString() : "unknown"));

        handleUploadError(alertMsg, pageNum, orderType);
    }

    private void handleUploadError(@NonNull String alertMsg, String pageNum, SnapsOrderConstants.eSnapsOrderType orderType) throws Exception {
        //원본 이미지 오류는 사용자에게 문제가 있는 사진을 지우라고 안내 한다
        if (SnapsUploadFailedImageDataCollector.isExistFailedImageData(getProjectCode())) {
            getSnapsOrderActivityBridge().onUploadFailedOrgImgWhenSaveToBasket();
            return;
        }

        if (!StringUtil.isEmpty(alertMsg)) {
            alertMsg += (" projectCode : " + SnapsOrderManager.getProjectCode());
            MessageUtil.alertnoTitleOneBtn(getOrderActivity(), alertMsg, new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                }
            });
        }

        reportErrorLog(alertMsg, pageNum, orderType);
    }

    public static void reportErrorLog(String msg, SnapsOrderConstants.eSnapsOrderType orderType) {
        try {
            reportErrorLog(msg, null, orderType);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static void reportErrorLog(String alertMsg, String pageNum, SnapsOrderConstants.eSnapsOrderType orderType) throws Exception {
        //Crashlytics로도 충분해서 굳이 서버로 보내지 않음
//        sendAutoSaveFilesToSnapsServer(); //FTP _mount/Data1/mobileErrorLog에 수집된다

        SnapsLogger.sendLogOrderException(orderType, alertMsg);

        if (pageNum != null)
            HttpReq.sendSaveErrorLog(getProjectCode(), Config.getPROD_CODE(), SnapsLoginManager.getUUserNo(getOrderActivity()), SystemUtil.getAppVersion(getOrderActivity()), "365001", pageNum, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

//    private static void sendAutoSaveFilesToSnapsServer() throws Exception {
//        if (Config.isCalendar() || !AutoSaveManager.isSupportProductAutoSave()) {
//            ;
//        } else {
//            Context context = ContextUtil.getContext();
//            if (context == null) return;
//
//            String userId = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_ID);
//            if (StringUtil.isEmpty(userId))
//                userId = SnapsLoginManager.getUUserNo(context);
//
//            AutoSaveManager.sendAutoSaveFilesToSnapsServer(getOrderActivity(), userId);
//        }
//    }

    private void initSaveMode() throws Exception {
        setUploadingState(false);
        Activity activity = getOrderActivity();
        if (activity != null && !activity.isFinishing()) {
            SystemUtil.toggleScreen(activity.getWindow(), false);
        }
        setSnapsOrderStatePauseCode("");
        deleteMainThumbnailFile();
    }

    private void deleteMainThumbnailFile() throws Exception {
        String filePath = "";
        try {
            File file = Config.getTHUMB_PATH("thumb.jpg");
            filePath = file.getAbsolutePath();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        FileUtil.deleteFile(filePath);
    }

    private void initSnapsOrderHandler() throws Exception {
        if (getOrderActivity() == null)
            throw new SnapsOrderInitializeException("activity is null.");
        snapsOrderHandler = SnapsOrderHandlerFactory.createSnapsOrderHandler(getOrderActivity(), getSnapsOrderActivityBridge());
    }

    private void initSnapsOrderState() {
        snapsOrderState = SnapsOrderState.newInstance();
    }

    public void getProjectCode(SnapsOrderResultListener listener) throws Exception {
        snapsOrderHandler.getProjectCode(listener);
    }

    private SnapsImageUploadListener baseBackgroundOrgImageUploadListener = new SnapsImageUploadListener() {
        @Override
        public void onImageUploadStart() {
            Dlog.d("onImageUploadStart() background org img upload start.");
            if (getImageUploadStateListener() != null)
                getImageUploadStateListener().onOrgImgUploadStateChanged(eImageUploadState.START, null);
        }

        @Override
        public void onImageUploadAllBackgroundTaskFinished() {
            Dlog.d("onImageUploadAllBackgroundTaskFinished() background org img upload finished.");
            if (getImageUploadStateListener() != null)
                getImageUploadStateListener().onOrgImgUploadStateChanged(eImageUploadState.FINISH, null);
        }

        @Override
        public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
            Dlog.d("onImageUploadSucceed() background org img upload Succeed. --> (" + uploadResultData.getFinishedCnt() + ")" + uploadResultData.getImageData().F_IMG_NAME);
            if (getImageUploadStateListener() != null && getSnapsOrderActivityBridge() != null) {
                getImageUploadStateListener().onOrgImgUploadStateChanged(eImageUploadState.PROGRESS, uploadResultData);
            }
        }

        @Override
        public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
            if (uploadResultData != null && uploadResultData.getUploadResultMsg() != null) {
                switch (uploadResultData.getUploadResultMsg()) {
                    case UPLOAD_FAILED_CAUSE_NOT_EXIST_PROJECT_CODE:
                    case UPLOAD_FAILED_CAUSE_NOT_EXIST_DIARY_SEQ:
                        uploadOrgImgOnBgAfterGetProjectCode();
                        break;
                    case UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE:
                        //LTE로 올릴 때는 사용자의 컨펌이 필요 하다
                        if (SnapsImageUploadUtil.shouldConfirmUseCellularData(getOrderActivity())) {
                            if (snapsOrderHandler != null)
                                snapsOrderHandler.showConfirmBackgroundUploadByCellularData(getOrderActivity(), new ICommonConfirmListener() {
                                    @Override
                                    public void onConfirmed() {
                                        SnapsOrderManager.uploadOrgImgOnBackground();
                                    }
                                });
                            return;
                        }
                        break;
                }
            }


            if (uploadResultData != null) {
                getImageUploadStateListener().onOrgImgUploadStateChanged(eImageUploadState.FAILED, uploadResultData);
            }
        }
    };

    private SnapsImageUploadListener baseBackgroundThumbImageUploadListener = new SnapsImageUploadListener() {
        @Override
        public void onImageUploadStart() {
            Dlog.d("onImageUploadStart() background thumb img upload start.");
            if (getImageUploadStateListener() != null)
                getImageUploadStateListener().onThumbImgUploadStateChanged(eImageUploadState.START, null);
        }

        @Override
        public void onImageUploadAllBackgroundTaskFinished() {
            Dlog.d("onImageUploadAllBackgroundTaskFinished() background thumb img upload finished.");
            if (getImageUploadStateListener() != null)
                getImageUploadStateListener().onThumbImgUploadStateChanged(eImageUploadState.FINISH, null);
        }

        @Override
        public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
            Dlog.d("onImageUploadSucceed() background thumb img upload Succeed. --> (" + uploadResultData.getFinishedCnt() + ")" + uploadResultData.getImageData().F_IMG_NAME);
            if (getImageUploadStateListener() != null) {
                getImageUploadStateListener().onThumbImgUploadStateChanged(eImageUploadState.PROGRESS, uploadResultData);
            }
        }

        @Override
        public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
            if (uploadResultData != null && uploadResultData.getUploadResultMsg() != null) {
                switch (uploadResultData.getUploadResultMsg()) {
                    case UPLOAD_FAILED_CAUSE_NOT_EXIST_PROJECT_CODE:
                    case UPLOAD_FAILED_CAUSE_NOT_EXIST_DIARY_SEQ:
                        uploadThumbImgOnBgAfterGetProjectCode();
                        break;
                    case UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE:
                        if (getSnapsOrderActivityBridge() != null)
                            getSnapsOrderActivityBridge().onDisconnectNetwork();
                        break;
                }
            }


            if (uploadResultData != null) {
                getImageUploadStateListener().onThumbImgUploadStateChanged(eImageUploadState.FAILED, uploadResultData);
            }
        }
    };

    //만약 critical한 error일 경우 true반환
    private boolean handleOrderFailureReasonByFailedCode(int msg) {
        try {
            Activity activity = getOrderActivity();
            switch (msg) {
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_LOGGED_IN:
                    SnapsLoginManager.startLogInProcess(activity, Const_VALUES.LOGIN_P_RESULT, null, SnapsOrderConstants.LOGIN_REQUSTCODE);
                    break;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_PROJECT_CODE:
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_DIARY_SEQ:
                    performSaveToBasketAfterGetProjectCode();
                    return false;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT:
                    MessageUtil.alertnoTitleOneBtn(activity, getStringFromOrderActivity(R.string.not_photo_replenishment), null);
                    break;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_PRINTABLE_PHOTO_EXIST:
//                    MessageUtil.alertnoTitleOneBtn(activity, getStringFromOrderActivity(R.string.not_printable_photo_exist_please_change_and_save), new ICustomDialogListener() {
//                        @Override
//                        public void onClick(byte clickedOk) {
//                            changeOrderStateAndHandleProgressBar(ISnapsOrderStateListener.ORDER_STATE_STOP);
                    startUploadWithProgressBar();
//                        }
//                    });
                    return false;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_DIARY_IMAGE:
                    MessageUtil.alertnoTitleOneBtn(activity, getStringFromOrderActivity(R.string.not_photo_replenishment), null);
                    break;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_CONNECT_NETWORK:
                    MessageUtil.alertnoTitleOneBtn(activity, getStringFromOrderActivity(R.string.common_network_error_msg), null);
                    break;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_ENOUGH_STORAGE_SPACE:
                    MessageUtil.alertnoTitleOneBtn(activity, getStringFromOrderActivity(R.string.not_enough_storage_space_for_save), null);
                    break;
                case SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_IS_NOT_EXIST_PROJECT_FILE_FOLDER:
                    if (isGrantWriteExternalStoragePermission()) { //파일 읽고 쓰는 권한조차 가지고 있다면, 원인이 무엇일까!!? 일단 내부 저장소의 공간이 없다고 가정한다.
                        SnapsLogger.sendLogException("SnapsOrderManager", SnapsOrderFileCreateExceptionCreator.createExceptionWithMessage("project file create error...getExternalCacheDir : " + (activity != null ? Config.getExternalCacheDir(activity) : "activity is null")));
                        MessageUtil.alertnoTitleOneBtn(activity, getStringFromOrderActivity(R.string.not_enough_storage_space_for_save), null);
                    }
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
        return true;
    }

    private boolean isGrantWriteExternalStoragePermission() {
        if (Build.VERSION.SDK_INT > 22 && getOrderActivity() != null) {
            if (getOrderActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (getOrderActivity().shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    getOrderActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
                } else {
                    getOrderActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION);
                }
                return false;
            }
        }

        return true;
    }

    private SnapsOrderActivityBridge getSnapsOrderActivityBridge() {
        return snapsOrderActivityBridge;
    }

    private void setSnapsOrderActivityBridge(SnapsOrderActivityBridge snapsOrderActivityBridge) {
        this.snapsOrderActivityBridge = snapsOrderActivityBridge;
    }

    public SnapsImageUploadStateListener getImageUploadStateListener() {
        return imageUploadStateListener;
    }

    private void registerNetworkStateChangeHandler() throws Exception {
        unRegisterNetworkChangeReceiver();

        Activity activity = getOrderActivity();
        if (activity == null || activity.isFinishing()) return;
        networkChangeReceiver = new NetworkStateChangeReceiver();
        networkChangeReceiver.setNetworkStateChangeListener(this);
        activity.registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        setRegisterNetworkChangeReceiver(true);
    }

    private boolean isConnectedBackgroundImgUploadNetworkState(NetworkInfo networkInfo) {
        if (networkInfo == null) return false;
        CNetStatus netStatus = CNetStatus.getInstance();
        return netStatus.isAliveNetwork(getOrderActivity());
    }

    private void suspendBackgroundImgUpload() {
        Dlog.d("suspendBackgroundImgUpload() suspend background img upload.");
        try {
            if (snapsOrderHandler != null)
                snapsOrderHandler.suspendBackgroundImgUpload();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private void uploadOrgImgOnBgAfterGetProjectCode() {
        try {
            if (Config.isValidProjCodeWithStringCode(getProjectCode())) {
                uploadOrgImgOnBackground();
            } else {
                getProjectCode(new SnapsOrderResultListener() {
                    @Override
                    public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                        uploadOrgImgOnBackground();
                    }

                    @Override
                    public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                        Dlog.e(TAG, "uploadOrgImgOnBgAfterGetProjectCode() failed get project code.");
                        if (SmartSnapsManager.isFirstSmartAreaSearching()) {
                            if (getOrderActivity() == null || getOrderActivity().isFinishing() || getSnapsOrderActivityBridge() == null)
                                return;
                            getSnapsOrderActivityBridge().onDisconnectNetwork();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private void uploadThumbImgOnBgAfterGetProjectCode() {
        try {
            if (Config.isValidProjCodeWithStringCode(getProjectCode())) {
                uploadThumbImgListOnBackground();
            } else {
                getProjectCode(new SnapsOrderResultListener() {
                    @Override
                    public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                        uploadThumbImgListOnBackground();
                    }

                    @Override
                    public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                        Dlog.e(TAG, "uploadThumbImgOnBgAfterGetProjectCode() failed get project code.");
                        if (SmartSnapsManager.isFirstSmartAreaSearching()) {
                            if (getOrderActivity() == null || getOrderActivity().isFinishing() || getSnapsOrderActivityBridge() == null)
                                return;
                            getSnapsOrderActivityBridge().onDisconnectNetwork();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
        }
    }

    private void performSaveToBasketAfterGetProjectCode() {
        try {
            getProjectCode(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    startInspectRequiredOptionsWithProgressView();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.e(TAG, "performSaveToBasketAfterGetProjectCode() failed get project code.");
                    MessageUtil.alertnoTitleOneBtn(getOrderActivity(), getStringFromOrderActivity(R.string.cart_upload_error_message_6), null);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getOrderActivity(), e);
            MessageUtil.alertnoTitleOneBtn(getOrderActivity(), getStringFromOrderActivity(R.string.cart_upload_error_message_6), null);
        }
    }

    private void goToCartPage() {
        Activity activity = getOrderActivity();
        if (activity == null || activity.isFinishing()) return;
        try {
            Intent intent = new Intent(activity, RenewalHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("goToCart", true);
            activity.startActivity(intent);
            activity.finish();

            GoHomeOpserver.notifyGoHome();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void finishAutoSaveMode() {
        try {
            AutoSaveManager saveMan = AutoSaveManager.getInstance();
            if (saveMan != null) {
                saveMan.finishAutoSaveMode();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setUploadingState(boolean uploadingProject) {
        if (snapsOrderState != null) snapsOrderState.setUploadingProject(uploadingProject);
    }

    private String getStringFromOrderActivity(int resId) {
        Activity activity = getOrderActivity();
        return activity != null ? activity.getString(resId) : "";
    }

    public boolean isRegisterNetworkChangeReceiver() {
        return isRegisterNetworkChangeReceiver;
    }

    public void setRegisterNetworkChangeReceiver(boolean registerNetworkChangeReceiver) {
        isRegisterNetworkChangeReceiver = registerNetworkChangeReceiver;
    }

    public SnapsOrderBaseHandler getSnapsOrderHandler() {
        return snapsOrderHandler;
    }

    private static boolean isBlockMultipleUploadError() {
        return getInstance().isBlockMultipleUploadError;
    }

    private static void setBlockMultipleUploadError(boolean blockMultipleUploadError) {
        getInstance().isBlockMultipleUploadError = blockMultipleUploadError;
    }

    private void exportAutoSaveTemplateFile() {
        if (getSnapsOrderActivityBridge() == null) return;
        try {
            AutoSaveManager.exportAutoSaveTemplateFile(getSnapsOrderActivityBridge().getTemplate());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getSnapsOrderActivityBridge().getActivity(), e);
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    private void setSnapsOrderStateListener(ISnapsOrderStateListener snapsOrderStateListener) {
        SnapsOrderManager snapsOrderManager = getInstance();
        SnapsOrderState orderState = snapsOrderManager.snapsOrderState;
        if (orderState != null) {
            orderState.setSnapsOrderStateListener(snapsOrderStateListener);
        }
    }

    private ISnapsOrderStateListener getSnapsOrderStateListener() {
        SnapsOrderManager snapsOrderManager = getInstance();
        SnapsOrderState orderState = snapsOrderManager.snapsOrderState;
        if (orderState != null) {
            return orderState.getSnapsOrderStateListener();
        }
        return null;
    }

    public void createThumbnailCacheWithImageData(Context context, MyPhotoSelectImageData imageData) throws Exception {
        snapsThumbnailMakeExecutor.start(context, imageData);
    }

    private static final int HANDLE_MSG_PERFORM_ORG_IMG_UPLOAD = 0;
    private static final int HANDLE_MSG_PERFORM_THUMB_IMG_UPLOAD = 1;
    private static final int HANDLE_MSG_UNLOCK_EDITOR_ACTIVITY_ON_RESUME = 2;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLE_MSG_PERFORM_ORG_IMG_UPLOAD:
                if (isInitialized())
                    uploadOrgImgOnBackground();
                break;
            case HANDLE_MSG_PERFORM_THUMB_IMG_UPLOAD:
                if (isInitialized())
                    uploadThumbImgListOnBackground();
                break;
            case HANDLE_MSG_UNLOCK_EDITOR_ACTIVITY_ON_RESUME:
                isEditorActivityOnResumeLock = false;
                break;
        }
    }
}
