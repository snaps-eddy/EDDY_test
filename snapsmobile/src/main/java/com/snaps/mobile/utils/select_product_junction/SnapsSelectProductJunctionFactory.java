package com.snaps.mobile.utils.select_product_junction;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForAccessory;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForAccordionCard;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForBabyNameSticker;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForCalendar;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForDIYSticker;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForFrameProduct;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForGiftProducts;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForMiniBannerProduct;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForNewPhoneCase;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForNewSticker;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForNewYearsCard;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForPhoneCase;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForPolaroidShapeProducts;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForPoster;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForSealSticker;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForSimpleMakingBook;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForSimplePhotoBook;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForSlogan;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForSmartTalk;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForTransparencyPhotoCard;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionGotoPage;
import com.snaps.mobile.utils.select_product_junction.junctions.ProductCodeMatcher;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionFactory {

    /**
     * ????????? ?????????????????? ????????? ??? ????????? StrSwitcher ?????? ????????????, ??? ?????? ????????? ???????????? ????????? if-else ??? ?????? ????????? ????????????.
     */
    private ProductCodeMatcher productCodeMatcher;

    private SnapsSelectProductJunctionFactory() {
        productCodeMatcher = new ProductCodeMatcher();
    }

    public static SnapsSelectProductJunctionFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final SnapsSelectProductJunctionFactory INSTANCE = new SnapsSelectProductJunctionFactory();
    }

    public ISnapsProductLauncher createProductLauncher(String prodKey) {
        ISnapsProductLauncher launcher = productCodeMatcher.findProductLauncher(prodKey);
        if (launcher != null) {
            return launcher;
        }

        if (Config.isSimplePhotoBook(prodKey)) {
            // ?????? ???????????? BO ?????? ????????? ???????????? ???????????? ??????. ????????????????????? ????????? ?????? ???????????????.
            return new SnapsSelectProductJunctionForSimplePhotoBook();
        } else if (Config.isSimpleMakingBook(prodKey)) {
            return new SnapsSelectProductJunctionForSimpleMakingBook();
        } else if (Const_PRODUCT.isFrameProduct(prodKey) && !Config.isCalendar(prodKey)) {
            // ?????????
            return new SnapsSelectProductJunctionForFrameProduct();
        } else if (Const_PRODUCT.isPolaroidProduct(prodKey) || Const_PRODUCT.isWalletProduct(prodKey)) {
            //?????????, ???????????????
            return new SnapsSelectProductJunctionForPolaroidShapeProducts();
        } else if (Config.isCalendar(prodKey)) {
            // ?????????
            return new SnapsSelectProductJunctionForCalendar();
        } else if (Const_PRODUCT.isDesignNoteProduct(prodKey) || Const_PRODUCT.isMousePadProduct(prodKey)) {
            return new SnapsSelectProductJunctionForGiftProducts();
        } else if (Const_PRODUCT.isNewYearsCardProduct(prodKey)) {
            // ?????? ?????????
            return new SnapsSelectProductJunctionForNewYearsCard();
        } else if (Const_PRODUCT.isDIYStickerProduct(prodKey)) {
            //?????????
            return new SnapsSelectProductJunctionForDIYSticker();
        } else if (Const_PRODUCT.isStikerGroupProduct(prodKey)) {
            //?????????
            return new SnapsSelectProductJunctionForNewSticker();
        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct(prodKey)) {
            //?????????
            return new SnapsSelectProductJunctionForBabyNameSticker();
        } else if (Const_PRODUCT.isAccessoryProductGroup(prodKey)) {
            //????????????
            return new SnapsSelectProductJunctionForAccessory();
        } else if (Const_PRODUCT.isAccordionCardProduct(prodKey)) {
            // ???????????? ??????
            return new SnapsSelectProductJunctionForAccordionCard();
        } else if (Const_PRODUCT.isPosterGroupProduct(prodKey)) {
            // ????????? ??????
            return new SnapsSelectProductJunctionForPoster();
        } else if (Const_PRODUCT.isSloganProduct(prodKey)) {
            // ????????? ??????
            return new SnapsSelectProductJunctionForSlogan();
        } else if (Const_PRODUCT.isMiniBannerProduct(prodKey)) {
            // ???????????? ??????
            return new SnapsSelectProductJunctionForMiniBannerProduct();
        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct(prodKey)) {
            // ?????? ?????? ??????
            return new SnapsSelectProductJunctionForTransparencyPhotoCard();
        } else if (Const_PRODUCT.isSmartTalkProduct(prodKey)) {
            // ????????????
            return new SnapsSelectProductJunctionForSmartTalk();
        } else if (Const_PRODUCT.isLegacyPhoneCaseProduct(prodKey)) {
            // ????????????
            return new SnapsSelectProductJunctionForPhoneCase();
        } else if (Const_PRODUCT.isUvPhoneCaseProduct(prodKey) || Const_PRODUCT.isPrintPhoneCaseProduct(prodKey)) {
            // 2020.08.18 New Phone case
            return new SnapsSelectProductJunctionForNewPhoneCase();
        } else if (Const_PRODUCT.isSealStickerProduct(prodKey)) {
            return new SnapsSelectProductJunctionForSealSticker();
        } else {
            return new SnapsSelectProductJunctionGotoPage();
        }
    }
}
