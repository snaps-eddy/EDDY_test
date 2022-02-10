package com.snaps.mobile.activity.common.products.base;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsAnimationListener;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditControls;
import com.snaps.mobile.activity.edit.view.DialogSmartSnapsProgress;
import com.snaps.mobile.activity.edit.view.SnapsClippingDimLayout;
import com.snaps.mobile.activity.themebook.OrientationChecker;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsAnimationHandler;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.text_animation.HTextView;
import com.snaps.mobile.utils.text_animation.HTextViewType;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import errorhandle.logger.Logg;
import font.FTextView;

/**
 * Created by ysjeong on 2018. 2. 13..
 */

public class SnapsProductEditorSmartSnapsHandler implements SmartSnapsAnimationListener {
    private static final String TAG = SnapsProductEditorSmartSnapsHandler.class.getSimpleName();

    private SnapsProductBaseEditorHandler baseEditorHandler = null;

    public static SnapsProductEditorSmartSnapsHandler createInstanceWithBaseHandler(SnapsProductBaseEditorHandler baseEditorHandler) {
        return new SnapsProductEditorSmartSnapsHandler(baseEditorHandler);
    }

    private SnapsProductEditorSmartSnapsHandler(SnapsProductBaseEditorHandler baseEditorHandler) {
        this.baseEditorHandler = baseEditorHandler;
    }

    private SnapsProductEditControls getEditControls() throws Exception {
        return baseEditorHandler.getEditControls();
    }

    private Activity getActivity() throws Exception {
        return baseEditorHandler.getActivity();
    }

    public SnapsProductBaseEditorHandler getBaseEditorHandler() throws Exception {
        return baseEditorHandler;
    }

