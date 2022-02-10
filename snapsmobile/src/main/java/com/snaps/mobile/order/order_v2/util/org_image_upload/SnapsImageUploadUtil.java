package com.snaps.mobile.order.order_v2.util.org_image_upload;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.json.SnapsDiaryImgUploadResultJson;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.exceptions.factories.SnapsInvalidImageDataExceptionCreator;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.task.upload_task.default_task.SnapsOrderUploadBaseImgTask;
import com.snaps.mobile.order.order_v2.util.org_image_upload.threadpool_util.PriorityThreadFactory;
import com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies.SnapsImageBaseUploadHandler;
import com.snaps.mobile.order.order_v2.util.org_image_upload.uploader_stratigies.SnapsOrgImgUploadHandlerFactory;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

import errorhandle.logger.SnapsLogger;

import static com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants.SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE;
import static com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants.SETTING_VALUE_USE_CELLULAR_DENIED_DATE;

/**
 * Created by ysjeong on 2017. 4. 3..
 */

public class SnapsImageUploadUtil {
    private static final String TAG = SnapsImageUploadUtil.class.getSimpleName();

    public static void startThumbImgUploadOnNewThread(final MyPhotoSelectImageData imageData, final SnapsImageUploadListener listener) throws Exception {
        ThreadFactory backgroundHighPriorityThreadFactory = new PriorityThreadFactory(1);
        Thread thread = backgroundHighPriorityThreadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SnapsImageUploadUtil.startThumbImgUploadWithImageData(imageData, new SnapsImageUploadListener() {
                        @Override
                        public void onImageUploadStart() { /** 결과 안 들어옴 **/ }

                        @Override
                        public void onImageUploadAllBackgroundTaskFinished() { /** 결과 안 들어옴 **/ }

                        @Override
                        public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
                            try {
                                listener.onImageUploadSucceed(uploadResultData);
                                listener.onImageUploadAllBackgroundTaskFinished();
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }

                        @Override
                        public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
                            try {
                                listener.onImageUploadFailed(uploadResultData);
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    });
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });

