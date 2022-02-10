package com.snaps.mobile.order.order_v2.util.handler;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;

import java.util.ArrayList;
import java.util.List;

import errorhandle.SnapsAssert;

/**
 * Created by ysjeong on 2017. 12. 21..
 */

public class SnapsOrderDefaultHandler extends SnapsOrderBaseHandler {
    private static final String TAG = SnapsOrderDefaultHandler.class.getSimpleName();

    public static SnapsOrderBaseHandler createInstance(@NonNull Activity activity, @NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        return new SnapsOrderDefaultHandler(activity, snapsOrderActivityBridge);
    }

    private SnapsOrderDefaultHandler(@NonNull Activity activity, @NonNull SnapsOrderActivityBridge snapsOrderActivityBridge) throws Exception {
        super(activity, snapsOrderActivityBridge);
    }

    @Override
    protected void startUploadProcess() {
        performMakeThumbnailFile();
    }

    /**
     * 메인 썸네일을 생성하는 부분인데, 실패해도 그냥 넘어간다. (웹팀에서 디폴트 이미지를 보여주기로 협의 되었다.)
     */
    @Override
    protected void performMakeMainPageThumbnailFile() {
        Dlog.d("performMakeMainPageThumbnailFile() start make pageThumbanil");

        try {
            setLockAfterMakeThumbnail(false);

            //간혹 썸네일을 만들다가, 프로그래스가 중지 된다는 CS 인입이 되어서 썸네일 만드는 작업에 대해 어느 정도까지 기다리다, 응답이 없으면 실패로 간주하고 그냥 넘어간다.
            startMainPageThumbnailMakingCheck();

            getSnapsOrderTaskHandler().requestMakeMainPageThumbnailFile(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performMakeMainPageThumbnailFile() onSnapsOrderResultSucceed()");
                    if (!isOverWaitTimeForPageThumbnailMake()) {
                        suspendPageThumbnailMakeCheck();
                        performUploadMainThumbnail();
                    }
                    //만약, 기다리는 시간이 초과 되었다면 onOverWaitTime()가 호출되어, 다음 Task로 넘어가게 된다.
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performMakeMainPageThumbnailFile() onSnapsOrderResultFailed()");
                    if (!isOverWaitTimeForPageThumbnailMake()) {
                        suspendPageThumbnailMakeCheck();
                        performUploadOrgImages(); //생성에 실패하면 바로 원본 이미지 업로드 하는 단계로 건너 뛴다.
                    }
                    //만약, 기다리는 시간이 초과 되었다면 onOverWaitTime()가 호출되어, 다음 Task로 넘어가게 된다.
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
            performUploadOrgImages(); //생성에 실패하면 바로 원본 이미지 업로드 하는 단계로 건너 뛴다.
        }
    }

    @Override
    protected void performUploadMainThumbnail() {
        Dlog.d("performUploadMainThumbnail() start upload Main Thumbnail");
        try {
            getSnapsOrderTaskHandler().performUploadMainThumbnail(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performUploadMainThumbnail() onSnapsOrderResultSucceed()");
                    SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_MAIN_THUMBNAIL);
                    performUploadOrgImages();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performUploadMainThumbnail() onSnapsOrderResultFailed()");
                    performUploadOrgImages(); //페이지 썸네일을 못 올려도 그냥 넘어간다...
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
            performUploadOrgImages(); //페이지 썸네일을 못 올려도 그냥 넘어간다...
        }
    }

    @Override
    protected void performUploadOrgImages() {
        Dlog.d("performUploadOrgImages() start upload org image");
        if (isLockAfterMakeThumbnail()) return;
        setLockAfterMakeThumbnail(true);

        try {
            getSnapsOrderTaskHandler().performUploadOrgImages(new SnapsOrderResultListener() {
                @Override
                public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                    Dlog.d("performUploadOrgImages() onSnapsOrderResultSucceed()");
                    SnapsTimerProgressView.completeUploadProgressTask(SnapsProgressViewAPI.eTimerProgressTaskType.TASK_TYPE_UPLOAD_ORG_IMG);
                    try {
                        deleteNotContainDELImages();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        SnapsAssert.assertException(getActivity(), e);
                    }

                    performMakeXML();
                }

                @Override
                public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                    Dlog.w(TAG, "performUploadMainThumbnail() onSnapsOrderResultFailed()");
                    getProjectUploadResultListener().onSnapsOrderResultFailed(resultMsgObj, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE);
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (getProjectUploadResultListener() != null) getProjectUploadResultListener().onSnapsOrderResultFailed(null, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_ORG_IMAGE);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private void deleteNotContainDELImages() throws Exception {
        if (getSnapsOrderTaskHandler() == null) return;

        SnapsOrderAttribute attribute = getSnapsOrderTaskHandler().getAttribute();
        if (attribute == null) return;

        SnapsTemplate snapsTemplate = attribute.getSnapsTemplate();
        if (snapsTemplate == null || snapsTemplate.delimgList == null || snapsTemplate.getPages() == null) return;

        ArrayList<SnapsDelImage> definitiveDelImageList = new ArrayList<>();
        for (SnapsPage snapsPage : snapsTemplate.getPages()) {
            if (snapsPage == null) continue;
            List<MyPhotoSelectImageData> imageDataList = snapsPage.getImageDataListOnPage();
            if (imageDataList == null) continue;

            for (MyPhotoSelectImageData imageData : imageDataList) {
                if (imageData == null) continue;

                final String imageSEQ = imageData.F_IMG_SQNC;
                if (!StringUtil.isEmpty(imageSEQ)) {
                    ArrayList<SnapsDelImage> delImages = snapsTemplate.delimgList;
                    for (SnapsDelImage delImage : delImages) {
                        if (delImage == null) continue;

                        final String delImageSEQ = delImage.imgSeq;
                        if (imageSEQ.equalsIgnoreCase(delImageSEQ)) {
                            definitiveDelImageList.add(delImage);
                            break;
                        }
                    }
                }
            }
        }

        snapsTemplate.delimgList.clear();
        snapsTemplate.delimgList = null;
        snapsTemplate.delimgList = definitiveDelImageList;
    }

    @Override
    protected void onOverWaitTimeMakePageThumbnail() {
        performUploadOrgImages(); //썸네일 생성에 실패하면 그냥 넘어간다...
    }
}
