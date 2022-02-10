package com.snaps.mobile.order.order_v2.task.prepare_process;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by ysjeong on 2017. 3. 31..
 */

public class SnapsOrderPrepareHandlerRecommendPhotoBook extends SnapsOrderPrepareHandlerDefault {
    protected SnapsOrderPrepareHandlerRecommendPhotoBook(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        super(attribute, snapsOrderActivityBridge);
    }

    public static SnapsOrderPrepareHandlerRecommendPhotoBook createInstanceWithAttribute(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        return new SnapsOrderPrepareHandlerRecommendPhotoBook(attribute, snapsOrderActivityBridge);
    }

//    @Override
//    public SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertAttribute() throws Exception {
//        int completeEdit = isCheckEditState();
//
//        if(completeEdit == -1) {
//            if (isCheckResolution()) {
//                return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute().setAdditionTitleText(getTitleConfirmText());
//            } else if (SnapsTextToImageUtil.isSupportEditTextProduct() && isExistTextControl()) {
//                return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertExistTextControl().setAdditionTitleText(getTitleConfirmText());
//            }
//
//            return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttribute().setAdditionTitleText(getTitleConfirmText());
//        } else { //빈영역이 있다.
//            if(Const_PRODUCT.isMiniBannerProduct()) {
//                int alertTitleMsgResId = R.string.not_photo_replenishment;
//                return SnapsOrderSaveToBasketAlertAttribute.createNoImageWithTitleResId(alertTitleMsgResId).setAdditionTitleText(getTitleConfirmText());
//            }else {
//                int alertTitleMsgResId = R.string.photo_card_save_cart_blank_photo_alert_msg;
//                return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(alertTitleMsgResId).setAdditionTitleText(getTitleConfirmText());
//            }
//        }
//    }

    private String getTitleConfirmText() {
        if (StringUtil.isEmptyAfterTrim(Config.getPROJ_NAME())) return "";
        return String.format(getAttribute().getActivity().getString(R.string.save_popup_confirm_title_with_format), Config.getPROJ_NAME());
    }

    @Override
    protected boolean isLackQuantity() throws Exception {
        return false;
    }

    protected SnapsOrderSaveToBasketAlertAttribute createSaveConditionSuccessAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttribute().setAdditionTitleText(getTitleConfirmText());
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLowResolutionAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertNotPrintAttribute().setAdditionTitleText(getTitleConfirmText());
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createExistEmptyImageControlAlertAttribute() {
        return SnapsOrderSaveToBasketAlertAttribute.createDefaultSaveToBasketAlertAttributeWithTitleResId(R.string.photo_card_save_cart_blank_photo_alert_msg).setAdditionTitleText(getTitleConfirmText());
    }

    @Override
    protected SnapsOrderSaveToBasketAlertAttribute createLackQuantityAlertAttribute() {
        return null;
    }
}
