package com.snaps.mobile.order.order_v2.task.prepare_process;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerForFrameProduct extends SnapsOrderPrepareHandlerDefault {
    private static final String TAG = SnapsOrderPrepareHandlerForFrameProduct.class.getSimpleName();

    protected SnapsOrderPrepareHandlerForFrameProduct(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerForFrameProduct createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerForFrameProduct(attribute, snapsOrderActivityBridge);
    }

    //액자류는 우선순위가 다르다.(해상도 체크를 먼저 한다.)
    @Override
    protected eSaveCartConditionCheckResult checkConditionForSaveToBasket() {
        try {
            if (isExistLowResolutionPhoto()) {
                return eSaveCartConditionCheckResult.FAILED_CAUSE_CONTAIN_LOW_RESOLUTION_PHOTO;
            }

            if (isLackQuantity()) {
                return eSaveCartConditionCheckResult.FAILED_CAUSE_IS_LACK_QUANTITY;
            }

            if (isExistEmptyImageControl()) {
                return eSaveCartConditionCheckResult.FAILED_CAUSE_EXIST_EMPTY_IMAGE_CONTROL;
            }

            return eSaveCartConditionCheckResult.SUCCESS;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return eSaveCartConditionCheckResult.EXCEPTION;
        }
    }

    @Override
    protected boolean isLackQuantity() throws Exception {
        return false;
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createSaveToBasketAlertNotPrintOnlyCancelBtnAttribute();
    }

//    //비어 있는 사진이 있을때
//    @Override
//    protected SnapsOrderSaveToBasketAlertAttribute createExistEmptyImageControlAlertAttribute() {
//        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(R.string.photo_card_save_cart_blank_photo_alert_msg);
//    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return null;
    }
}
