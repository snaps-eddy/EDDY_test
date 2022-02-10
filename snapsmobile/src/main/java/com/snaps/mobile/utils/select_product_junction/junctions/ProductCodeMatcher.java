package com.snaps.mobile.utils.select_product_junction.junctions;

import androidx.annotation.Nullable;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.ui.menu.webinterface.ISnapsWebEventCMDConstants;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductJunctionStrSwitchPerform;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;
import java.util.Map;

import static com.snaps.common.utils.constant.Const_PRODUCT.HOLOGRAPHY_SLOGAN_60X20;
import static com.snaps.common.utils.constant.Const_PRODUCT.MAGICAL_REFLECTIVE_SLOGAN_60X20;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_FOLDER_ORIGINAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_FOLDER_WIDE;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_NORMAL_ORIGINAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_NORMAL_WIDE;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_POLAROID;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_POST_CARD_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_POST_CARD_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_SQUARE_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_SQUARE_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_TTAEBUJI;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_WOOD_BLOCK;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PHOTO_CARD;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_NEW_WALLET_PHOTO;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PHOTO_MUGCUP;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_TUMBLR;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_TUMBLR_GRADE;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_NEW_POLAROID;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PACKAGE_NEW_POLAROID_MINI;
import static com.snaps.common.utils.constant.Const_PRODUCT.REFLECTIVE_SLOGAN_60X20;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.PRODUCT_PHOTOPRINT_PRODCODE;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.PRODUCT_THEMEBOOK_A5;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.PRODUCT_THEMEBOOK_A6;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.PRODUCT_IDENTIFY_PHOTOPRINT_PRODCODE;

/**
 * Created by ysjeong on 2016. 11. 21..
 */

public class ProductCodeMatcher implements ISnapsWebEventCMDConstants {

    private Map<String, ISnapsProductJunctionStrSwitchPerform> performMap = new HashMap<>();

    public ProductCodeMatcher() {
        clear();
        createCases();
    }

    public void clear() {
        if (performMap != null && !performMap.isEmpty()) {
            performMap.clear();
        }
    }

