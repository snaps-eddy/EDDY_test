package com.snaps.mobile.activity.themebook.design_list;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.themebook.design_list.design_list.CardDesignList;
import com.snaps.mobile.activity.themebook.design_list.design_list.NewYearsCardDesignList;
import com.snaps.mobile.activity.themebook.design_list.design_list.PhoneCaseDesignList;
import com.snaps.mobile.activity.themebook.design_list.design_list.PhotoBookDesignList;
import com.snaps.mobile.activity.themebook.design_list.design_list.PhotoCardDesignList;
import com.snaps.mobile.activity.themebook.design_list.Interface.ThemeDesignListAPI;
import com.snaps.mobile.activity.themebook.design_list.design_list.StickerDesignList;

/**
 * Created by kimduckwon on 2017. 11. 30..
 */

public class NewThemeDesignListFactory {
    public static ThemeDesignListAPI createDesignList(NewThemeDesignListActivity activity) {

        if (Const_PRODUCT.isNewYearsCardProduct()) {
            return new NewYearsCardDesignList(activity);
        } else if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            return new PhotoCardDesignList(activity);
        } else if (Const_PRODUCT.isCardProduct()) {
            return new CardDesignList(activity);
        } else if (Const_PRODUCT.isStikerGroupProduct()
                || Const_PRODUCT.isPosterGroupProduct()
                || Const_PRODUCT.isAccordionCardProduct()
                || Const_PRODUCT.isSloganProduct()
                || Const_PRODUCT.isBabyNameStikerGroupProduct()
                || Const_PRODUCT.isMiniBannerProduct()) {
            return new StickerDesignList(activity);
        } else if (Const_PRODUCT.isLegacyPhoneCaseProduct()){
            return new PhoneCaseDesignList(activity);
        } else {
            return new PhotoBookDesignList(activity);
        }

    }
}
