package com.snaps.mobile.order.order_v2.task.prepare_process;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import com.snaps.common.snaps_image_proccesor.image_coordinate_processor.ImageCoordinateCalculator;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public abstract class SnapsOrderPrepareBaseHandler implements SnapsOrderConstants {
    private static final String TAG = SnapsOrderPrepareBaseHandler.class.getSimpleName();

    private SnapsOrderAttribute attribute = null;

    private SnapsOrderActivityBridge snapsOrderActivityBridge = null;

    private DialogInputNameFragment diagInput;

    private DialogConfirmFragment diagConfirm;

    protected SnapsOrderPrepareBaseHandler(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        this.setAttribute(attribute);
        this.setSnapsOrderActivityBridge(snapsOrderActivityBridge);
    }

    public void finalizeInstance() {
        attribute = null;

        snapsOrderActivityBridge = null;

        diagInput = null;

        diagConfirm = null;
    }

    //????????? ????????? ?????? ????????????
    protected abstract boolean isLackQuantity() throws Exception;

    //????????? ?????? ????????? ??????????????? ??????, ?????? ?????? ??????
    protected abstract SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute();

    //????????? ????????? ????????? ????????????
    protected abstract SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute();

    protected abstract void setTextControlBaseText() throws Exception;

    public abstract int performInspectOrderOptionAndGetResultCode() throws Exception; //?????????????????? ??????????????? ???????????? ?????? ????????? ????????????. (???????????? ????????? ?????? ?????? ??????)

    public abstract void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception; //??????????????? ????????????????????????? ????????? ????????? ?????? ????????????.

    public abstract void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener dialogConfirmClickListener) throws Exception; //?????? ?????? ??? ?????? ??????

    public void performSaveToBasket(@NonNull final DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
        SnapsOrderSaveToBasketAlertAttribute alertAttribute = createSaveToBasketAlertAttribute();
        showSaveToBasketAlert(alertAttribute, dialogInputNameClickListener);
    }

    public void setProductYN() {
        boolean isOk = inspectRequiredOrderCondition();
        getAttribute().getSnapsTemplate().setF_PRO_YORN(isOk ? "Y" : "N");
    }

    //????????? ????????????, ????????? ????????? ?????? ?????????
    private boolean inspectRequiredOrderCondition() {
        try {
            return !isExistEmptyImageControl() && !isLackQuantity();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return true;
    }

//    private boolean isExcludeInspectRequiredOrderConditionProduct() {
//        return Config.isPhotobooks() || Config.isSNSBook() || Config.isCalendar() || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isDIYStickerProduct();
//    }

    public void setOrderBaseInfo(final SnapsOrderResultListener baseInfoListener) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            boolean isSuccess = true;

            @Override
            public void onPre() {
                //??????????????? ??????????????? ??????????????? ?????? ????????? ????????? ????????????.
                try {
                    //????????? ????????? ??????
                    setAddPageInfo();

                    /**
                     * ???????????? ???????????? ??????????????? ??? ???????????? ????????? ?????? ????????? ??? ????????? ?????? ?????????,
                     * ????????? ????????? ???????????? ??? ????????? ??????????????? ?????? ??????, ???????????? ??? ????????? ??? ?????? ????????? ????????? ????????????.
                     */
                    setTextControlBaseText();

                    //????????? ?????? ?????????
                    setKeepScreenState(true);

                    //??????????????? m?????? ??????????????? ??????.
                    initProjUType();

                    //????????? ????????? ?????? ????????? ????????? ?????????
                    SnapsUploadFailedImageDataCollector.clearHistory(Config.getPROJ_CODE());
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    isSuccess = false;
                    SnapsAssert.assertException(getAttribute().getActivity(), e);
                }
            }

            @Override
            public void onBG() {
                //???????????? ????????? ???????????? ????????? ?????? ????????? UI ???????????? ???????????? ?????????.
                try {
                    setLayoutControlCoordinateInfo();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    isSuccess = false;
                    SnapsAssert.assertException(getAttribute().getActivity(), e);
                }
            }

            @Override
            public void onPost() {
                if (isSuccess) {
                    baseInfoListener.onSnapsOrderResultSucceed(null);
                } else {
                    baseInfoListener.onSnapsOrderResultFailed(null, eSnapsOrderType.ORDER_TYPE_PREPARATION);
                }
            }
        });
    }

    protected SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertAttribute() throws Exception {
        Dlog.d(Dlog.UI_MACRO, "DIALOG_SAVE");
        eSaveCartConditionCheckResult checkResult = checkConditionForSaveToBasket();
        if (checkResult == null) return null;

        switch (checkResult) {
            case SUCCESS:
                return createSaveConditionSuccessAlertAttribute();
            case FAILED_CAUSE_IS_LACK_QUANTITY:
                return createLackQuantityAlertAttribute();
            case FAILED_CAUSE_CONTAIN_LOW_RESOLUTION_PHOTO:
                return createLowResolutionAlertAttribute();
            case FAILED_CAUSE_EXIST_EMPTY_IMAGE_CONTROL:
                return createExistEmptyImageControlAlertAttribute();
        }

        return null;
    }

    protected SnapsOrderSaveToBasketAlertAttribute createSaveConditionSuccessAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttribute();
    }

    //?????? ?????? ????????? ?????????
    protected SnapsOrderSaveToBasketAlertAttribute createExistEmptyImageControlAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(R.string.photo_card_save_cart_blank_photo_alert_msg);
    }

    /**
     * 1.?????? ?????? ????????? ???????????????
     * 2.????????? ?????? ?????????
     * 3.???????????? ????????? ???????????? ?????????.
     */
    protected eSaveCartConditionCheckResult checkConditionForSaveToBasket() {
        try {
            if (isLackQuantity()) {
                return eSaveCartConditionCheckResult.FAILED_CAUSE_IS_LACK_QUANTITY;
            }

            if (isExistEmptyImageControl()) {
                return eSaveCartConditionCheckResult.FAILED_CAUSE_EXIST_EMPTY_IMAGE_CONTROL;
            }

            if (isExistLowResolutionPhoto()) {
                return eSaveCartConditionCheckResult.FAILED_CAUSE_CONTAIN_LOW_RESOLUTION_PHOTO;
            }

            return eSaveCartConditionCheckResult.SUCCESS;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return eSaveCartConditionCheckResult.EXCEPTION;
        }
    }

    protected boolean isExistEmptyImageControl() throws Exception {
        int completeEdit = findEmptyPageIdxWithPageList();
        return completeEdit != -1;
    }

    protected void setKeepScreenState(boolean isOn) throws Exception {
        if (attribute == null) return;
        Context context = attribute.getActivity();
        if (context == null || !(context instanceof Activity)) return;
        Activity activity = (Activity) context;
        if (activity.isFinishing()) return;
        SystemUtil.toggleScreen(activity.getWindow(), isOn);
    }

    protected void initProjUType() {
        if (getAttribute().isEditMode())
            Config.setPROJ_UTYPE("m");
    }

    protected void setAddPageInfo() throws Exception {
        //?????? ??? ?????? ?????? ???...?????? ????????? ?????? ?????????..
        if (Config.isPhotobooks() || Config.isSNSBook()) {
            if (!attribute.getSnapsTemplate().info.F_BASE_QUANTITY.equals("")) {
                int addPage = attribute.getPageList().size() - (Integer.parseInt(attribute.getSnapsTemplate().info.F_BASE_QUANTITY) + 2); // 17
                attribute.getSnapsTemplate().setF_ADD_PAGE(addPage);
                Dlog.d("setAddPageInfo() page:" + addPage);
            } else
                attribute.getSnapsTemplate().setF_ADD_PAGE(0);
        } else {
            attribute.getSnapsTemplate().setF_ADD_PAGE(0);
        }
    }

    public void setLayoutControlCoordinateInfo() throws Exception {
        if (attribute == null || attribute.getPageList() == null) return;
        ArrayList<SnapsPage> arrPageList = attribute.getPageList();
        for (SnapsPage snapsPage : arrPageList) {
            if (snapsPage == null) continue;

            //Images
            ArrayList<SnapsControl> arrLayers = snapsPage.getLayerLayouts();
            for (SnapsControl control : arrLayers) {
                if (control == null) continue;
                if (control instanceof SnapsLayoutControl) {
                    SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                    ImageCoordinateCalculator.setLayoutControlCoordinateInfo(attribute.getActivity(), layoutControl);
                }
            }
        }
    }

    protected boolean isNewYearsCardCompletePageCount() throws Exception {
        ArrayList<SnapsPage> pageList = attribute.getPageList();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return Const_PRODUCT.isNewYearsCardProduct() && totalQuantity >= SnapsProductEditConstants.MAX_NEW_YEARS_CARD_QUANTITY;

    }

    protected int findEmptyPageIdxWithPageList() throws Exception {
        return PhotobookCommonUtils.findEmptyPageIdxWithPageList(attribute.getPageList());
    }

    protected int checkBaseOrderOptionAndGetResultCode() {
        int resultCode = ORDER_PREPARE_INSPECT_RESULT_OK;
        if (!CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_CONNECT_NETWORK;
        else if (!SystemUtil.isEnoughStorageSpace()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_ENOUGH_STORAGE_SPACE;
        else if (getAttribute() == null || getAttribute().getSnapsTemplate() == null) resultCode = ORDER_PREPARE_INSPECT_RESULT_DATA_ERROR;
        else if (!isLogOn()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_LOGGED_IN;
        else if (!isExistProjectFileFolder()) resultCode = ORDER_PREPARE_INSPECT_RESULT_IS_NOT_EXIST_PROJECT_FILE_FOLDER;
        return resultCode;
    }

    /**
     * ??????????????? ???????????? ??????(auraXML ???)??? ????????? ???????????? ?????? ????????? ?????? ?????? ?????? ??? ??????
     */
    private boolean isExistProjectFileFolder() {
        try {
            return Config.checkProjectFileDir();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("failed make to project file folder :");
        }
        return false;
    }

    /***
     * ???????????? ????????? ????????? ???????????? ??????.
     */
    protected boolean isExistLowResolutionPhoto() throws Exception {
        if (attribute == null)
            return false;

        return PhotobookCommonUtils.isContainLowResolutionImageOnPages(attribute.getPageList());
    }

    protected boolean isExistTextControl() throws Exception {
        if (attribute == null || attribute.getPageList() == null)
            return true;

        for (SnapsPage page : attribute.getPageList()) {
            if (page != null && page.getTextControlList() != null && !page.getTextControlList().isEmpty()) return true;
        }

        return false;
    }

    private boolean isLogOn() {
        String userNo = SnapsLoginManager.getUUserNo(attribute.getActivity());
        return !StringUtil.isEmpty(userNo);
    }

    public SnapsOrderActivityBridge getSnapsOrderActivityBridge() {
        return snapsOrderActivityBridge;
    }

    public void setSnapsOrderActivityBridge(SnapsOrderActivityBridge snapsOrderActivityBridge) {
        this.snapsOrderActivityBridge = snapsOrderActivityBridge;
    }

    public SnapsOrderAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(SnapsOrderAttribute attribute) {
        this.attribute = attribute;
    }

    public DialogInputNameFragment getDiagInput() {
        return diagInput;
    }

    public DialogConfirmFragment getDiagConfirm() {
        return diagConfirm;
    }

    public void setDiagInput(DialogInputNameFragment diagInput) {
        this.diagInput = diagInput;
    }

    public void setDiagConfirm(DialogConfirmFragment diagConfirm) {
        this.diagConfirm = diagConfirm;
    }

    protected boolean isShowingDiagInput() {
        return getDiagInput() != null && getDiagInput().isShowingDialog();
    }
}
