package com.snaps.mobile.order.order_v2.util.handler;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;

import errorhandle.SnapsAssert;

/**
 * Created by ysjeong on 2017. 12. 21..
 */

public class SnapsOrderDiaryHandler extends SnapsOrderBaseHandler {
    private static final String TAG = SnapsOrderDiaryHandler.class.getSimpleName();

    public static SnapsOrderBaseHandler createInstance(@NonNull Activity activity, @NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        return new SnapsOrderDiaryHandler(activity, snapsOrderActivityBridge);
    }

    private SnapsOrderDiaryHandler(@NonNull Activity activity, @NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        super(activity, snapsOrderActivityBridge);
    }

    @Override
    protected void startUploadProcess() {
        performUploadOrgImages();
    }

    @Override
    protected void performUploadOrgImages() {
        Dlog.d("performUploadOrgImages() start upload org image");

        try {
            getSnapsOrderTaskHandler().performUploadOrgImages(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performUploadOrgImages() onSnapsOrderResultSucceed()");
                    SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_ORG_IMG);
                    performMakeThumbnailFile();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performUploadOrgImages() onSnapsOrderResultFailed()");
                    getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (getProjectUploadResultListener() != null) getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    /**
     * ?????? ???????????? ???????????? ????????????, ???????????? ?????? ????????????. (???????????? ????????? ???????????? ??????????????? ?????? ?????????.)
     */
    @Override
    protected void performMakeMainPageThumbnailFile() {
        Dlog.d("performMakeMainPageThumbnailFile() start make pageThumbnail");

        try {
            //?????? ???????????? ????????????, ?????????????????? ?????? ????????? CS ????????? ????????? ????????? ????????? ????????? ?????? ?????? ???????????? ????????????, ????????? ????????? ????????? ???????????? ?????? ????????????.
            startMainPageThumbnailMakingCheck();

            getSnapsOrderTaskHandler().requestMakeMainPageThumbnailFile(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performMakeMainPageThumbnailFile() onSnapsOrderResultSucceed()");
                    if (!isOverWaitTimeForPageThumbnailMake()) {
                        suspendPageThumbnailMakeCheck();
                        performUploadMainThumbnail();
                    }
                    //??????, ???????????? ????????? ?????? ???????????? onOverWaitTime()??? ????????????, ?????? ?????? ???
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performMakeMainPageThumbnailFile() onSnapsOrderResultFailed()");
                    if (!isOverWaitTimeForPageThumbnailMake()) {
                        suspendPageThumbnailMakeCheck();
                        getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_PAGE_THUMBNAILS);
                    }
                    //??????, ???????????? ????????? ?????? ???????????? onOverWaitTime()??? ????????????, ?????? ?????? ???
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
            getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_PAGE_THUMBNAILS);
        }
    }

    @Override
    protected void performUploadMainThumbnail() {
        Dlog.d("performUploadMainThumbnail() start upload Main Thumbnail");
        try {
            if (isLockAfterMakeThumbnail()) return;
            setLockAfterMakeThumbnail(true);

            getSnapsOrderTaskHandler().performUploadMainThumbnail(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performUploadMainThumbnail() onSnapsOrderResultSucceed()");
                    SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_MAIN_THUMBNAIL);
                    performMakeXML();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performUploadMainThumbnail() onSnapsOrderResultFailed()");
                    getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
            getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_MAIN_THUMBNAIL);
        }
    }

    @Override
    protected void onOverWaitTimeMakePageThumbnail() {
        if (getProjectUploadResultListener() != null)
            getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_PAGE_THUMBNAILS);
    }
}
