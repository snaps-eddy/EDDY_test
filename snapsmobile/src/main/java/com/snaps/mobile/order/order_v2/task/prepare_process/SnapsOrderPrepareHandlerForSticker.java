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

import static com.snaps.common.utils.constant.Const_PRODUCT.BIG_RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.EXAM_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.LONG_PHOTO_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.NAME_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.ROUND_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.SQUARE_STICKER;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_BIG_RECTANGLE_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_EXAM_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_LONG_PHOTO_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_NAME_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_RECTANGLE_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_ROUND_QUANTITY;
import static com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor.MAX_SQUARE_QUANTITY;

/**
 * Created by kimduckwon on 2018. 1. 17..
 */

public class SnapsOrderPrepareHandlerForSticker extends SnapsOrderPrepareBaseHandler {
    private static final String TAG = SnapsOrderPrepareHandlerForSticker.class.getSimpleName();

    private SnapsOrderPrepareHandlerForSticker(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForSticker createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForSticker(attribute, snapsOrderActivityBridge);
    }

    @Override
    public int performInspectOrderOptionAndGetResultCode() throws Exception {
        int resultCode = checkBaseOrderOptionAndGetResultCode();
        if (resultCode != ORDER_PREPARE_INSPECT_RESULT_OK) return resultCode;

        if (!Config.isValidProjCode()) resultCode = ORDER_PREPARE_INSPECT_RESULT_NOT_EXIST_PROJECT_CODE;

        return resultCode;
    }

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
        } catch (IllegalStateException e) { Dlog.e(TAG, e);}
    }

    @Override
    protected void setTextControlBaseText() throws Exception {}

    @Override
    protected boolean isLackQuantity() throws Exception {
        return !isStickerCompletePageCount();
    }

    private boolean isStickerCompletePageCount() throws Exception {
        ArrayList<SnapsPage> pageList = getAttribute().getPageList();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return Const_PRODUCT.isStikerGroupProduct() && totalQuantity >= getStickerMaxQuantity();
    }

    private int getStickerMaxQuantity() {
        switch (Config.getPROD_CODE()) {
            case ROUND_STICKER:
                return MAX_ROUND_QUANTITY;
            case SQUARE_STICKER:
                return MAX_SQUARE_QUANTITY;
            case RECTANGLE_STICKER:
                return MAX_RECTANGLE_QUANTITY;
            case EXAM_STICKER:
                return MAX_EXAM_QUANTITY;
            case NAME_STICKER:
                return MAX_NAME_QUANTITY;
            case BIG_RECTANGLE_STICKER:
                return MAX_BIG_RECTANGLE_QUANTITY;
            case LONG_PHOTO_STICKER:
                return MAX_LONG_PHOTO_QUANTITY;
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
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(getStickerLackQuantitySaveAlertMsg());
    }

    private int getStickerLackQuantitySaveAlertMsg() {
        switch (Config.getPROD_CODE()) {
            case ROUND_STICKER:
                return R.string.round_sticker_quantity_shortage;
            case SQUARE_STICKER:
                return  R.string.square_sticker_quantity_shortage;
            case RECTANGLE_STICKER:
                return R.string.rectangle_sticker_quantity_shortage;
            case EXAM_STICKER:
                return R.string.round_sticker_quantity_shortage;
            case NAME_STICKER:
                return R.string.name_sticker_quantity_shortage;
            case BIG_RECTANGLE_STICKER:
                return R.string.round_sticker_quantity_shortage;
            case LONG_PHOTO_STICKER:
                return R.string.long_photo_sticker_quantity_shortage;
            default:
                return 0;
        }
    }
}