    DialogSmartSnapsProgress createBaseSmartSnapsProgressDialog() throws Exception {
        DialogSmartSnapsProgress dialogSmartSnapsProgress = new DialogSmartSnapsProgress(getActivity());

        getEditControls().setSmartSnapsSearchProgressBar((ProgressBar) dialogSmartSnapsProgress.findViewById(R.id.dialog_smart_snaps_progress_progressBar));
        getEditControls().setSmartSnapsSearchProgressValueText((FTextView) dialogSmartSnapsProgress.findViewById(R.id.dialog_smart_snaps_progress_value_tv));
        getEditControls().setSmartSnapsSearchProgressTitleText((HTextView) dialogSmartSnapsProgress.findViewById(R.id.dialog_smart_snaps_progress_title_tv));
        getEditControls().setSmartSnapsSearchProgressDimLayout((SnapsClippingDimLayout) dialogSmartSnapsProgress.findViewById(R.id.dialog_smart_snaps_progress_dim_layout));

        FTextView suspendView = (FTextView) dialogSmartSnapsProgress.findViewById(R.id.dialog_smart_snaps_progress_suspend_btn);
        View suspendViewLayout = (View) dialogSmartSnapsProgress.findViewById(R.id.dialog_smart_snaps_progress_suspend_ly);
        getEditControls().setSmartSnapsSearchCancelText(suspendView);
        if (suspendView != null) {
            suspendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSmartSnapsSearchSuspendConfirmDialog();
                }
            });
        }

        if (suspendViewLayout != null) {
            suspendViewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSmartSnapsSearchSuspendConfirmDialog();
                }
            });
        }

        return dialogSmartSnapsProgress;
    }

    private void showSmartSnapsSearchSuspendConfirmDialog() {
        CustomizeDialog smartSearchingCancelConfirmDialog = null;
        try {
            smartSearchingCancelConfirmDialog = MessageUtil.alertnoTitleTwoBtn(getActivity(), getActivity().getString(R.string.confirm_cancel_smart_searching), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    smartSnapsManager.setSmartSearchingCancelConfirmDialog(null);
                    if (clickedOk == ICustomDialogListener.OK) {
                        handleSmartSnapsSearchSuspend();
                    }
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.setSmartSearchingCancelConfirmDialog(smartSearchingCancelConfirmDialog);
    }

    private void handleSmartSnapsSearchSuspend() {
        if (SmartSnapsManager.isSmartAreaSearching()) {
            try {
                suspendSmartSnapsFaceSearching();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            showSmartSnapsSuspendedAlert();
        }
    }

    private void showSmartSnapsSuspendedAlert() {
        try {
            MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.smart_snaps_search_suspend_alert), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    try {
                        handleFirstSmartSnapsAnimationComplete();
                        handleOnSmartSnapsAnimationSuspended();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    void suspendSmartSnapsFaceSearching() throws Exception {
        SmartSnapsManager.suspendSmartSnapsFaceSearching();

        hideDimView();

        getBaseEditorHandler().refreshUI();
    }

    void handleFirstSmartSnapsAnimationComplete() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (!smartSnapsManager.isFirstSmartSearching()) return;

        try {
            SmartSnapsAnimationHandler animationHandler = smartSnapsManager.getSmartSnapsAnimationHandler();
            if (animationHandler != null) animationHandler.initProgressType();

            getBaseEditorHandler().refreshUI();

            smartSnapsManager.dismissSmartSearchingCancelConfirmDialog();

            smartSnapsManager.setScreenRotationLock(false);

            getBaseEditorHandler().setPageCurrentItem(0, false);

            SnapsProductBaseEditor baseEditor = getBaseEditorHandler().getEditorBase();
            baseEditor.onFinishedFirstSmartSnapsAnimation();
            dismissSmartSnapsPageProgress();

            baseEditor.exportAutoSaveTemplate();

//            if (android.provider.Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
//                OrientationChecker orientationChecker = OrientationManager.getInstance(getActivity()).getOrientationChecker();
//                orientationChecker.setPrevOrientation(Configuration.ORIENTATION_PORTRAIT);
//                getBaseEditorHandler().handleNotifyPortraitOrientation();
//            } else {
//                getBaseEditorHandler().handleNotifyOrientationState();
//            }
            if (OrientationSensorManager.isActiveAutoRotation(getActivity())) {
                getBaseEditorHandler().handleNotifyOrientationState();
            } else {
                OrientationChecker orientationChecker = OrientationManager.getInstance(getActivity()).getOrientationChecker();
                orientationChecker.setPrevOrientation(Configuration.ORIENTATION_PORTRAIT);
                getBaseEditorHandler().handleNotifyPortraitOrientation();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        removeAllSmartSnapsSearchingProgress();
    }

    private void changeAllAnimationStateReadyToNone() {
        try {
            ArrayList<MyPhotoSelectImageData> imageDataList = PhotobookCommonUtils.getImageListFromTemplate(getBaseEditorHandler().getSnapsTemplate());
            for (MyPhotoSelectImageData imageData : imageDataList) {
                if (imageData == null) continue;
                SmartSnapsImgInfo smartSnapsImgInfo = imageData.getSmartSnapsImgInfo();
                if (smartSnapsImgInfo == null || smartSnapsImgInfo.getSmartSnapsImgState() != SmartSnapsConstants.eSmartSnapsImgState.READY)
                    return;
                SmartSnapsUtil.changeSmartSnapsImgStateWithImageData(imageData, SmartSnapsConstants.eSmartSnapsImgState.NONE);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void dismissSmartSnapsPageProgress() {
        try {
            DialogSmartSnapsProgress smartSnapsProgress = getEditControls().getSmartSnapsProgress();
            if (smartSnapsProgress != null)
                smartSnapsProgress.dismiss();

            hideDimView();
        } catch (Exception e) {
            SnapsAssert.assertException(e);
            Dlog.e(TAG, e);
        }
    }

    private void removeAllSmartSnapsSearchingProgress() {
        try {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.removeAllSmartSnapsSearchingProgress();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onSmartSnapsAnimationStart(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        Dlog.d("onSmartSnapsAnimationStart()");
        showSmartSnapsProgressWithType(progressType);
    }

    private void showSmartSnapsProgressWithType(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        try {
            DialogSmartSnapsProgress smartSnapsProgress = getEditControls().getSmartSnapsProgress();
            switch (progressType) {
                case FIST_LOAD:
                    initSmartSearchProgress();
                    smartSnapsProgress.setFirstLoadMode();
                    break;
                default:
                    smartSnapsProgress.setNormalLoadMode();
                    break;
            }
            if (!smartSnapsProgress.isShowing())
                smartSnapsProgress.show();

        } catch (Exception e) {
            SnapsAssert.assertException(e);
            Dlog.e(TAG, e);
        }
    }

    private void initSmartSearchProgress() throws Exception {
        if (getEditControls() == null) return;

        if (getEditControls().getSmartSnapsSearchProgressBar() != null) {
            getEditControls().getSmartSnapsSearchProgressBar().setProgress(0);
        }

        if (getEditControls().getSmartSnapsSearchProgressValueText() != null) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            setSmartSnapsSearchProgressValueText(0, smartSnapsManager.getSmartSnapsTaskTotalCount());
        }

        SmartSnapsUtil.setSmartSnapsProgressClipArea(getActivity(), getEditControls());

        showDimView();
    }

    private void showDimView() {
        if (SmartSnapsUtil.isOmitDimUIProduct()) return;
        try {
            View dimView = getDimView();
            if (dimView != null)
                dimView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void hideDimView() throws Exception {
        try {
            View dimView = getDimView();
            if (dimView != null)
                dimView.setVisibility(View.GONE);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private View getDimView() throws Exception {
        View dimView = null;
        OrientationManager orientationManager = OrientationManager.getInstance(getActivity());
        if (orientationManager.isLandScapeMode()) {
            dimView = getActivity().findViewById(R.id.activity_edit_themebook_thumbnail_recyclerview_h_dim_view);
        } else {
            dimView = getActivity().findViewById(R.id.activity_edit_themebook_thumbnail_recyclerview_v_dim_view);
        }
        return dimView;
    }

    @Override
    public void onSmartSnapsAnimationUpdateProgress(SmartSnapsConstants.eSmartSnapsProgressType progressType, int totalCount, int finishCount) {
        switch (progressType) {
            case FIST_LOAD:
                try {
                    updateSmartSnapsProgressBar(totalCount, finishCount);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                break;
        }
    }

    private void updateSmartSnapsProgressBar(int totalCount, int finishedCnt) throws Exception {
        if (getEditControls() == null) return;

        if (getEditControls().getSmartSnapsSearchProgressBar() != null) {
            float progressValue = finishedCnt / (float) totalCount;
            getEditControls().getSmartSnapsSearchProgressBar().setProgress((int) (progressValue * 100));
            Dlog.d("updateSmartSnapsProgressBar() progress:" + ((int) (progressValue * 100)));
        }

        setSmartSnapsSearchProgressValueText(finishedCnt, totalCount);
    }

    private void setSmartSnapsSearchProgressValueText(int currentValue, int totalValue) throws Exception {
        int textResId = 0;
        boolean isCompleted = false;
        if (currentValue <= 0) {
            textResId = R.string.smart_upload_ready_title_text;
        } else {
            isCompleted = currentValue == totalValue;
            textResId = isCompleted ? R.string.smart_uploading_completed_title_text : R.string.smart_uploading_title_text;
        }

        HTextView titleTextView = getEditControls().getSmartSnapsSearchProgressTitleText();
        if (titleTextView != null) {
            String progressText = getActivity().getString(textResId);
            if (StringUtil.isEmpty(titleTextView.getText().toString()) || !progressText.equalsIgnoreCase(titleTextView.getText().toString())) {
                titleTextView.setAnimateType(HTextViewType.FALL);
                titleTextView.animateText(getActivity().getString(textResId));
            }
        }

        String valueText = String.format(getActivity().getString(R.string.smart_uploading_title_value_format), currentValue, totalValue);
        int startIdx = valueText.indexOf("(") + 1;
        int endIdx = valueText.indexOf("/");
        FTextView valueTextView = getEditControls().getSmartSnapsSearchProgressValueText();
        valueTextView.setText("");
        final SpannableStringBuilder sp = new SpannableStringBuilder(valueText);
        sp.setSpan(new ForegroundColorSpan(Color.argb(255, 227, 106, 99)), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        valueTextView.append(sp);

        if (isCompleted) {
            FTextView cancelText = getEditControls().getSmartSnapsSearchCancelText();
            cancelText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSmartSnapsAnimationFinish(SmartSnapsConstants.eSmartSnapsProgressType progressType) {
        Dlog.d("onSmartSnapsAnimationFinish()");
        switch (progressType) {
            case FIST_LOAD:
                try {
                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    smartSnapsManager.dismissSmartSearchingCancelConfirmDialog();

                    showSmartSearchCompletedDialog();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    initSmartSnapsTargets();
                    handleFirstSmartSnapsAnimationComplete();
                    handleOnSmartSnapsAnimationSuspended();
                }
                break;
            default:
                initSmartSnapsTargets();
                dismissSmartSnapsPageProgress();
                break;
        }
    }

    void handleOnSmartSnapsAnimationSuspended() {
        changeAllAnimationStateReadyToNone();

        SnapsOrderManager.uploadThumbImgListOnBackground();
    }

    private void showSmartSearchCompletedDialog() throws Exception {
        final CustomizeDialog customizeDialog = new CustomizeDialog(getActivity());

        customizeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customizeDialog.setContentView(R.layout.smart_snaps_search_completed_dialog);

        View confirmBtn = customizeDialog.findViewById(R.id.custom_dialog_confirm_btn);
        if (confirmBtn != null) {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customizeDialog.dismiss();
                    initSmartSnapsTargets();
                    handleFirstSmartSnapsAnimationComplete();

                    SnapsOrderManager.uploadOrgImgOnBackground();
                }
            });
        }

        TextView text = (TextView) customizeDialog.findViewById(R.id.smart_snaps_search_completed_dialog_text_tv);
        if (text != null) {
            text.setText(SmartSnapsManager.isSmartImageSelectType() ? R.string.smart_snaps_search_completed_popup_contents : R.string.normal_select_smart_snaps_search_completed_popup_contents);
        }

        ImageView icon = (ImageView) customizeDialog.findViewById(R.id.smart_snaps_search_completed_dialog_icon_iv);
        if (icon != null) {
            icon.setImageResource(SmartSnapsManager.isSmartImageSelectType() ? R.drawable.img_smart_pop_finish_icon : R.drawable.img_general_pop_finish_icon);
        }

        customizeDialog.setCanceledOnTouchOutside(false);
        customizeDialog.setCancelable(false);
        customizeDialog.show();
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
    public void requestSmartAnimationWithPage(int page) {
        try {
            getBaseEditorHandler().setPageCurrentItem(page, true);

            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.handleSmartSnapsAnimationOnPage(page);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(e);
        }
    }

    @Override
    public void requestRefreshPageThumbnail(int page) {
        try {
            getBaseEditorHandler().handleBaseRefreshPageThumbnail(page);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void requestAnimation(MyPhotoSelectImageData imageData) {
        try {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            smartSnapsManager.requestSmartImgAnimation(imageData);

            if (SmartSnapsManager.isFirstSmartAreaSearching())
                smartSnapsManager.requestSmartThumbImgAnimation(imageData);
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
