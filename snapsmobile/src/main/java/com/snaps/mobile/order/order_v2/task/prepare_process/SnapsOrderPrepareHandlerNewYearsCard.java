package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by kimduckwon on 2017. 11. 15..
 */

public class SnapsOrderPrepareHandlerNewYearsCard extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerNewYearsCard.class.getSimpleName();

    private SnapsOrderPrepareHandlerNewYearsCard(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerNewYearsCard createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerNewYearsCard(attribute, snapsOrderActivityBridge);
    }

//    @Override
//    public SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertAttribute() throws Exception {
//        int photoCardInspectCheckResultCode = performInspectRequiredPhotoCardQuantity();
//        if (photoCardInspectCheckResultCode == SnapsOrderPrepareHandlerDefault.ORDER_PREPARE_INSPECT_RESULT_OK) {
//            if (isExistLowResolutionPhoto()) {
//                return SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeNoPrintWithTitleResId(R.string.dialog_save_confirm_title);
//            } else if (SnapsTextToImageUtil.isSupportEditTextProduct() && isExistTextControl()) {
//                return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertExistTextControl();
//            }
//
//            return SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeWithTitleResId(R.string.dialog_save_confirm_title);
//        } else {
//            int alertTitleMsgResId = R.string.photo_card_save_cart_blank_photo_alert_msg;
//            if (photoCardInspectCheckResultCode == SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_FULL_REQUIRE_PAGE)
//                alertTitleMsgResId = R.string.new_years_card_save_cart_more_card_alert_msg;
//            return isExistLowResolutionPhoto() ? SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeNoPrintWithTitleResId(alertTitleMsgResId)
//                    : SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeWithTitleResId(alertTitleMsgResId);
//        }
//    }

    @Override
    public int performInspectOrderOptionAndGetResultCode() throws Exception {
        int resultCode = checkBaseOrderOptionAndGetResultCode();
        if (resultCode != ORDER_PREPARE_INSPECT_RESULT_OK) return resultCode;

        if (!Config.isValidProjCode()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_PROJECT_CODE;
//        else if (!isPassPhotoReplenishmentCheck()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT;
//        else if (!isExistLowResolutionPhoto()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_PRINTABLE_PHOTO_EXIST;

        return resultCode;
    }

//    public int performInspectRequiredPhotoCardQuantity() throws Exception {
//        if (!isPassPhotoReplenishmentCheckForNewYearsCard()) {
//            if (!isNewYearsCardCompletePageCount()) {
//                getAttribute().getSnapsTemplate().setF_PRO_YORN("N");
//                return ORDER_PREPARE_INSPECT_RESULT_NOT_FULL_REQUIRE_PAGE;
//            } else {
//                getAttribute().getSnapsTemplate().setF_PRO_YORN("N");
//                return ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT;
//            }
//        }
//
//        getAttribute().getSnapsTemplate().setF_PRO_YORN("Y");
//        return ORDER_PREPARE_INSPECT_RESULT_OK;
//    }

    @Override
    public void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
        if (!isShowingDiagInput()) {
            if (getAttribute().isEditMode())
                setDiagInput(DialogInputNameFragment.newInstanceSave(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));
            else
                setDiagInput(DialogInputNameFragment.newInstance(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));

            setSaveToBasketAlertMsg(alertAttribute);

            getDiagInput().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
        }
    }

    private void setSaveToBasketAlertMsg(SnapsOrderSaveToBasketAlertAttribute alertAttribute) throws Exception {
        getDiagInput().setAlertAttribute(alertAttribute);
    }

    @Override
    public void showCompleteUploadPopup(DialogConfirmFragment.IDialogConfirmClickListener dialogConfirmClickListener) {
        setDiagConfirm(DialogConfirmFragment.newInstance(DialogConfirmFragment.DIALOG_TYPE_ORDER_COMPLETE, dialogConfirmClickListener));

        try {
            getDiagConfirm().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
        } catch (IllegalStateException e) { Dlog.e(TAG, e); }
    }

    @Override
    protected void setTextControlBaseText() throws Exception {}

    @Override
    protected boolean isLackQuantity() throws Exception {
        return !isNewYearsCardCompletePageCount();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(R.string.new_years_card_save_cart_more_card_alert_msg);
    }
}
