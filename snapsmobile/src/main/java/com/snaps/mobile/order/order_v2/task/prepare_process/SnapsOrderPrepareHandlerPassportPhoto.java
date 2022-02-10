package com.snaps.mobile.order.order_v2.task.prepare_process;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerPassportPhoto extends SnapsOrderPrepareHandlerDefault {
    private SnapsOrderPrepareHandlerPassportPhoto(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerPassportPhoto createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerPassportPhoto(attribute, snapsOrderActivityBridge);
    }

    @Override
    public void showSaveToBasketAlert(SnapsOrderSaveToBasketAlertAttribute alertAttribute, DialogInputNameFragment.IDialogInputNameClickListener dialogInputNameClickListener) throws Exception {
//        if (Config.useKorean()) {
//            PassportPhotoSaveDialog confirmDialog = new PassportPhotoSaveDialog((getAttribute().getActivity()), dialogInputNameClickListener);
//            confirmDialog.setCancelable(false);
//            confirmDialog.setLowResolutionPhotoDescVisible(isExistLowResolutionPhoto());
//            confirmDialog.show();
//        } else {
            if (!isShowingDiagInput()) {
                if (getAttribute().isEditMode())
                    setDiagInput(DialogInputNameFragment.newInstanceSave(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));
                else
                    setDiagInput(DialogInputNameFragment.newInstance(Config.ORDR_STAT_ORDER_CODE, dialogInputNameClickListener));

                getDiagInput().setAlertAttribute(createSaveToBasketAlertAttribute());
                getDiagInput().show(((FragmentActivity) getAttribute().getActivity()).getSupportFragmentManager(), "dialog");
            }
//        }
    }

    @Override
    protected boolean isLackQuantity() throws Exception {
        return false;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createSaveToBasketAlertNotPrintOnlyCancelBtnAttribute();
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return null;
    }
}
