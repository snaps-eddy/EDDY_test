package com.snaps.mobile.activity.themebook.design_list.adapter;

import android.content.Context;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;

import java.util.List;

/**
 * Created by kimduckwon on 2017. 11. 30..
 */

public class ThemeDesignListAdapterFactory {

    public static BaseThemeDesignListAdapter createAdapter(Context context, BaseThemeDesignListAdapter.DesignListAdapterAttribute adapterAttribute) {

        if (Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct()) {
            return new ThemeProductListAdapter(context, adapterAttribute);
        } else if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            return new ThemePhotoCardDesignListAdapter(context, adapterAttribute);
        } else if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
            return new ThemePhotoBookDesignListAdapter(context, adapterAttribute);
        } else if (Const_PRODUCT.isStikerGroupProduct() || Const_PRODUCT.isPosterGroupProduct()
                || Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isSloganProduct()
                || Const_PRODUCT.isBabyNameStikerGroupProduct() || Const_PRODUCT.isMiniBannerProduct()) {
            return new ThemeStickerDesignListAdapter(context, adapterAttribute);
        } else if (Const_PRODUCT.isLegacyPhoneCaseProduct()) {
            return new ThemePhoneCaseListAdapter(context, adapterAttribute);
        } else {
            return new BaseThemeDesignListAdapter(context, adapterAttribute) {
                @Override
                public void onBindViewHolder(DesignListHolder holder, int position) {

                }

                @Override
                public void selectLayout(int position) {

                }

                @Override
                public List getSelectList() {
                    return null;
                }
            };
        }
    }
}