        thread.start();
    }

    public static void showConfirmBackgroundUploadByCellularData(final Activity activity, CustomizeDialog cellularDataConfirmDialog, final ICustomDialogListener dialogListener) throws Exception {
        if (activity == null || activity.isFinishing() || isAllowUploadByCellular(activity)) return;

        if (cellularDataConfirmDialog != null && cellularDataConfirmDialog.isShowing()) return;

        cellularDataConfirmDialog = MessageUtil.alertnoTitleTwoBtn(activity, activity.getString(R.string.confirm_background_upload_by_cellular_data), new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (dialogListener != null) {
                    dialogListener.onClick(clickedOk);
                }
            }
        });

        cellularDataConfirmDialog.setCancelable(false);
    }

    public static void showConfirmBackgroundUploadByCellularDataCheckDenied(final Activity activity, CustomizeDialog cellularDataConfirmDialog, final ICustomDialogListener dialogListener) throws Exception {
        if (isDeniedUseCellularData(activity)) return;

        showConfirmBackgroundUploadByCellularData(activity, cellularDataConfirmDialog, dialogListener);
    }

    public static boolean isThumbnailErrorMsg(String errMsg) throws Exception {
        return !StringUtil.isEmpty(errMsg) && errMsg.toLowerCase().startsWith("fail") && errMsg.replace("||", "|").split("\\|").length > 1;
    }

    public static SnapsOrderConstants.NETWORK_CHECK_RESULT getBackgroundImgUploadNetworkCheckResult(Context context) {
        if (context == null) return SnapsOrderConstants.NETWORK_CHECK_RESULT.ERROR;

        CNetStatus netStatus = CNetStatus.getInstance();
        switch (netStatus.getNetType(context)) {
            case CNetStatus.NET_TYPE_WIFI:
                return SnapsOrderConstants.NETWORK_CHECK_RESULT.SUCCESS;
            case CNetStatus.NET_TYPE_3G:
                if (isDeniedUseCellularData(context)) {
                    Setting.set(context, SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE, "");
                    return SnapsOrderConstants.NETWORK_CHECK_RESULT.FAILED_CAUSE_DENIED;
                } else if (!isAllowUploadByCellular(context)) return SnapsOrderConstants.NETWORK_CHECK_RESULT.FAILED_CAUSE_IS_NOT_ALLOW_CELLULAR;
                return SnapsOrderConstants.NETWORK_CHECK_RESULT.SUCCESS;
        }

        return SnapsOrderConstants.NETWORK_CHECK_RESULT.FAILED_CAUSE_IS_NOT_CONNECTED;
    }

    public static boolean isAllowBackgroundImgUploadNetworkState(Activity activity) {
        SnapsOrderConstants.NETWORK_CHECK_RESULT networkCheckResult = SnapsImageUploadUtil.getBackgroundImgUploadNetworkCheckResult(activity);
        return networkCheckResult == SnapsOrderConstants.NETWORK_CHECK_RESULT.SUCCESS;
    }

    public static boolean isDeniedUseCellularData(Context context) {
        String deniedDate = Setting.getString(context, SETTING_VALUE_USE_CELLULAR_DENIED_DATE);
        return DateUtil.isTodayDateWithSavedDate(deniedDate);
    }

    public static boolean shouldConfirmUseCellularData(Activity activity) {
        SnapsOrderConstants.NETWORK_CHECK_RESULT networkCheckResult = SnapsImageUploadUtil.getBackgroundImgUploadNetworkCheckResult(activity);
        return networkCheckResult == SnapsOrderConstants.NETWORK_CHECK_RESULT.FAILED_CAUSE_IS_NOT_ALLOW_CELLULAR;
    }

    public static boolean isAllowUploadByCellular(Context context) {
        String confirmDate = Setting.getString(context, SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE);
        return DateUtil.isTodayDateWithSavedDate(confirmDate);
    }

    private static void postImageUploadResultSuccess(final SnapsImageUploadListener orgImgUploadListener, final MyPhotoSelectImageData uploadImageData, final SnapsOrderConstants.eSnapsOrderUploadResultMsg resultMsg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (orgImgUploadListener != null) {
                    orgImgUploadListener.onImageUploadSucceed(createImageUploadResultMsgData(uploadImageData, resultMsg));
                }
            }
        });
    }

    private static void postImageUploadResultFailed(final SnapsImageUploadListener orgImgUploadListener, final MyPhotoSelectImageData uploadImageData, final SnapsOrderConstants.eSnapsOrderUploadResultMsg resultMsg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (orgImgUploadListener != null) {
                    orgImgUploadListener.onImageUploadFailed(createImageUploadResultMsgData(uploadImageData, resultMsg));
                }
            }
        });
    }

    synchronized static void startOrgImgUploadWithImageData(MyPhotoSelectImageData uploadImageData, final SnapsImageUploadListener orgImgUploadListener) throws Exception {
        if (skipIfAlreadyOrgImgUploadingOnBackground(uploadImageData)) {
            postImageUploadResultSuccess(orgImgUploadListener, uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_SKIPPED);
            return;
        }

        //왜 인지 모르겠는데 crashlytics 로그를 보면 업로드하려는 파일이 없는 건지..FileNotFoundException이 간헐적으로 발생한다
        if (isNotExistLocalImage(uploadImageData)) {
            postImageUploadResultFailed(orgImgUploadListener, uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_EXIST_IMAGE);
            uploadImageData.finishUploadSyncLock();
            return;
        }

        //스마트 스냅스 상품은 썸네일을 올려야 원본을 올릴 수 있다
        if (SnapsImageUploadUtil.isNotUploadedThumbnail(uploadImageData)) {
            handleUploadImageAfterThumbnailUpload(uploadImageData, orgImgUploadListener);
            return;
        }

        try {
            Dlog.d("startOrgImgUploadWithImageData() start org image upload on background:" + uploadImageData.F_IMG_NAME);
            uploadImageData.startUploadSyncLock(); //unLock처리는 handleAnalyzeUploadResultMsg 내 에서 정보 분석을 마친 후 에 진행한다.

            startMeasureOrgImageUploadTime(uploadImageData); //Timer Progress view 처리를 위해 올리기 직전과 완료된 시간을 측정한다.

            SnapsImageBaseUploadHandler uploadHandler = SnapsOrgImgUploadHandlerFactory.createOrgImgUploadHandler(uploadImageData);
            String message = uploadHandler.requestImageUpload();

            Dlog.d("startOrgImgUploadWithImageData() end org image upload on background:" + uploadImageData.F_IMG_NAME);

            finishMeasureOrgImageUploadTime(uploadImageData);

            uploadHandler.handleAnalyzeUploadResultMsg(message, orgImgUploadListener);
        } finally {
            uploadImageData.finishUploadSyncLock();
        }
    }

    private static void handleUploadImageAfterThumbnailUpload(final MyPhotoSelectImageData imageData, final SnapsImageUploadListener orgImgUploadListener) throws Exception {
        SnapsImageUploadUtil.startThumbImgUploadWithImageData(imageData, new SnapsImageUploadListener() {
            @Override
            public void onImageUploadStart() {}
            @Override
            public void onImageUploadAllBackgroundTaskFinished() {}

            @Override
            public void onImageUploadSucceed(SnapsImageUploadResultData uploadResultData) {
                try {
                    if (SnapsImageUploadUtil.isNotUploadedThumbnail(imageData)) { //그래도 실패 했다면 실패다..
                        postImageUploadResultFailed(orgImgUploadListener, imageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_THUMBNAIL_RETURN_VALUE_ERR);
                        imageData.finishUploadSyncLock();
                    } else {
                        if (orgImgUploadListener !=  null && orgImgUploadListener instanceof SnapsOrderUploadBaseImgTask.SnapsImageUploadResultListener) {
                            startOrgImgUploadByAsync(imageData, (SnapsOrderUploadBaseImgTask.SnapsImageUploadResultListener) orgImgUploadListener);
                        } else {  //이게 else면 개발 오류일 가능성이 크다.
                            postImageUploadResultFailed(orgImgUploadListener, imageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_THUMBNAIL_RETURN_VALUE_ERR);
                            imageData.finishUploadSyncLock();
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onImageUploadFailed(SnapsImageUploadResultData uploadResultData) {
                postImageUploadResultFailed(orgImgUploadListener, imageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_THUMBNAIL_RETURN_VALUE_ERR);
                imageData.finishUploadSyncLock();
            }
        });
    }

    public synchronized static void startThumbImgUploadWithImageData(MyPhotoSelectImageData uploadImageData, final SnapsImageUploadListener imgUploadListener) throws Exception {
        if (skipIfAlreadyThumbImgUploadingOnBackground(uploadImageData)) {
            postImageUploadResultSuccess(imgUploadListener, uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_SKIPPED);
            return;
        }

        //왜 인지 모르겠는데 crashlytics 로그를 보면 업로드하려는 파일이 없는 건지..FileNotFoundException이 간헐적으로 발생한다
        if (isNotExistLocalImage(uploadImageData)) {
            postImageUploadResultFailed(imgUploadListener, uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_EXIST_IMAGE);
            uploadImageData.finishUploadSyncLock();
            return;
        }

        try {
            Dlog.d("startThumbImgUploadWithImageData() start thumb image upload on background:" + uploadImageData.F_IMG_NAME);
            uploadImageData.startUploadSyncLock(); //unLock처리는 handleAnalyzeUploadResultMsg 내 에서 정보 분석을 마친 후 에 진행한다.

            SnapsImageBaseUploadHandler uploadHandler = SnapsOrgImgUploadHandlerFactory.createThumbImgUploadHandler(uploadImageData);
            String message = uploadHandler.requestImageUpload();

            Dlog.d("startThumbImgUploadWithImageData() end thumb image upload on background:" + uploadImageData.F_IMG_NAME);

            uploadHandler.handleAnalyzeUploadResultMsg(message, imgUploadListener);
        } finally {
            uploadImageData.finishUploadSyncLock();
        }
    }

    private static boolean isNotExistLocalImage(MyPhotoSelectImageData uploadImageData) {
        return uploadImageData == null || StringUtil.isEmpty(uploadImageData.PATH) || (!uploadImageData.PATH.startsWith("http") && !new File(uploadImageData.PATH).exists());
    }

    private static boolean skipIfAlreadyOrgImgUploadingOnBackground(MyPhotoSelectImageData uploadImageData) throws Exception {
//        Logg.y("skipIfAlreadyOrgImgUploadingOnBackground lock : " + (uploadImageData != null ? uploadImageData.F_IMG_NAME : ""));
        uploadImageData.waitIfUploading(); //이미 해당 이미지를 다른 쓰레드에서 올리고 있다면, 다 올릴때까지 기다린다.
//        Logg.y("skipIfAlreadyOrgImgUploadingOnBackground unLock : " + (uploadImageData != null ? uploadImageData.F_IMG_NAME : ""));

        return isOrgImgUploadedOnImageData(uploadImageData);
    }

    private static boolean skipIfAlreadyThumbImgUploadingOnBackground(MyPhotoSelectImageData uploadImageData) throws Exception {
        uploadImageData.waitIfUploading(); //이미 해당 이미지를 다른 쓰레드에서 올리고 있다면, 다 올릴때까지 기다린다.

        return isThumbImgUploadedOnImageData(uploadImageData);
    }

    private static void startMeasureOrgImageUploadTime(MyPhotoSelectImageData uploadImageData) {
        SnapsTimerProgressView.setOffsetForMeasureImageData(uploadImageData);
    }

    private static void finishMeasureOrgImageUploadTime(MyPhotoSelectImageData uploadImageData) {
        SnapsTimerProgressView.updateOrgImgUploadProgressValueWithEstimateTime(uploadImageData);
    }

    public static boolean isNotUploadedThumbnail(MyPhotoSelectImageData uploadImageData) {
        return SmartSnapsManager.isSupportSmartSnapsProduct() && uploadImageData != null && !SnapsImageUploadUtil.isValidUploadedThumbImageData(uploadImageData);
    }

    public synchronized static void startOrgImgUploadByAsync(@NonNull final MyPhotoSelectImageData uploadImageData, @NonNull  final SnapsOrderUploadBaseImgTask.SnapsImageUploadResultListener orgImgUploadListener) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
                orgImgUploadListener.onImageUploadStart();
            }

            @Override
            public void onBG() {
                try {
                    startOrgImgUploadWithImageData(uploadImageData, orgImgUploadListener);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    orgImgUploadListener.onImageUploadFailed(createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_EXCEPTION));
                }
            }

            @Override
            public void onPost() {}
        });
    }

    public synchronized static void startThumbImgUploadByAsync(@NonNull final MyPhotoSelectImageData uploadImageData, @NonNull  final SnapsOrderUploadBaseImgTask.SnapsImageUploadResultListener thumbImgUploadListener) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
                thumbImgUploadListener.onImageUploadStart();
            }

            @Override
            public void onBG() {
                try {
                    startThumbImgUploadWithImageData(uploadImageData, thumbImgUploadListener);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    thumbImgUploadListener.onImageUploadFailed(createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_EXCEPTION));
                }
            }

            @Override
            public void onPost() {}
        });
    }

    public synchronized static void startOrgImgUploadByAsyncAfterThumbImgUpload(@NonNull final MyPhotoSelectImageData uploadImageData, @NonNull  final SnapsOrderUploadBaseImgTask.SnapsImageUploadResultListener thumbImgUploadListener) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
                thumbImgUploadListener.onImageUploadStart();
            }

            @Override
            public void onBG() {
                try {
                    startThumbImgUploadWithImageData(uploadImageData, null);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    thumbImgUploadListener.onImageUploadFailed(createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_EXCEPTION));
                }
            }

            @Override
            public void onPost() {
                try {
                    if (SmartSnapsManager.isSupportSmartSnapsProduct() && SnapsImageUploadUtil.isNotUploadedThumbnail(uploadImageData)) {
                        thumbImgUploadListener.onImageUploadFailed(createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_EXCEPTION));
                    } else {
                        startOrgImgUploadByAsync(uploadImageData, thumbImgUploadListener);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    thumbImgUploadListener.onImageUploadFailed(createImageUploadResultMsgData(uploadImageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_EXCEPTION));
                }
            }
        });
    }

    public static SnapsDelImage createSnapsDelImageByUploadResultValue(MyPhotoSelectImageData uploadImageData, String[] returnValue) throws ArrayIndexOutOfBoundsException {
        if (uploadImageData == null) return null;
        SnapsDelImage delImg = new SnapsDelImage();
        delImg.imgYear = returnValue[2].replace("/", "");
        delImg.imgSeq = returnValue[3].replace("/", "");
        delImg.uploadPath = returnValue[1];
//        delImg.tinyPath = returnValue[9]; 안쓴다.
        delImg.oriPath = returnValue[7];
        delImg.sizeOrgImg = returnValue[4] + " " + returnValue[5];
        delImg.realFileName = uploadImageData.F_IMG_NAME;
        delImg.shootDate = returnValue[0];
        delImg.usedImgCnt = "0";
        return delImg;
    }

    public static SnapsDelImage createSnapsDelImageByUploadResultValue(MyPhotoSelectImageData uploadImageData, SnapsDiaryImgUploadResultJson imgUploadResult) {
        if (imgUploadResult == null || uploadImageData == null) return null;
        SnapsDelImage delImg = new SnapsDelImage();
        delImg.imgYear = imgUploadResult.getImgYear();
        delImg.imgSeq = imgUploadResult.getImgSqnc();
        delImg.uploadPath = imgUploadResult.getRealFilePath();
        delImg.tinyPath = imgUploadResult.getTinyFilePath();
        delImg.oriPath = imgUploadResult.getOrgFilePath();
        delImg.sizeOrgImg = imgUploadResult.getImgWidth() + " " + imgUploadResult.getImgHeight();
        delImg.realFileName = uploadImageData.F_IMG_NAME;
        delImg.shootDate = imgUploadResult.getDiaryNo();
        delImg.usedImgCnt = "0";
        return delImg;
    }

    public static void setUploadedOrgImageInfoByUploadResultValue(MyPhotoSelectImageData uploadImageData, String[] returnValue) throws ArrayIndexOutOfBoundsException {
        if (uploadImageData == null || returnValue == null) return;
        uploadImageData.F_IMG_YEAR = returnValue[2].replace("/", "");
        uploadImageData.F_IMG_SQNC = returnValue[3].replace("/", "");
        uploadImageData.F_UPLOAD_PATH = returnValue[1];
        uploadImageData.THUMBNAIL_PATH = returnValue[8];
        uploadImageData.ORIGINAL_PATH = returnValue[7];

        checkInvalidOrgImageUploadInfoWithImageData(uploadImageData, returnValue);
    }

    private static void checkInvalidOrgImageUploadInfoWithImageData(MyPhotoSelectImageData uploadImageData, String[] returnValue) {
        if (!isValidUploadedOrgImageData(uploadImageData)) {
            StringBuilder builder = new StringBuilder();
            builder.append("uploadImageData.F_IMG_YEAR : ").append(uploadImageData.F_IMG_YEAR).append("\n");
            builder.append("uploadImageData.F_IMG_SQNC : ").append(uploadImageData.F_IMG_SQNC).append("\n");
            builder.append("uploadImageData.F_UPLOAD_PATH : ").append(uploadImageData.F_UPLOAD_PATH).append("\n");

            if (returnValue != null) {
                builder.append("return value : ");
                for (String str : returnValue) {
                    builder.append(str);
                }
            }

            SnapsLogger.sendLogException("SnapsImageUploadUtil/checkInvalidOrgImageUploadInfoWithImageData", SnapsInvalidImageDataExceptionCreator.createExceptionWithMessage(builder.toString()));
        }
    }

    public static void setUploadedThumbImageInfoByUploadResultValue(MyPhotoSelectImageData uploadImageData, String[] returnValue) throws ArrayIndexOutOfBoundsException {
        if (uploadImageData == null || returnValue == null) return;
        uploadImageData.F_IMG_YEAR = returnValue[2].replace("/", "");
        uploadImageData.F_IMG_SQNC = returnValue[3].replace("/", "");
        uploadImageData.THUMBNAIL_PATH = returnValue[8];

        checkInvalidThumbImageUploadInfoWithImageData(uploadImageData, returnValue);
    }

    private static void checkInvalidThumbImageUploadInfoWithImageData(MyPhotoSelectImageData uploadImageData, String[] returnValue) {
        if (!isValidUploadedThumbImageData(uploadImageData)) {
            StringBuilder builder = new StringBuilder();
            builder.append("uploadImageData.F_IMG_YEAR : ").append(uploadImageData.F_IMG_YEAR).append("\n");
            builder.append("uploadImageData.F_IMG_SQNC : ").append(uploadImageData.F_IMG_SQNC).append("\n");
            builder.append("uploadImageData.THUMBNAIL_PATH : ").append(uploadImageData.THUMBNAIL_PATH).append("\n");

            if (returnValue != null) {
                builder.append("return value : ");
                for (String str : returnValue) {
                    builder.append(str);
                }
            }

            SnapsLogger.sendLogException("SnapsImageUploadUtil/checkInvalidThumbImageUploadInfoWithImageData", SnapsInvalidImageDataExceptionCreator.createExceptionWithMessage(builder.toString()));
        }
    }

    public static SnapsImageUploadResultData createImageUploadResultMsgData(MyPhotoSelectImageData imageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg errMsg) {
        return createImageUploadResultMsgData(imageData, errMsg, "");
    }

    public static SnapsImageUploadResultData createImageUploadResultMsgData(MyPhotoSelectImageData imageData, SnapsOrderConstants.eSnapsOrderUploadResultMsg errMsg, String detailMsg) {
        return new SnapsImageUploadResultData.Builder().setImageData(imageData).setErrMsg(errMsg).setDetailMsg(detailMsg).create();
    }

    public static SnapsImageUploadResultData createOrgImgSuccessData(MyPhotoSelectImageData imageData, SnapsDelImage delImage) {
        return new SnapsImageUploadResultData.Builder().setImageData(imageData).setDelImage(delImage).create();
    }

    public static boolean isValidUploadedOrgImageData(MyPhotoSelectImageData uploadImageData) {
        return uploadImageData != null
                && !StringUtil.isEmpty(uploadImageData.F_UPLOAD_PATH)
                && !StringUtil.isEmpty(uploadImageData.F_IMG_YEAR)
                && !StringUtil.isEmpty(uploadImageData.F_IMG_SQNC);
    }

    public static boolean isValidUploadedThumbImageData(MyPhotoSelectImageData uploadImageData) {
        return uploadImageData != null
                && !StringUtil.isEmpty(uploadImageData.THUMBNAIL_PATH) && (uploadImageData.THUMBNAIL_PATH.startsWith("http") || uploadImageData.THUMBNAIL_PATH.startsWith("/Upload"))
                && !StringUtil.isEmpty(uploadImageData.F_IMG_YEAR)
                && !StringUtil.isEmpty(uploadImageData.F_IMG_SQNC);
    }

    public static void initOrgImgUploadInfo(MyPhotoSelectImageData uploadImageData) {
        if (uploadImageData != null) {
            uploadImageData.F_UPLOAD_PATH = "";
            uploadImageData.F_IMG_YEAR = "";
            uploadImageData.F_IMG_SQNC = "";
        }
    }

    public static void initThumbImgUploadInfo(MyPhotoSelectImageData uploadImageData) {
        if (uploadImageData != null) {
            uploadImageData.THUMBNAIL_PATH = "";
            uploadImageData.F_IMG_YEAR = "";
            uploadImageData.F_IMG_SQNC = "";
        }
    }

    public static boolean shouldBeOrgImgUploadWithImageData(MyPhotoSelectImageData orgData) {
        return shouldBeUploadImageDataKind(orgData)
                && (orgData.F_IMG_SQNC.equals("") || orgData.F_UPLOAD_PATH.equals("") || orgData.F_IMG_YEAR.equals(""));
    }

    public static boolean shouldBeThumbImgUploadWithImageData(MyPhotoSelectImageData orgData) {
        return shouldBeUploadImageDataKind(orgData) && !isValidUploadedThumbImageData(orgData);
    }

    public static boolean shouldBeUploadImageDataKind(MyPhotoSelectImageData orgData) {
        return (orgData != null)
                && (orgData.KIND == Const_VALUES.SELECT_PHONE || orgData.KIND == Const_VALUES.SELECT_UPLOAD || (!Config.isSNSBook() && Config.isSNSPhoto(orgData.KIND)));
    }

    public static boolean isOrgImgUploadedOnImageData(MyPhotoSelectImageData orgData) {
        try {
            fixInvalidUploadedOrgImageData(orgData);
        } catch (Exception e) { Dlog.e(TAG, e); }

        return !shouldBeOrgImgUploadWithImageData(orgData) && !orgData.isUploadFailedOrgImage;
    }

    public static boolean isThumbImgUploadedOnImageData(MyPhotoSelectImageData orgData) {
        try {
            fixInvalidUploadedThumbImageData(orgData);
        } catch (Exception e) { Dlog.e(TAG, e); }

        return !shouldBeThumbImgUploadWithImageData(orgData);
    }

    //간헐적으로 업로드는 되었는데 필수 정보가 누락되는 케이스가 확인 되어서 보정 코드를 넣었다.
    public static void fixInvalidUploadedOrgImageData(MyPhotoSelectImageData orgData) throws Exception {
        if (orgData.KIND == Const_VALUES.SELECT_UPLOAD
                && (orgData.F_IMG_SQNC.equals("") || orgData.F_UPLOAD_PATH.equals("") || orgData.F_IMG_YEAR.equals(""))) {
            String imagePath = orgData.PATH;
            if (imagePath != null && imagePath.contains("storage")) {
                orgData.KIND = Const_VALUES.SELECT_PHONE;
            }
            SnapsImageUploadUtil.initOrgImgUploadInfo(orgData);
        }
    }

    public static void fixInvalidUploadedThumbImageData(MyPhotoSelectImageData orgData) throws Exception {
        if (orgData.KIND == Const_VALUES.SELECT_UPLOAD
                && !isValidUploadedThumbImageData(orgData)) {
            String imagePath = orgData.PATH;
            if (imagePath != null && imagePath.contains("storage")) {
                orgData.KIND = Const_VALUES.SELECT_PHONE;
            }
            SnapsImageUploadUtil.initThumbImgUploadInfo(orgData);
        }
    }


    public static void addDelImgDataInTemplate(SnapsTemplate template, SnapsDelImage delImage) {
        if (template != null && template.delimgList != null && delImage != null) {
            try {
                removeContainedDelImgDataInTemplate(template.delimgList, delImage);
                template.delimgList.add(delImage);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }


    private static void removeContainedDelImgDataInTemplate(ArrayList<SnapsDelImage> templateDelList, SnapsDelImage insertData) throws Exception {
        if (templateDelList == null || insertData == null) return;

        for (int ii = templateDelList.size()-1; ii>=0; ii--) {
            SnapsDelImage delImage = templateDelList.get(ii);
            if (delImage == null) continue;
            if (!StringUtil.isEmpty(delImage.imgSeq) && delImage.imgSeq.equalsIgnoreCase(insertData.imgSeq)) {
                templateDelList.remove(delImage);
            }
        }
    }
}
