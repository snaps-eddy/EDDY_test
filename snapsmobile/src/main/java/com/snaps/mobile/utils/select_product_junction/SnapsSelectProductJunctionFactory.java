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
     * 단순히 프로덕트코드 비교만 할 경우는 StrSwitcher 에서 검색하고, 그 외에 그룹을 확인하는 방법은 if-else 를 통해 객체를 생성한다.
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
            // 심플 포토북은 BO 애서 모바일 포토북을 지칭하는 이름. 모바일관점으로 본다면 일반 포토북이다.
            return new SnapsSelectProductJunctionForSimplePhotoBook();
        } else if (Config.isSimpleMakingBook(prodKey)) {
            return new SnapsSelectProductJunctionForSimpleMakingBook();
        } else if (Const_PRODUCT.isFrameProduct(prodKey) && !Config.isCalendar(prodKey)) {
            // 액자군
            return new SnapsSelectProductJunctionForFrameProduct();
        } else if (Const_PRODUCT.isPolaroidProduct(prodKey) || Const_PRODUCT.isWalletProduct(prodKey)) {
            //지갑용, 폴라로이드
            return new SnapsSelectProductJunctionForPolaroidShapeProducts();
        } else if (Config.isCalendar(prodKey)) {
            // 캘린더
            return new SnapsSelectProductJunctionForCalendar();
        } else if (Const_PRODUCT.isDesignNoteProduct(prodKey) || Const_PRODUCT.isMousePadProduct(prodKey)) {
            return new SnapsSelectProductJunctionForGiftProducts();
        } else if (Const_PRODUCT.isNewYearsCardProduct(prodKey)) {
            // 일본 연하장
            return new SnapsSelectProductJunctionForNewYearsCard();
        } else if (Const_PRODUCT.isDIYStickerProduct(prodKey)) {
            //스티커
            return new SnapsSelectProductJunctionForDIYSticker();
        } else if (Const_PRODUCT.isStikerGroupProduct(prodKey)) {
            //스티커
            return new SnapsSelectProductJunctionForNewSticker();
        } else if (Const_PRODUCT.isBabyNameStikerGroupProduct(prodKey)) {
            //스티커
            return new SnapsSelectProductJunctionForBabyNameSticker();
        } else if (Const_PRODUCT.isAccessoryProductGroup(prodKey)) {
            //액세서리
            return new SnapsSelectProductJunctionForAccessory();
        } else if (Const_PRODUCT.isAccordionCardProduct(prodKey)) {
            // 아코디언 카드
            return new SnapsSelectProductJunctionForAccordionCard();
        } else if (Const_PRODUCT.isPosterGroupProduct(prodKey)) {
            // 포스터 그룹
            return new SnapsSelectProductJunctionForPoster();
        } else if (Const_PRODUCT.isSloganProduct(prodKey)) {
            // 슬로건 그룹
            return new SnapsSelectProductJunctionForSlogan();
        } else if (Const_PRODUCT.isMiniBannerProduct(prodKey)) {
            // 미니배너 그룹
            return new SnapsSelectProductJunctionForMiniBannerProduct();
        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct(prodKey)) {
            // 투명 포토 카드
            return new SnapsSelectProductJunctionForTransparencyPhotoCard();
        } else if (Const_PRODUCT.isSmartTalkProduct(prodKey)) {
            // 스마트톡
            return new SnapsSelectProductJunctionForSmartTalk();
        } else if (Const_PRODUCT.isLegacyPhoneCaseProduct(prodKey)) {
            // 폰케이스
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
