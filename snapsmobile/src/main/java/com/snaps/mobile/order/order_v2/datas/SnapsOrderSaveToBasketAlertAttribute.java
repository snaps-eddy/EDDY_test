package com.snaps.mobile.order.order_v2.datas;

import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;

/**
 * Created by ysjeong on 2017. 7. 26..
 */

public class SnapsOrderSaveToBasketAlertAttribute {
    private int titleResId, subTitleResId, cancelBtnResId, confirmBtnResId;
    private String titleText, additionTitleText, additionSubText;

    public static SnapsOrderSaveToBasketAlertAttribute createDefaultSaveToBasketAlertAttribute() {
        return new SnapsOrderSaveToBasketAlertAttribute.Builder().setTitleResId(getConfirmDialogTitleTextResId()).setCancelBtnResId(R.string.cancel).setConfirmBtnResId(R.string.save).create();
    }

    public static SnapsOrderSaveToBasketAlertAttribute createDefaultSaveToBasketAlertAttributeWithTitleResId(int titleResId) {
        return new SnapsOrderSaveToBasketAlertAttribute.Builder().setTitleResId(titleResId).setSubTitleResId(-1).setCancelBtnResId(R.string.cancel).setConfirmBtnResId(R.string.save).create();
    }

    public static SnapsOrderSaveToBasketAlertAttribute createDefaultSaveToBasketAlertAttributeWithTitleResId(String title) {
        return new SnapsOrderSaveToBasketAlertAttribute.Builder().setTitleText(title).setSubTitleResId(-1).setCancelBtnResId(R.string.cancel).setConfirmBtnResId(R.string.save).create();
    }

    public static SnapsOrderSaveToBasketAlertAttribute createDefaultSaveToBasketAlertNotPrintAttribute() {
        return new SnapsOrderSaveToBasketAlertAttribute.Builder().setTitleResId(getConfirmDialogTitleTextResId()).setSubTitleResId(R.string.print_not_recommended_comment_check).setCancelBtnResId(R.string.cancel).setConfirmBtnResId(R.string.save).create();
    }

    public static SnapsOrderSaveToBasketAlertAttribute createSaveToBasketAlertNotPrintOnlyCancelBtnAttribute() {
        return new SnapsOrderSaveToBasketAlertAttribute.Builder().setTitleResId(R.string.save_failed_msg_is_contain_low_resolution_image).setCancelBtnResId(R.string.confirm).create();
    }

    private static int getConfirmDialogTitleTextResId() {
        return Config.isSmartSnapsRecommendLayoutPhotoBook() ? R.string.save_to_basket_confirm_title_form_ani_book : R.string.dialog_save_confirm_title;
    }

    public SnapsOrderSaveToBasketAlertAttribute(Builder builder) {
        this.titleResId = builder.titleResId;
        this.subTitleResId = builder.subTitleResId;
        this.cancelBtnResId = builder.cancelBtnResId;
        this.confirmBtnResId = builder.confirmBtnResId;
        this.titleText = builder.titleText;
        this.additionTitleText = builder.additionTitleText;
        this.additionSubText = builder.additionSubText;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public String getTitleText() {
        return titleText;
    }

    public int getSubTitleResId() {
        return subTitleResId;
    }

    public int getCancelBtnResId() {
        return cancelBtnResId;
    }

    public int getConfirmBtnResId() {
        return confirmBtnResId;
    }

    public String getAdditionTitleText() {
        return additionTitleText;
    }

    public String getAdditionSubText() {
        return additionSubText;
    }

    public SnapsOrderSaveToBasketAlertAttribute setAdditionSubText(String additionSubText) {
        this.additionSubText = additionSubText;
        return this;
    }

    public SnapsOrderSaveToBasketAlertAttribute setAdditionTitleText(String additionTitleText) {
        this.additionTitleText = additionTitleText;
        return this;
    }

    public static class Builder {
        private int titleResId, subTitleResId, cancelBtnResId, confirmBtnResId;
        private String titleText, additionSubText, additionTitleText;

        public Builder setAdditionTitleText(String additionTitleText) {
            this.additionTitleText = additionTitleText;
            return this;
        }

        public Builder setAdditionSubText(String additionSubText) {
            this.additionSubText = additionSubText;
            return this;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setTitleResId(int titleResId) {
            this.titleResId = titleResId;
            return this;
        }

        public Builder setSubTitleResId(int subTitleResId) {
            this.subTitleResId = subTitleResId;
            return this;
        }

        public Builder setCancelBtnResId(int cancelBtnResId) {
            this.cancelBtnResId = cancelBtnResId;
            return this;
        }

        public Builder setConfirmBtnResId(int confirmBtnResId) {
            this.confirmBtnResId = confirmBtnResId;
            return this;
        }

        public SnapsOrderSaveToBasketAlertAttribute create() {
            return new SnapsOrderSaveToBasketAlertAttribute(this);
        }
    }
}
