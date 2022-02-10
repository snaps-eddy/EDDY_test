package com.snaps.mobile.activity.common.handler;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SmartRecommendBookHandlerInstance;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragmentFactory;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

public class SmartRecommendBookCaptureHandler {
    private static final String TAG = SmartRecommendBookCaptureHandler.class.getSimpleName();

    private SnapsEditActExternalConnectionBridge actExternalConnectionBridge = null;
    private ISnapsCaptureListener snapsPageCaptureListener = null;
    private SnapsCanvasFragment canvasFragment = null;

    public static SmartRecommendBookCaptureHandler createHandlerWithInstance(SmartRecommendBookHandlerInstance instance) {
        SmartRecommendBookCaptureHandler templateHandler = new SmartRecommendBookCaptureHandler();
        templateHandler.actExternalConnectionBridge = instance.getExternalConnectionBridge();
        return templateHandler;
    }

    public void setPageThumbnailFail(int index) {
        if (snapsPageCaptureListener != null)
            snapsPageCaptureListener.onFinishPageCapture(false);
    }

    public void setPageThumbnail(int pageIdx, String filePath) {
        if (actExternalConnectionBridge == null) return;

        if (actExternalConnectionBridge.getPageList() == null || actExternalConnectionBridge.getTemplate() == null || actExternalConnectionBridge.getTemplate().getPages() == null) {
            SnapsTimerProgressView.destroyProgressView();
            if (snapsPageCaptureListener != null)
                snapsPageCaptureListener.onFinishPageCapture(false);
            return;
        }

        if (PhotobookCommonUtils.isAlreadyCreatedThumbnail(actExternalConnectionBridge.getPageList(), pageIdx)) {
            if (snapsPageCaptureListener != null)
                snapsPageCaptureListener.onFinishPageCapture(true);
            return;
        }

        //페이지 썸네일 한번 딴 것은 다시 따지 않는다.
        PhotobookCommonUtils.changePageThumbnailState(actExternalConnectionBridge.getPageList(), pageIdx, true);

        try {
            if (canvasFragment == null) {
                createCanvasFragment();
            }

            if (canvasFragment != null) {
                if (canvasFragment.getContext() == null) {
                    throw new Exception("canvasFragment.getContext()  is null");
                }

                canvasFragment.getArguments().putBoolean("pageSave", SnapsOrderManager.isUploadingProject()); //저장할때만 true
                canvasFragment.getArguments().putBoolean("pageLoad", false);
                canvasFragment.getArguments().putInt("index", 0);
                canvasFragment.getArguments().putBoolean("visibleButton", false);
                canvasFragment.getArguments().putBoolean("preThumbnail", true);
                canvasFragment.makeSnapsCanvas();
            } else {
                if (snapsPageCaptureListener != null)
                    snapsPageCaptureListener.onFinishPageCapture(false);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsTimerProgressView.destroyProgressView();
            MessageUtil.toast(getActivity(), getActivity().getString(R.string.refresh_screen_error_msg));//"화면을 갱신하는 중 오류가 발생 했습니다.");

            if (snapsPageCaptureListener != null)
                snapsPageCaptureListener.onFinishPageCapture(false);
        }
    }

    public void setSnapsPageCaptureListener(ISnapsCaptureListener snapsPageCaptureListener) {
        this.snapsPageCaptureListener = snapsPageCaptureListener;
    }

    public void createCanvasFragment() {
        //대표 썸네일 만드는 과정도 생략한다.
        Bundle bundle = new Bundle();
        bundle.putInt("index", 0);
        bundle.putBoolean("pageSave", false);
        bundle.putBoolean("pageLoad", false);
        bundle.putBoolean("preThumbnail", false);
        bundle.putBoolean("visibleButton", false);

        canvasFragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
        if (canvasFragment == null) return;

        canvasFragment.setArguments(bundle);
        FragmentUtil.replce(R.id.smart_snaps_analysis_product_capture_layout, (FragmentActivity) getActivity(), canvasFragment);
    }

    private Activity getActivity() {
        return actExternalConnectionBridge != null ? actExternalConnectionBridge.getActivity() : null;
    }
}
