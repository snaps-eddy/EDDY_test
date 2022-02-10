package com.snaps.mobile.activity.common.products.base;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsAnimationListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.DialogSmartSnapsProgress;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies.SmartRecommendBookAnimationBridge;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2018. 2. 13..
 */

public class SmartRecommendBookEditorSmartSnapsHandler implements SmartSnapsAnimationListener {
    private static final String TAG = SmartRecommendBookEditorSmartSnapsHandler.class.getSimpleName();

    private SmartRecommendBookAnimationBridge analysisProductAnimationBridge = null;

    private DialogSmartSnapsProgress dialogSmartSnapsProgress;

    public static SmartRecommendBookEditorSmartSnapsHandler createInstanceWithBaseHandler(SmartRecommendBookAnimationBridge analysisProductAnimationBridge) {
        return new SmartRecommendBookEditorSmartSnapsHandler(analysisProductAnimationBridge);
    }

    private SmartRecommendBookEditorSmartSnapsHandler(SmartRecommendBookAnimationBridge analysisProductAnimationBridge) {
        this.analysisProductAnimationBridge = analysisProductAnimationBridge;
        this.dialogSmartSnapsProgress = new DialogSmartSnapsProgress(analysisProductAnimationBridge.getActivity());
    }

//    private void changeAllAnimationStateReadyToNone() {
//        try {
//            SnapsTemplate snapsTemplate = null;
//            if (analysisProductAnimationBridge != null)
//                snapsTemplate = analysisProductAnimationBridge.getTemplate();
//
//            ArrayList<MyPhotoSelectImageData> imageDataList = PhotobookCommonUtils.getImageListFromTemplate(snapsTemplate);
//            for (MyPhotoSelectImageData imageData : imageDataList) {
//                if (imageData == null) continue;
//                SmartSnapsImgInfo smartSnapsImgInfo = imageData.getSmartSnapsImgInfo();
//                if (smartSnapsImgInfo == null || smartSnapsImgInfo.getSmartSnapsImgState() != SmartSnapsConstants.eSmartSnapsImgState.READY) return;
//                SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(imageData, SmartSnapsConstants.eSmartSnapsImgState.NONE);
//            }
//        } catch (Exception e) { Dlog.e(TAG, e); }
//    }

    private void dismissSmartSnapsPageProgress() {
        if (analysisProductAnimationBridge == null) return;
        try {
            if (dialogSmartSnapsProgress != null)
                dialogSmartSnapsProgress.dismiss();

        } catch (Exception e) {
            SnapsAssert.assertException(e);
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onSmartSnapsAnimationStart(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        showSmartSnapsProgressWithType(progressType);
    }

    private void showSmartSnapsProgressWithType(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        if (analysisProductAnimationBridge == null) return;
        try {
            if (dialogSmartSnapsProgress != null) {
                switch (progressType) {
                    default:
                        dialogSmartSnapsProgress.setNormalLoadMode();
                        break;
                }
                if (!dialogSmartSnapsProgress.isShowing())
                    dialogSmartSnapsProgress.show();
            }
        } catch (Exception e) {
            SnapsAssert.assertException(e);
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onSmartSnapsAnimationUpdateProgress(SmartSnapsConstants.eSmartSnapsProgressType progressType, int totalCount, int finishCount) {}

    @Override
    public void onSmartSnapsAnimationFinish(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        switch (progressType) {
            default:
                initSmartSnapsTargets();
                dismissSmartSnapsPageProgress();
                break;
        }
    }

    private void initSmartSnapsTargets() {
        try {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.initSmartSnapsTargets();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void requestSmartAnimationWithPage(int page) {}

    @Override
    public void requestRefreshPageThumbnail(int page) {}

    @Override
    public void requestAnimation(MyPhotoSelectImageData imageData) {
        try {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.requestSmartImgAnimation(imageData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(e);
        }
    }

    @Override
    public void onOccurredException(Exception e) {
        Dlog.e(TAG, e);
    }
}
