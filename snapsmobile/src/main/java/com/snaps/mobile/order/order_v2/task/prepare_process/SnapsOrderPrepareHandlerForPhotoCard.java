package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerForPhotoCard extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerForPhotoCard.class.getSimpleName();

    private SnapsOrderPrepareHandlerForPhotoCard(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForPhotoCard createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForPhotoCard(attribute, snapsOrderActivityBridge);
    }

//    @Override
//    public SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertAttribute() throws Exception {
//        int photoCardInspectCheckResultCode = performInspectRequiredPhotoCardQuantity();
//        if (photoCardInspectCheckResultCode == SnapsOrderPrepareHandlerDefault.ORDER_PREPARE_INSPECT_RESULT_OK) {
//
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
//                alertTitleMsgResId = R.string.photo_card_save_cart_more_card_alert_msg;
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
//        if(Const_PRODUCT.isTransparencyPhotoCardProduct()) {
//            if (!isPassPhotoReplenishmentCheckForTransparencyPhotoCard()) {
//                if (!isTranscrencyPhotoCardCompletePageCount()) {
//                    getAttribute().getSnapsTemplate().setF_PRO_YORN("N");
//                    return ORDER_PREPARE_INSPECT_RESULT_NOT_FULL_REQUIRE_PAGE;
//                } else {
//                    getAttribute().getSnapsTemplate().setF_PRO_YORN("N");
//                    return ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT;
//                }
//            }
//        } else {
//            if (!isPassPhotoReplenishmentCheckForPhotoCard()) {
//                if (!isPhotoCardCompletePageCount()) {
//                    getAttribute().getSnapsTemplate().setF_PRO_YORN("N");
//                    return ORDER_PREPARE_INSPECT_RESULT_NOT_FULL_REQUIRE_PAGE;
//                } else {
//                    getAttribute().getSnapsTemplate().setF_PRO_YORN("N");
//                    return ORDER_PREPARE_INSPECT_RESULT_NOT_PHOTO_REPLENISHMENT;
//                }
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
        if (Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            return !isTranscrencyPhotoCardCompletePageCount();
        }

        return !isPhotoCardCompletePageCount();
    }

    private boolean isPhotoCardCompletePageCount() throws Exception {
        ArrayList<SnapsPage> pageList = getAttribute().getPageList();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii+=2) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return Const_PRODUCT.isPhotoCardProduct() && totalQuantity >= SnapsProductEditConstants.MAX_PHOTO_CARD_QUANTITY;
    }

    private boolean isTranscrencyPhotoCardCompletePageCount() throws Exception {
        ArrayList<SnapsPage> pageList = getAttribute().getPageList();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii+=1) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return Const_PRODUCT.isTransparencyPhotoCardProduct() && totalQuantity >= SnapsProductEditConstants.MAX_PHOTO_CARD_QUANTITY;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(R.string.photo_card_save_cart_more_card_alert_msg);
    }
}
