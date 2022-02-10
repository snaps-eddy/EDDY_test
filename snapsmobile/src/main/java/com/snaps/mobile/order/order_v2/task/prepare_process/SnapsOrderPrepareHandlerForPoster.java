package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

import java.util.ArrayList;

import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A2_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A2_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A3_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A3_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A4_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A4_VERTICAL;
import static com.snaps.mobile.activity.common.products.multi_page_product.PosterEditor.MAX_A2_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.PosterEditor.MAX_A3_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.PosterEditor.MAX_A4_QUANTITY;

/**
 * Created by kimduckwon on 2018. 1. 17..
 */

public class SnapsOrderPrepareHandlerForPoster extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerForPoster.class.getSimpleName();
    private SnapsOrderPrepareHandlerForPoster(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForPoster createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForPoster(attribute, snapsOrderActivityBridge);
    }

//    @Override
//    public SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertAttribute() throws Exception {
//        int photoCardInspectCheckResultCode = performInspectRequiredPhotoCardQuantity();
//        if (photoCardInspectCheckResultCode == SnapsOrderPrepareHandlerDefault.ORDER_PREPARE_INSPECT_RESULT_OK) {
//            return isExistLowResolutionPhoto() ? SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeNoPrintWithTitleResId(R.string.dialog_save_confirm_title)
//                    : SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeWithTitleResId(R.string.dialog_save_confirm_title);
//        } else {
//            int alertTitleMsgResId = R.string.photo_card_save_cart_blank_photo_alert_msg;
//            if (photoCardInspectCheckResultCode == SnapsOrderConstants.ORDER_PREPARE_INSPECT_RESULT_NOT_FULL_REQUIRE_PAGE)
//                alertTitleMsgResId = getSaveAlertMsg();
//            return isExistLowResolutionPhoto() ? SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeNoPrintWithTitleResId(alertTitleMsgResId)
//                    : SnapsOrderSaveToBasketAlertAttribute.createPhotoCardSaveToBasketAlertAttributeWithTitleResId(alertTitleMsgResId);
//        }
//    }

    private int getPosterLackQuantityAlertMsg() {
        switch (Config.getPROD_CODE()) {
            case POSTER_A4_VERTICAL:
            case POSTER_A4_HORIZONTAL:
                return R.string.poster_A4_quantity_shortage;
            case POSTER_A3_VERTICAL:
            case POSTER_A3_HORIZONTAL:
                return  R.string.poster_A3_quantity_shortage;
            default:
                return 0;

        }
    }

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
//        if (!isPassPhotoReplenishmentCheckForPoster()) {
//            if (!isPosterCompletePageCount()) {
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
        return !isPosterCompletePageCount();
    }

    private boolean isPosterCompletePageCount() throws Exception {
        ArrayList<SnapsPage> pageList = getAttribute().getPageList();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return Const_PRODUCT.isPosterGroupProduct() && totalQuantity >= getPosterMaxQuantity();
    }

    private int getPosterMaxQuantity() {
        switch (Config.getPROD_CODE()) {
            case POSTER_A4_VERTICAL:
            case POSTER_A4_HORIZONTAL:
                return MAX_A4_QUANTITY;
            case POSTER_A3_VERTICAL:
            case POSTER_A3_HORIZONTAL:
                return MAX_A3_QUANTITY;
            case POSTER_A2_VERTICAL:
            case POSTER_A2_HORIZONTAL:
                return MAX_A2_QUANTITY;
            default:
                return 0;
        }
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(getPosterLackQuantityAlertMsg());
    }
}