    private void createCases() {
        //Card
        final String[] arrCardProdKeys = {
                PRODUCT_CARD_5_7_NORMAL_ORIGINAL,
                PRODUCT_CARD_5_7_NORMAL_WIDE,
                PRODUCT_CARD_5_7_FOLDER_ORIGINAL,
                PRODUCT_CARD_5_7_FOLDER_WIDE
        };
        for (String key : arrCardProdKeys) {
            addCase(key, SnapsSelectProductJunctionForCard::new);
        }

        //ThemeBook
        final String[] arrThemeBookProdKeys = {
                PRODUCT_THEMEBOOK_A5,
                PRODUCT_THEMEBOOK_A6
        };

        for (String key : arrThemeBookProdKeys) {
            addCase(key, SnapsSelectProductJunctionForThemeBook::new);
        }

        //PhotoPrint
        for (String key : PRODUCT_PHOTOPRINT_PRODCODE) {
            addCase(key, SnapsSelectProductJunctionForPhotoPrint::new);
        }

        //Sticker
        addCase(Config.PRODUCT_STICKER, SnapsSelectProductJunctionForStickerKit::new);

        //MugCups
        final String[] arrMugCupsProdKeys = {
                PRODUCT_TUMBLR,
                PRODUCT_TUMBLR_GRADE,
                PRODUCT_PHOTO_MUGCUP
        };
        for (String key : arrMugCupsProdKeys) {
            addCase(key, SnapsSelectProductJunctionForCupProducts::new);
        }

        //KakaoBook
        final String[] arrKakaoBookProdKeys = {
                Config.PRODUCT_NEW_KAKAKO_STORYBOOK_HARD,
                Config.PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT
        };

        for (String key : arrKakaoBookProdKeys) {
            addCase(key, SnapsSelectProductJunctionForNewKakaoBook::new);
        }

        //FaceBook
        final String[] arrFaceBookProdKeys = {
                Config.PRODUCT_FACEBOOK_PHOTOBOOK_HARD,
                Config.PRODUCT_FACEBOOK_PHOTOBOOK_SOFT
        };
        for (String key : arrFaceBookProdKeys) {
            addCase(key, SnapsSelectProductJunctionForFaceBookPhotoBook::new);
        }

        //DiaryBook
        final String[] arrDiaryBookProdKeys = {
                Config.PRODUCT_SNAPS_DIARY_HARD,
                Config.PRODUCT_SNAPS_DIARY_SOFT
        };
        for (String key : arrDiaryBookProdKeys) {
            addCase(key, SnapsSelectProductJunctionForDiaryBook::new);
        }

        //PackageKits
        final String[] arrPackageKitProdKeys = {
                PRODUCT_PACKAGE_SQUARE_HORIZONTAL, PRODUCT_PACKAGE_SQUARE_VERTICAL,
                PRODUCT_PACKAGE_WOOD_BLOCK,
                PRODUCT_PACKAGE_POST_CARD_HORIZONTAL, PRODUCT_PACKAGE_POST_CARD_VERTICAL,
                PRODUCT_PACKAGE_TTAEBUJI, PRODUCT_PACKAGE_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID_MINI};
        for (String key : arrPackageKitProdKeys) {
            addCase(key, SnapsSelectProductJunctionForPackageKits::new);
        }

        //PhotoCard
        addCase(PRODUCT_PHOTO_CARD, SnapsSelectProductJunctionForPhotoCard::new);

        //지갑용 사진(신규)
        addCase(PRODUCT_NEW_WALLET_PHOTO, SnapsSelectProductJunctionForWalletPhoto::new);

        //증명 사진
        for (String key : PRODUCT_IDENTIFY_PHOTOPRINT_PRODCODE) {
            addCase(key, SnapsSelectProductJunctionForIdentifyPhotoPrint::new);
        }

        // 아크릴 키링
        addCase(Const_PRODUCT.ACRYLIC_KEYRING, SnapsSelectProductJunctionForAcrylicKeyRing::new);

        // 아크릴 스탠드
        addCase(Const_PRODUCT.ACRYLIC_STAND, SnapsSelectProductJunctionForAcrylicStand::new);

        // 매지컬 반사 슬로건
        final String[] magicalReflectiveSlogans = {Const_PRODUCT.MAGICAL_REFLECTIVE_SLOGAN_18X6, MAGICAL_REFLECTIVE_SLOGAN_60X20};
        for (String key : magicalReflectiveSlogans) {
            addCase(key, SnapsSelectProductJunctionForMagicalReflectiveSlogan::new);
        }

        // 반사 슬로건
        final String[] reflectiveSlogans = {Const_PRODUCT.REFLECTIVE_SLOGAN_18X6, REFLECTIVE_SLOGAN_60X20};
        for (String key : reflectiveSlogans) {
            addCase(key, SnapsSelectProductJunctionForReflectiveSlogan::new);
        }

        // 홀로그램 슬로건
        final String[] holographySlogans = {Const_PRODUCT.HOLOGRAPHY_SLOGAN_18X6, HOLOGRAPHY_SLOGAN_60X20};
        for (String key : holographySlogans) {
            addCase(key, SnapsSelectProductJunctionForHolographySlogan::new);
        }

        // 핀버튼, 자석버튼, 거울 버튼
        final String[] pinBackButtons = {
                Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_32X32,
                Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_38X38,
                Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_44X44,
                Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_58X58,
                Const_PRODUCT.PIN_BACK_BUTTON_CIRCLE_75X75,
                Const_PRODUCT.PIN_BACK_BUTTON_SQUARE_37X37,
                Const_PRODUCT.PIN_BACK_BUTTON_SQUARE_50X50,
                Const_PRODUCT.PIN_BACK_BUTTON_HEART_57X52,
                Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_32X32,
                Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_38X38,
                Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_44X44,
                Const_PRODUCT.MAGNET_BACK_BUTTON_CIRCLE_58X58,
                Const_PRODUCT.MAGNET_BACK_BUTTON_SQUARE_37X37,
                Const_PRODUCT.MAGNET_BACK_BUTTON_SQUARE_50X50,
                Const_PRODUCT.MAGNET_BACK_BUTTON_HEART_57X52,
                Const_PRODUCT.MIRROR_BACK_BUTTON_CIRCLE_58X58,
                Const_PRODUCT.MIRROR_BACK_BUTTON_CIRCLE_75X75,
        };
        for (String key : pinBackButtons) {
            addCase(key, SnapsSelectProductJunctionForButtons::new);
        }

        // 버즈 케이스
        addCase(Const_PRODUCT.BUDS_CASE, SnapsSelectProductJunctionForBudsCase::new);

        // 에어팟 케이스
        final String[] airpodsCases = {Const_PRODUCT.AIRPODS_CASE, Const_PRODUCT.AIRPODS_PRO_CASE};
        for (String key : airpodsCases) {
            addCase(key, SnapsSelectProductJunctionForAirpodsCase::new);
        }

        // 패브릭포스터
        final String[] fabricPosters = {Const_PRODUCT.FABRIC_POSTER_A1,
                Const_PRODUCT.FABRIC_POSTER_A2,
                Const_PRODUCT.FABRIC_POSTER_A3};
        for (String key : fabricPosters) {
            addCase(key, SnapsSelectProductJunctionForFabricPoster::new);
        }

        // 틴케이스
        final String[] tinCases = {Const_PRODUCT.TIN_CASE_S_V,
                Const_PRODUCT.TIN_CASE_M_V,
                Const_PRODUCT.TIN_CASE_L_V,
                Const_PRODUCT.TIN_CASE_S_H,
                Const_PRODUCT.TIN_CASE_M_H,
                Const_PRODUCT.TIN_CASE_L_H};
        for (String key : tinCases) {
            addCase(key, SnapsSelectProductJunctionForTinCase::new);
        }

    }

    private void addCase(String str, ISnapsProductJunctionStrSwitchPerform perform) {
        if (performMap != null) {
            performMap.put(str, perform);
        }
    }

    @Nullable
    public ISnapsProductLauncher findProductLauncher(String key) {
        if (performMap == null || !performMap.containsKey(key)) return null;
        ISnapsProductJunctionStrSwitchPerform perform = performMap.get(key);
        if (perform != null) {
            return perform.getLauncher();
        }

        return null;
    }
}
