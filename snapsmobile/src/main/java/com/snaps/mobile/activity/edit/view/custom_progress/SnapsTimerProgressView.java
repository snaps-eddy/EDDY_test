package com.snaps.mobile.activity.edit.view.custom_progress;

import android.app.Activity;
import android.os.Looper;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate.SnapsTimerProgressProjectUploadCalculator;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate.SnapsTimerProgressTasksCalculator;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsTimerProgressException;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressView {
    private static final String TAG = SnapsTimerProgressView.class.getSimpleName();
    private static volatile SnapsTimerProgressView gInstance = null;

    private SnapsTimerProgressViewFactory.eTimerProgressType currentProgressType = null;

    private SnapsProgressViewAPI snapsProgressViewAPI = null;

    private SnapsTimerProgressView() {}

    public static void createInstance() {
        if (gInstance ==  null) {
            synchronized (SnapsTimerProgressView.class) {
                if (gInstance ==  null) {
                    gInstance = new SnapsTimerProgressView();
                }
            }
        }
    }

    public static void destroyProgressView() {
        if (gInstance ==  null) return;

        try {
            gInstance.hideProgress();

            if (gInstance.snapsProgressViewAPI != null)
                gInstance.snapsProgressViewAPI.releaseInstance();

            gInstance = null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static SnapsTimerProgressView getInstance() {
        if(gInstance ==  null)
            createInstance();

        return gInstance;
    }

    public static void showProgress(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        SnapsTimerProgressView.showProgress(activity, progressType, "");
    }

    public static void showProgress(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType, String msg) {
        try {
            if (Looper.myLooper() != Looper.getMainLooper()) return;

            SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
            SnapsProgressViewAPI progressAPI = snapsTimerProgressView.initProgressAPI(activity, progressType);
            if (progressAPI != null)
                progressAPI.showProgress();

            SnapsTimerProgressView.setMessage(msg);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private SnapsProgressViewAPI initProgressAPI(Activity activity, SnapsTimerProgressViewFactory.eTimerProgressType progressType) {
        try {
            currentProgressType = progressType;
            snapsProgressViewAPI = SnapsTimerProgressViewFactory.createProgressView(activity, progressType);
        } catch (SnapsTimerProgressException e) {
            Dlog.e(TAG, e);
        }

        return snapsProgressViewAPI;
    }

    public static void setMessage(String message) {
        try {
            SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
            snapsTimerProgressView.snapsProgressViewAPI.setMessage(message);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private void hideProgress() {
        try {
            if (snapsProgressViewAPI != null)
                snapsProgressViewAPI.hideProgress();
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    /**
     * ??????????????? ????????? ??? ??? Task??? ?????? ??? ?????????, ?????? ??? ????????? ??????.
     */
    public static void completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType taskType) {
        SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
        if (snapsTimerProgressView.currentProgressType != SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_UPLOAD) return;
        try {
            SnapsProgressViewAPI progressViewAPI = snapsTimerProgressView.snapsProgressViewAPI;
            SnapsTimerProgressProjectUploadCalculator progressCalculator = (SnapsTimerProgressProjectUploadCalculator) progressViewAPI.getProgressCalculator();
            progressCalculator.updateProgressValue(taskType, 100);
            progressCalculator.updateEstimatedTime(taskType, 0);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    /**
     * ?????? ????????? ???????????? ???????????? ?????? ???????????? ????????? ???????????? ?????? ??? ????????? ??????.
     */
    public static void initUploadImageList(ArrayList<MyPhotoSelectImageData> uploadImageList) {
        SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
        if (snapsTimerProgressView.currentProgressType != SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_UPLOAD) return;
        try {
            SnapsProgressViewAPI progressViewAPI = snapsTimerProgressView.snapsProgressViewAPI;
            SnapsTimerProgressProjectUploadCalculator progressCalculator = (SnapsTimerProgressProjectUploadCalculator) progressViewAPI.getProgressCalculator();
            progressCalculator.initOrgImgUploadCalculator(uploadImageList);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    /**
     * ??? ?????????????????? ????????? ?????? ?????? ?????? ????????? ????????? ??????.
     */
    public static void setOffsetForMeasureImageData(MyPhotoSelectImageData MyPhotoSelectImageData) {
        SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
        if (snapsTimerProgressView.currentProgressType != SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_UPLOAD) return;
        try {
            SnapsProgressViewAPI progressViewAPI = snapsTimerProgressView.snapsProgressViewAPI;
            if (progressViewAPI != null) {
                SnapsTimerProgressProjectUploadCalculator progressCalculator = (SnapsTimerProgressProjectUploadCalculator) progressViewAPI.getProgressCalculator();
                progressCalculator.setOffsetForMeasureImageData(MyPhotoSelectImageData);
            }
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    /**
     * ??? ?????????????????? ????????? ?????? ????????? ?????? ???????????? ??????.
     */
    public static void updateOrgImgUploadProgressValueWithEstimateTime(MyPhotoSelectImageData imageData) {
        SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
        if (snapsTimerProgressView.currentProgressType != SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_UPLOAD) return;
        try {
            SnapsProgressViewAPI progressViewAPI = snapsTimerProgressView.snapsProgressViewAPI;
            SnapsTimerProgressProjectUploadCalculator progressCalculator = (SnapsTimerProgressProjectUploadCalculator) progressViewAPI.getProgressCalculator();

            progressCalculator.addUploadedImageCount();

            progressCalculator.updateOrgImgUploadProgressValue();

            progressCalculator.updateOrgImgUploadEstimateTime(imageData);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    /**
     * PROGRESS_TYPE_TASKS?????? 100??? ???????????? ?????? ????????? ??? ????????? ????????????.
     */
    public static void updateTasksProgressValue(float progressValue) {
        SnapsTimerProgressView snapsTimerProgressView = SnapsTimerProgressView.getInstance();
        if (snapsTimerProgressView.currentProgressType != SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_TASKS) return;
        try {
            SnapsProgressViewAPI progressViewAPI = snapsTimerProgressView.snapsProgressViewAPI;
            SnapsTimerProgressTasksCalculator progressCalculator = (SnapsTimerProgressTasksCalculator) progressViewAPI.getProgressCalculator();
            progressCalculator.updateProgressValue(progressValue);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }
}
