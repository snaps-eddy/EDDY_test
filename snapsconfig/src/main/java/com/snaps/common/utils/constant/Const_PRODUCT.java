package com.snaps.common.utils.constant;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import java.util.HashMap;


/***
 * 상품코드 상수값 정의..
 *
 * @author hansang-ug
 *
 */
public class Const_PRODUCT {
    private static final String TAG = Const_PRODUCT.class.getSimpleName();

    // 상품에 대한 정보 저장.. 상품코드, 상품 margin, padding 등등

    /* 191001 폼보드 */
    /* 191002 매트액자 */
    /* 191003 액자프레임 원목액자 옵션 */
    /* 191005 캔버스 */
    /* 191004 아크릴 */
    /* 191006 마블 */
    /* 191007 화이트 */
    /* 191008 브러쉬 매탈액자 옵션 */

    public static final String FRAME_TYPE_FORM = "191001";
    public static final String FRAME_TYPE_MET = "191002";
    public static final String FRAME_TYPE_WOOD = "191003";
    public static final String FRAME_TYPE_CANVAS = "191005";
    public static final String FRAME_TYPE_ARC = "191004";
    public static final String FRAME_TYPE_MARVEL = "191006";
    // 메탈
    public static final String FRAME_TYPE_WHITE = "191007";
    public static final String FRAME_TYPE_BRUSH = "191008";

    // 마블액자
    // 00800800100002 마블액자[5 X 5]
    // 00800800100003 마블액자[5 X 7]
    // 00800800100004 마블액자[8 X 8]
    // 00800800100005 마블액자[8 X 10]
    public static final String PRODUCT_MARVEL_FRAME = "0080080010";

    // 원목액자군 상품 코드
    public static final String PRODUCT_WOOD_FRAME = "0080080007";

    // 메탈액자군 상품코드
    public static final String PRODUCT_METAL_FRAME = "0080080011";

    //인테리어 액자
    public static final String PRODUCT_INTEIOR_FRAME = "00800800130003";

    //행잉 액자
    public static final String PRODUCT_HANGING_FRAME_GROUP = "0080080020";
    public static final String PRODUCT_HANGING_FRAME_A1 = "00800800200016";
    public static final String PRODUCT_HANGING_FRAME_A2 = "00800800200017";
    public static final String PRODUCT_HANGING_FRAME_A3 = "00800800200018";
    public static final String PRODUCT_HANGING_FRAME_TYPE = "outsourcing_frame";

    public static final String PRODUCT_HANGING_NATURAL = "045014000171";
    public static final String PRODUCT_HANGING_WALNUT = "045014000203";
    public static final String PRODUCT_HANGING_BLACK = "045014000170";

    //프리미엄 아크릴 액자
    public static final String PRODUCT_PREMIUM_ACRYL_FRAME = "0080080008";

    //보드액자(폼보드,포멕스,복합판넬)
    public static final String PRODUCT_PORM_BOARD_FRAME = "0080080017";
    public static final String PRODUCT_FOMAX_FRAME = "0080080018";
    public static final String PRODUCT_MULTI_PANNAL_FRAME = "0080080019";

    // 액자 그룹 코드..
    public static final String PRODUCT_FRAME_GROUP = "008008";

    // 폴라로이드 오리지널.
    public static final String PRODUCT_POLAROID_ORIGNAL = "00800500080001";

    // 지갑용 사진.
    public static final String PRODUCT_WALLET = "00800500020001";

    // 편집사진 그룹 코드..
    public static final String PRODUCT_MODIFY_PHOTO_GROUP = "008005";


    /****
     * 기프트몰 상품.
     */
    public static final String PRODUCT_MOUSEPAD_GROUP = "00802300020001";
    public static final String PRODUCT_DESIGN_NOTE_GROUP = "008022";

    /**
     * Legacy Phone Case
     */
    public static final String PRODUCT_HARD_PHONE_CASE_GROUP = "008024";
    public static final String PRODUCT_BUMPER_PHONE_CASE_GROUP = "008027";

    /**
     * New Phone Case
     */
    public static final String PRODUCT_UV_PHONE_CASE_GROUP = "0080290001";
//    public static final String PRODUCT_NEW_HARD_PHONE_CASE_GROUP = "0080290002";
//    public static final String PRODUCT_NEW_BUMPER_PHONE_CASE_GROUP = "0080290003";
    /**
     * @Marko 2020년 2월 2일 폰케이스 개편에 따라 UV / 전사 두가지 그룹의 폰케이스로 변경됨.
     * 범퍼케이스는 내려가고 하드 폰케이스 그룹 코드 prefix가 0080290004로 변경.
     */
    public static final String PRODUCT_PRINT_HARD_PHONE_CASE_GROUP = "0080290004";

    public static final String PRODUCT_DESIGN_NORMAL_NOTE = "00802200020001";
    public static final String PRODUCT_DESIGN_SPRING_NOTE = "00802200030001";

    public static final String PRODUCT_STICKER_GROUP = "0080120002";

    public static final String PRODUCT_TUMBLR = "00802300010002";
    public static final String PRODUCT_TUMBLR_GRADE = "00802300010003";
    public static final String PRODUCT_PHOTO_MUGCUP = "00802300010001";

    public static final String PRODUCT_LAY_FLATBOOK = "0080060015";

    public static final String PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT = "00800600100009";
    public static final String PRODUCT_NEW_KAKAKO_STORYBOOK_HARD = "00800600100008";

    public static final String PRODUCT_FACEBOOK_PHOTOBOOK_SOFT = "00800600100011";
    public static final String PRODUCT_FACEBOOK_PHOTOBOOK_HARD = "00800600100010";

    public static final String PRODUCT_INSTAGRAM_BOOK_SOFT = "00800600100013";
    public static final String PRODUCT_INSTAGRAM_BOOK_HARD = "00800600100012";

    /**
     * 00800600070007	데일리북[A5]-하드커버	007
     * 00800600070008	데일리북[A5]-소프트커버	007
     */
    public static final String PRODUCT_SNAPS_DIARY_HARD = "00800600070007";
    public static final String PRODUCT_SNAPS_DIARY_SOFT = "00800600070008";

    //폰케이스
    public static final String PRODUCT_PHONE_CASE_IPHONE_5S = "00802400010001";
    public static final String PRODUCT_PHONE_CASE_IPHONE_6 = "00802400010002";
    public static final String PRODUCT_PHONE_CASE_IPHONE_6_PLUS = "00802400010003";
    public static final String PRODUCT_PHONE_CASE_GALAXY_S3 = "00802400020001";
    public static final String PRODUCT_PHONE_CASE_GALAXY_S4 = "00802400020002";
    public static final String PRODUCT_PHONE_CASE_GALAXY_S5 = "00802400020003";
    public static final String PRODUCT_PHONE_CASE_GALAXY_S6 = "00802400020007";
    public static final String PRODUCT_PHONE_CASE_GALAXY_NOTE_2 = "00802400020004";
    public static final String PRODUCT_PHONE_CASE_GALAXY_NOTE_3 = "00802400020005";
    public static final String PRODUCT_PHONE_CASE_GALAXY_NOTE_4 = "00802400020006";
    public static final String PRODUCT_PHONE_CASE_OPTIMUS_G2 = "00802400030001";
    public static final String PRODUCT_PHONE_CASE_OPTIMUS_G3 = "00802400030002";

    //5종 세트
    public static final String PRODUCT_PACKAGE_WOOD_BLOCK = "00800900150001";
    public static final String PRODUCT_PACKAGE_SQUARE_HORIZONTAL = "00800900180001";
    public static final String PRODUCT_PACKAGE_SQUARE_VERTICAL = "00800900180002";
    public static final String PRODUCT_PACKAGE_POST_CARD_HORIZONTAL = "00800900170001";
    public static final String PRODUCT_PACKAGE_POST_CARD_VERTICAL = "00800900170002";
    public static final String PRODUCT_PACKAGE_TTAEBUJI = "00800900160001";
    public static final String PRODUCT_PACKAGE_POLAROID = "00800900190001";
    public static final String PRODUCT_PACKAGE_NEW_POLAROID = "00800900190002";
    public static final String PRODUCT_PACKAGE_NEW_POLAROID_MINI = "00800900190003";

    //카드
    public static final String PRODUCT_CARD_5_7_NORMAL_ORIGINAL = "00800900110003";
    public static final String PRODUCT_CARD_5_7_NORMAL_WIDE = "00800900110004";
    public static final String PRODUCT_CARD_5_7_FOLDER_ORIGINAL = "00800900110005";
    public static final String PRODUCT_CARD_5_7_FOLDER_WIDE = "00800900110006";

    public static final String PRODUCT_NAME_PACKAGE_WOOD_BLOCK = "photo_pack";
    public static final String PRODUCT_NAME_PACKAGE_SQUARE = "photo_pack";
    public static final String PRODUCT_NAME_PACKAGE_POST_CARD = "photo_pack";
    public static final String PRODUCT_NAME_PACKAGE_POLAROID = "photo_pack";
    public static final String PRODUCT_NAME_PACKAGE_TTAEBUJI = "octopus";
    public static final String PRODUCT_NAME_PHOTO_CARD = "photo_pack";
    public static final String PRODUCT_NAME_WALLET_PHOTO = "photo_pack";
    public static final String PRODUCT_NAME_YEARS_CARD = "photo_pack";
    public static final String PRODUCT_NAME_TRANS_PHOTO_CARD = "transparent_card";

    public static final String PRODUCT_NAME_CARD = "5x7card";

    public static final String PRODUCT_ACCORDION_CARD = "snaps_accordion_card";
    public static final String PRODUCT_POSTER = "snaps_poster";

    public static final String PRODUCT_SIZE_TYPE_PACKAGE_WOOD_BLOCK = ""; //FIXME 안들어가는듯..
    public static final String PRODUCT_SIZE_TYPE_PACKAGE_SQUARE = "";
    public static final String PRODUCT_SIZE_TYPE_PACKAGE_POST_CARD = "";
    public static final String PRODUCT_SIZE_TYPE_PACKAGE_POLAROID = "";
    public static final String PRODUCT_SIZE_TYPE_PACKAGE_TTAEBUJI = "kit";


    //스마트 스냅스 분석 상품
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL = "045021012692";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_BABY = "045021012693";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COUPLE = "045021012696";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_FAMILY = "045021012698";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_ETC = "045021013593";

    //2021년 버전 자동 완성 포토북
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_TRAVEL_VER_2021 = "045021028567";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_BABY_VER_2021 = "045021028568";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COUPLE_VER_2021 = "045021028569";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_FAMILY_VER_2021 = "045021028570";
    public static final String PRODUCT_SMART_SNAPS_ANALYSIS_PHOTO_BOOK_ETC_VER_2021 = "045021028571";

    //포토 카드
    public static final String PRODUCT_PHOTO_CARD = "00800500180001";
    public static final String PRODUCT_NEW_WALLET_PHOTO = "00800500180002";//"00800500020002";
    //투명 포토 카드
    public static final String PRODUCT_TRANSPARENCY_PHOTO_CARD = "00800500180003";//"00800500020002";
    //일본 연하장 (PRODUCT_PACKAGE_POST_CARD_HORIZONTAL 동일한 상품코드
    public static final String PRODUCT_NEW_YEARS_CARD = "00800900170003";

    //스티커
    public final static String ROUND_STICKER = "00801200020001";
    public final static String SQUARE_STICKER = "00801200020002";
    public final static String RECTANGLE_STICKER = "00801200020003";
    public final static String EXAM_STICKER = "00801200020004";
    public final static String NAME_STICKER = "00801200020005";
    public final static String BIG_RECTANGLE_STICKER = "00801200020006";
    public final static String LONG_PHOTO_STICKER = "00801200020007";
    public final static String DIY_STICKER_A4 = "00801200020008";
    public final static String DIY_STICKER_A5 = "00801200020009";
    public final static String DIY_STICKER_A6 = "00801200020010";
    public final static String EXAM_STICKER_2020 = "00801200020011";

    public final static String BABY_NAME_STICKER_MINI = "00801200030001";
    public final static String BABY_NAME_STICKER_SMALL = "00801200030002";
    public final static String BABY_NAME_STICKER_MEDIUM = "00801200030003";
    public final static String BABY_NAME_STICKER_LARGE = "00801200030004";
    public final static String BABY_NAME_STICKER_GROUP = "0080120003";


    //액세사리몰
    public final static String ACCESSORY_PHOTO_CARD_ALBUM = "00802500010002";
    public final static String ACCESSORY_GIFT_BAG = "00802500010003";
    public final static String ACCESSORY_PHOTO_BOOK_GIFT_BOX = "00802500010004";
    public final static String ACCESSORY_PHOTO_BOOK_GIFT_BOX_ADVANCE = "00802500010005";
    public final static String ACCESSORY_GROUP = "0080250001";

    //아코디언 카드
    public final static String ACCORDION_CARD_NORMAL = "00800900200001";
    public final static String ACCORDION_CARD_MINI = "00800900200002";
    public final static String ACCORDION_CARD_GROUP = "0080090020";

    //포스터
    public final static String POSTER_A2_VERTICAL = "00800800220001";
    public final static String POSTER_A3_VERTICAL = "00800800220002";
    public final static String POSTER_A4_VERTICAL = "00800800220003";

    public final static String POSTER_A2_HORIZONTAL = "00800800210001";
    public final static String POSTER_A3_HORIZONTAL = "00800800210002";
    public final static String POSTER_A4_HORIZONTAL = "00800800210003";

    public final static String POSTER_GROUP_VERTICAL = "0080080022";
    public final static String POSTER_GROUP_HORIZONTAL = "0080080021";

    //종이슬로건
    public final static String PRODUCT_SLOGAN = "00800900210001";

    //미니배너
    public final static String PRODUCT_MINI_BANNER_BASIC = "00800900220001";
    public final static String PRODUCT_MINI_BANNER_CLEAR = "00800900220002";
    public final static String PRODUCT_MINI_BANNER_CANVAS = "00800900220003";

    //스마트톡
    public final static String PRODUCT_SMART_TALK_CIRCLE = "00802300030001";
    public final static String PRODUCT_SMART_TALK_HEART = "00802300030002";

    // 아크릴 키링
    public final static String ACRYLIC_KEYRING = "00802800060001";

    // 아크릴 스탠드
    public final static String ACRYLIC_STAND = "00802800070001";

    // 반사 슬로건
    public final static String REFLECTIVE_SLOGAN_18X6 = "00802800010001";
    public final static String REFLECTIVE_SLOGAN_60X20 = "00802800010002";

    // 홀로그램 슬로건
    public final static String HOLOGRAPHY_SLOGAN_18X6 = "00802800010003";
    public final static String HOLOGRAPHY_SLOGAN_60X20 = "00802800010004";

    // 매지컬 반사 슬로건
    public final static String MAGICAL_REFLECTIVE_SLOGAN_18X6 = "00802800010005";
    public final static String MAGICAL_REFLECTIVE_SLOGAN_60X20 = "00802800010006";

    // 핀뱃지
    public final static String PIN_BACK_BUTTON_CIRCLE_32X32 = "00802800040001";
    public final static String PIN_BACK_BUTTON_CIRCLE_38X38 = "00802800040002";
    public final static String PIN_BACK_BUTTON_CIRCLE_44X44 = "00802800040003";
    public final static String PIN_BACK_BUTTON_CIRCLE_58X58 = "00802800040004";
    public final static String PIN_BACK_BUTTON_CIRCLE_75X75 = "00802800040005";
    public final static String PIN_BACK_BUTTON_HEART_57X52 = "00802800040006";
    public final static String PIN_BACK_BUTTON_SQUARE_37X37 = "00802800040007";
    public final static String PIN_BACK_BUTTON_SQUARE_50X50 = "00802800040008";

    // 거울뱃지
    public final static String MIRROR_BACK_BUTTON_CIRCLE_58X58 = "00802800040009";
    public final static String MIRROR_BACK_BUTTON_CIRCLE_75X75 = "00802800040010";

    // 자석뱃지
    public final static String MAGNET_BACK_BUTTON_CIRCLE_32X32 = "00802800040011";
    public final static String MAGNET_BACK_BUTTON_CIRCLE_38X38 = "00802800040012";
    public final static String MAGNET_BACK_BUTTON_CIRCLE_44X44 = "00802800040013";
    public final static String MAGNET_BACK_BUTTON_CIRCLE_58X58 = "00802800040014";
    public final static String MAGNET_BACK_BUTTON_HEART_57X52 = "00802800040015";
    public final static String MAGNET_BACK_BUTTON_SQUARE_37X37 = "00802800040016";
    public final static String MAGNET_BACK_BUTTON_SQUARE_50X50 = "00802800040017";

    // 버즈케이스
    public final static String BUDS_CASE = "00802800030003";

    // 에어팟 케이스
    public final static String AIRPODS_CASE = "00802800030001";

    // 에어팟 프로 케이스
    public final static String AIRPODS_PRO_CASE = "00802800030002";

    // 패브릭 포스터
    public final static String FABRIC_POSTER_A1 = "00802800050001";
    public final static String FABRIC_POSTER_A2 = "00802800050002";
    public final static String FABRIC_POSTER_A3 = "00802800050003";

    // 틴 케이스
    public final static String TIN_CASE_S_V = "00802800020001";
    public final static String TIN_CASE_M_V = "00802800020002";
    public final static String TIN_CASE_L_V = "00802800020003";
    public final static String TIN_CASE_S_H = "00802800020004";
    public final static String TIN_CASE_M_H = "00802800020005";
    public final static String TIN_CASE_L_H = "00802800020006";

    // 대표 폰케이스
    public final static String DEFAULT_PHONE_CASE = "00802400000000";

    // 씰 스티커
    public final static String SEAL_STICKER = "00801200040001";

    public static boolean isSloganProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_SLOGAN));
    }

    public static boolean isSloganProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_SLOGAN);
    }

    public static boolean isMiniBannerProduct(String productCode) {
        if (productCode == null) return false;
        if (productCode.equals(PRODUCT_MINI_BANNER_BASIC)) return true;
        if (productCode.equals(PRODUCT_MINI_BANNER_CLEAR)) return true;
        if (productCode.equals(PRODUCT_MINI_BANNER_CANVAS)) return true;
        return false;
    }

    public static boolean isMiniBannerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        if (pdCode.equals(PRODUCT_MINI_BANNER_BASIC)) return true;
        if (pdCode.equals(PRODUCT_MINI_BANNER_CLEAR)) return true;
        if (pdCode.equals(PRODUCT_MINI_BANNER_CANVAS)) return true;
        return false;
    }

    public static boolean isNameStickerProduct(String productCode) {
        return productCode != null && (productCode.equals(NAME_STICKER));
    }

    public static boolean isNameStickerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(NAME_STICKER);
    }

    public static boolean isLongPhotoStickerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(LONG_PHOTO_STICKER);
    }

    public static boolean isDIYStickerProduct(String productCode) {
        return productCode != null && (productCode.equals(DIY_STICKER_A4) || productCode.equals(DIY_STICKER_A5) || productCode.equals(DIY_STICKER_A6));
    }

    public static boolean isDIYStickerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && (pdCode.equals(DIY_STICKER_A4) || pdCode.equals(DIY_STICKER_A5) || pdCode.equals(DIY_STICKER_A6));
    }


    public static boolean isAccordionCardProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(ACCORDION_CARD_GROUP))
            return true;
        return false;
    }

    public static boolean isAccordionCardProduct(String productCode) {
        if (productCode.startsWith(ACCORDION_CARD_GROUP))
            return true;
        return false;
    }

    public static boolean isPosterGroupProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(POSTER_GROUP_VERTICAL) || pdCode.startsWith(POSTER_GROUP_HORIZONTAL))
            return true;
        return false;
    }

    public static boolean isPosterGroupProduct(String productCode) {
        if (productCode.startsWith(POSTER_GROUP_VERTICAL) || productCode.startsWith(POSTER_GROUP_HORIZONTAL))
            return true;
        return false;
    }


    public static boolean isNewYearsCardProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_NEW_YEARS_CARD));
    }

    public static boolean isNewYearsCardProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_NEW_YEARS_CARD);
    }

    public static boolean isPhotoCardProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_PHOTO_CARD));
    }

    public static boolean isTransparencyPhotoCardProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_TRANSPARENCY_PHOTO_CARD));
    }

    public static boolean isSmartTalkProduct(String productCode) {
        if (productCode == null) return false;
        if (productCode.equals(PRODUCT_SMART_TALK_CIRCLE)) return true;
        if (productCode.equals(PRODUCT_SMART_TALK_HEART)) return true;
        return false;
    }

    public static boolean isSmartTalkProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        if (pdCode.equals(PRODUCT_SMART_TALK_CIRCLE)) return true;
        if (pdCode.equals(PRODUCT_SMART_TALK_HEART)) return true;
        return false;
    }

    public static boolean isAcrylicKeyringProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode.equalsIgnoreCase(ACRYLIC_KEYRING);
    }

    public static boolean isAcrylicKeyringProduct(String productCode) {
        return productCode != null && (productCode.equals(ACRYLIC_KEYRING));
    }

    public static boolean isPhotoCardProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_PHOTO_CARD);
    }

    public static boolean isTransparencyPhotoCardProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_TRANSPARENCY_PHOTO_CARD);
    }

    public static boolean isNewWalletProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_NEW_WALLET_PHOTO));
    }

    public static boolean isNewWalletProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_NEW_WALLET_PHOTO);
    }

    public static boolean isPolaroidPackProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_PACKAGE_POLAROID));
    }

    public static boolean isPolaroidPackProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_PACKAGE_POLAROID);
    }

    public static boolean isNewPolaroidPackProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_PACKAGE_NEW_POLAROID)) || (productCode.equals(PRODUCT_PACKAGE_NEW_POLAROID_MINI));
    }

    public static boolean isNewPolaroidPackProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && (pdCode.equals(PRODUCT_PACKAGE_NEW_POLAROID)) || (pdCode.equals(PRODUCT_PACKAGE_NEW_POLAROID_MINI));
    }

    public static boolean isWoodBlockProduct(String productCode) {
        return productCode != null && productCode.equals(PRODUCT_PACKAGE_WOOD_BLOCK);
    }

    public static boolean isWoodBlockProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_PACKAGE_WOOD_BLOCK);
    }

    public static boolean isSquareProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_PACKAGE_SQUARE_HORIZONTAL) || productCode.equals(PRODUCT_PACKAGE_SQUARE_VERTICAL));
    }

    public static boolean isSquareProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && (pdCode.equals(PRODUCT_PACKAGE_SQUARE_HORIZONTAL) || pdCode.equals(PRODUCT_PACKAGE_SQUARE_VERTICAL));
    }

    public static boolean isPostCardProduct(String productCode) {
        return productCode != null && (productCode.equals(PRODUCT_PACKAGE_POST_CARD_HORIZONTAL) || productCode.equals(PRODUCT_PACKAGE_POST_CARD_VERTICAL));
    }

    public static boolean isPostCardProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && (pdCode.equals(PRODUCT_PACKAGE_POST_CARD_HORIZONTAL) || pdCode.equals(PRODUCT_PACKAGE_POST_CARD_VERTICAL));
    }

    public static boolean isTtabujiProduct(String productCode) {
        return productCode != null && productCode.equals(PRODUCT_PACKAGE_TTAEBUJI);
    }

    public static boolean isTtabujiProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_PACKAGE_TTAEBUJI);
    }

    public static boolean isAcrylicStandProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode.equalsIgnoreCase(Const_PRODUCT.ACRYLIC_STAND);
    }

    public static boolean isAcrylicStandProduct(String productCode) {
        return productCode != null && (productCode.equals(ACRYLIC_STAND));
    }

    public static boolean isButtonProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                PIN_BACK_BUTTON_CIRCLE_38X38, PIN_BACK_BUTTON_CIRCLE_44X44,
                PIN_BACK_BUTTON_CIRCLE_58X58, PIN_BACK_BUTTON_CIRCLE_75X75,
                PIN_BACK_BUTTON_CIRCLE_32X32, PIN_BACK_BUTTON_HEART_57X52,
                PIN_BACK_BUTTON_SQUARE_37X37, PIN_BACK_BUTTON_SQUARE_50X50,

                MAGNET_BACK_BUTTON_CIRCLE_32X32, MAGNET_BACK_BUTTON_CIRCLE_38X38,
                MAGNET_BACK_BUTTON_CIRCLE_44X44, MAGNET_BACK_BUTTON_CIRCLE_58X58,
                MAGNET_BACK_BUTTON_HEART_57X52, MAGNET_BACK_BUTTON_SQUARE_37X37,
                MAGNET_BACK_BUTTON_SQUARE_50X50,

                MIRROR_BACK_BUTTON_CIRCLE_58X58, MIRROR_BACK_BUTTON_CIRCLE_75X75
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isButtonProduct(String productCode) {
        if (productCode == null) return false;

        if (productCode.equals(PIN_BACK_BUTTON_CIRCLE_38X38) || productCode.equals(PIN_BACK_BUTTON_CIRCLE_44X44)
                || productCode.equals(PIN_BACK_BUTTON_CIRCLE_58X58) || productCode.equals(PIN_BACK_BUTTON_CIRCLE_75X75)
                || productCode.equals(PIN_BACK_BUTTON_CIRCLE_32X32) || productCode.equals(PIN_BACK_BUTTON_HEART_57X52)
                || productCode.equals(PIN_BACK_BUTTON_SQUARE_37X37) || productCode.equals(PIN_BACK_BUTTON_SQUARE_50X50)
                || productCode.equals(MAGNET_BACK_BUTTON_CIRCLE_32X32) || productCode.equals(MAGNET_BACK_BUTTON_CIRCLE_38X38)
                || productCode.equals(MAGNET_BACK_BUTTON_CIRCLE_44X44) || productCode.equals(MAGNET_BACK_BUTTON_CIRCLE_58X58)
                || productCode.equals(MAGNET_BACK_BUTTON_HEART_57X52) || productCode.equals(MAGNET_BACK_BUTTON_SQUARE_37X37)
                || productCode.equals(MAGNET_BACK_BUTTON_SQUARE_50X50)
                || productCode.equals(MIRROR_BACK_BUTTON_CIRCLE_58X58) || productCode.equals(MIRROR_BACK_BUTTON_CIRCLE_75X75)) {
            return true;
        }
        return false;
    }

    public static boolean isMagicalReflectiveSloganProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                MAGICAL_REFLECTIVE_SLOGAN_18X6, MAGICAL_REFLECTIVE_SLOGAN_60X20
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMagicalReflectiveSloganProduct(String productCode) {
        return productCode != null && (productCode.equals(MAGICAL_REFLECTIVE_SLOGAN_18X6) || productCode.equals(MAGICAL_REFLECTIVE_SLOGAN_60X20));
    }

    public static boolean isReflectiveSloganProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                REFLECTIVE_SLOGAN_18X6, REFLECTIVE_SLOGAN_60X20
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReflectiveSloganProduct(String productCode) {
        return productCode != null && (productCode.equals(REFLECTIVE_SLOGAN_18X6) || productCode.equals(REFLECTIVE_SLOGAN_60X20));
    }

    public static boolean isHolographySloganProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                HOLOGRAPHY_SLOGAN_18X6, HOLOGRAPHY_SLOGAN_60X20
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHolographySloganProduct(String productCode) {
        return productCode != null && (productCode.equals(HOLOGRAPHY_SLOGAN_18X6) || productCode.equals(HOLOGRAPHY_SLOGAN_60X20));
    }

    public static boolean isBudsCaseProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode.equalsIgnoreCase(Const_PRODUCT.BUDS_CASE);
    }

    public static boolean isBudsCaseProduct(String productCode) {
        return productCode != null && (productCode.equals(BUDS_CASE));
    }

    public static boolean isAirpodsCaseProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                AIRPODS_CASE, AIRPODS_PRO_CASE
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAirpodsCaseProduct(String productCode) {
        return productCode != null && (productCode.equals(AIRPODS_CASE) || productCode.equals(AIRPODS_PRO_CASE));
    }

    public static boolean isFabricPosterProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                FABRIC_POSTER_A1, FABRIC_POSTER_A2, FABRIC_POSTER_A3
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFabricPosterProduct(String productCode) {
        return productCode != null && (productCode.equals(FABRIC_POSTER_A1) || productCode.equals(FABRIC_POSTER_A2) || productCode.equals(FABRIC_POSTER_A3));
    }

    public static boolean isTinCaseProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        final String[] arrProducts = {
                TIN_CASE_S_V, TIN_CASE_M_V, TIN_CASE_L_V, TIN_CASE_S_H, TIN_CASE_M_H, TIN_CASE_L_H
        };

        for (String product : arrProducts) {
            if (pdCode.equals(product)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTinCaseProduct(String productCode) {
        if (productCode == null) return false;
        final String[] arrProducts = {
                TIN_CASE_S_V, TIN_CASE_M_V, TIN_CASE_L_V,
                TIN_CASE_S_H, TIN_CASE_M_H, TIN_CASE_L_H};
        for (String product : arrProducts)
            if (productCode.equals(product)) return true;
        return false;
    }

    public static boolean isPackageProduct(String productCode) {
        if (productCode == null) return false;
        final String[] arrProducts = {
                PRODUCT_PACKAGE_WOOD_BLOCK, PRODUCT_PACKAGE_SQUARE_HORIZONTAL, PRODUCT_PACKAGE_SQUARE_VERTICAL,
                PRODUCT_PACKAGE_POST_CARD_HORIZONTAL, PRODUCT_PACKAGE_POST_CARD_VERTICAL,
                PRODUCT_PACKAGE_TTAEBUJI, PRODUCT_PACKAGE_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID_MINI};
        for (String product : arrProducts)
            if (productCode.equals(product)) return true;
        return false;
    }

    public static boolean isCardProduct(String productCode) {
        if (productCode == null) return false;
        final String[] arrProducts = {
                PRODUCT_CARD_5_7_NORMAL_ORIGINAL,
                PRODUCT_CARD_5_7_NORMAL_WIDE,
                PRODUCT_CARD_5_7_FOLDER_ORIGINAL,
                PRODUCT_CARD_5_7_FOLDER_WIDE
        };
        for (String product : arrProducts)
            if (productCode.equals(product)) return true;
        return false;
    }

    public static boolean isCardProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        final String[] arrProducts = {
                PRODUCT_CARD_5_7_NORMAL_ORIGINAL,
                PRODUCT_CARD_5_7_NORMAL_WIDE,
                PRODUCT_CARD_5_7_FOLDER_ORIGINAL,
                PRODUCT_CARD_5_7_FOLDER_WIDE
        };
        for (String product : arrProducts)
            if (pdCode.equals(product)) return true;
        return false;
    }

    public static boolean isCardShapeFolder() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        final String[] arrProducts = {
                PRODUCT_CARD_5_7_FOLDER_ORIGINAL,
                PRODUCT_CARD_5_7_FOLDER_WIDE
        };
        for (String product : arrProducts)
            if (pdCode.equals(product)) return true;
        return false;
    }

    public static boolean isCardShapeWide() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        final String[] arrProducts = {
                PRODUCT_CARD_5_7_NORMAL_WIDE,
                PRODUCT_CARD_5_7_FOLDER_WIDE
        };
        for (String product : arrProducts)
            if (pdCode.equals(product)) return true;
        return false;
    }

    public static boolean isCardShapeNormal() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        final String[] arrProducts = {
                PRODUCT_CARD_5_7_NORMAL_WIDE,
                PRODUCT_CARD_5_7_NORMAL_ORIGINAL
        };
        for (String product : arrProducts)
            if (pdCode.equals(product)) return true;
        return false;
    }

    //양면 인쇄 제품..
    public static boolean isBothSidePrintProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        final String[] arrProducts = {PRODUCT_PACKAGE_POST_CARD_HORIZONTAL, PRODUCT_PACKAGE_POST_CARD_VERTICAL,
                PRODUCT_PACKAGE_NEW_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID_MINI, PRODUCT_NEW_YEARS_CARD};
        for (String product : arrProducts)
            if (pdCode.equals(product)) {
                return true;
            }
        return false;
    }

    public static boolean isPackageProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode == null) return false;
        final String[] arrProducts = {PRODUCT_PACKAGE_WOOD_BLOCK, PRODUCT_PACKAGE_SQUARE_HORIZONTAL, PRODUCT_PACKAGE_SQUARE_VERTICAL,
                PRODUCT_PACKAGE_POST_CARD_HORIZONTAL, PRODUCT_PACKAGE_POST_CARD_VERTICAL,
                PRODUCT_PACKAGE_TTAEBUJI, PRODUCT_PACKAGE_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID, PRODUCT_PACKAGE_NEW_POLAROID_MINI};
        for (String product : arrProducts)
            if (pdCode.equals(product)) return true;
        return false;
    }

    public static final float AURATEXT_BASIC_RATION = 1.f;// 1.75f;
    public static final float AURATEXT_RATION = 1.85f;// 1.75f;
    public static final float AURATEXT_RATION_FACEBOOK = 1.f;// 1.75f;
    public static final float AURATEXT_RATION_INSTAGRAM_BOOK = 1.f;// 1.75f;


    public static boolean isPhotoMugCupProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(PRODUCT_PHOTO_MUGCUP))
            return true;

        return false;
    }

    public static boolean isPhotoMugCupProduct(String productCode) {
        if (productCode.equals(PRODUCT_PHOTO_MUGCUP))
            return true;

        return false;
    }

    public static boolean isTumblerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(PRODUCT_TUMBLR))
            return true;
        else if (pdCode.equals(PRODUCT_TUMBLR_GRADE))
            return true;
        return false;
    }

    public static boolean isTumblerProduct(String productCode) {
        if (productCode.equals(PRODUCT_TUMBLR))
            return true;
        else if (productCode.equals(PRODUCT_TUMBLR_GRADE))
            return true;
        return false;
    }

    public static boolean isDesignNoteProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_DESIGN_NOTE_GROUP))
            return true;
        return false;
    }

    public static boolean isDesignNoteProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_DESIGN_NOTE_GROUP))
            return true;
        return false;
    }

    public static boolean isStikerGroupProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_STICKER_GROUP))
            return true;
        return false;
    }

    public static boolean isStikerGroupProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_STICKER_GROUP))
            return true;
        return false;
    }

    public static boolean isBabyNameStikerGroupProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(BABY_NAME_STICKER_GROUP))
            return true;
        return false;
    }

    public static boolean isBabyNameStikerGroupProduct(String productCode) {
        if (productCode.startsWith(BABY_NAME_STICKER_GROUP))
            return true;
        return false;
    }

    public static boolean isExamStikerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(EXAM_STICKER);
    }

    public static boolean isNormalNoteProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_DESIGN_NORMAL_NOTE);
    }

    public static boolean isSpringNoteProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && pdCode.equals(PRODUCT_DESIGN_SPRING_NOTE);
    }

    public static boolean isLegacyPhoneCaseProduct() {
        return isLegacyPhoneCaseProduct(SnapsProductInfoManager.getInstance().getPROD_CODE());
    }

    public static boolean isLegacyPhoneCaseProduct(String productCode) {
        return productCode.startsWith(PRODUCT_HARD_PHONE_CASE_GROUP) || productCode.startsWith(PRODUCT_BUMPER_PHONE_CASE_GROUP);
    }

    public static boolean isUvPhoneCaseProduct() {
        return isUvPhoneCaseProduct(SnapsProductInfoManager.getInstance().getPROD_CODE());
    }

    public static boolean isUvPhoneCaseProduct(String productCode) {
        return productCode.startsWith(PRODUCT_UV_PHONE_CASE_GROUP);
    }

    public static boolean isPrintPhoneCaseProduct() {
        return isPrintPhoneCaseProduct(SnapsProductInfoManager.getInstance().getPROD_CODE());
    }

    public static boolean isPrintPhoneCaseProduct(String productCode) {
        return productCode.startsWith(PRODUCT_PRINT_HARD_PHONE_CASE_GROUP);
    }

    public static boolean isMousePadProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_MOUSEPAD_GROUP))
            return true;
        return false;
    }

    public static boolean isMousePadProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_MOUSEPAD_GROUP))
            return true;
        return false;
    }

    /***
     * Margin and padding
     */
    public static final int[] WOOD_FRAME_BORDER_PADDING = {15, 5};
    public static final int WOOD_FRAME_SHADOW_PADDING = 10;
    //    public static final float WOOD_FRAME_SHADOW_ONLY_PADDING = 3.5f;
//    public static final int[] WOOD_FRAME_SHADOW_ONLY_SIZE = { 0, 0, 0, 0 };
    public static final int WOOD_FRAME_SHADOW_CONTENT_SIZE = 5;
    public static final float[] WOOD_FRAME_OUTER_SHADOW_SIZE = {3f, 0f, 3f, 11f};


    //	public static final int[] MARVEL_FRAME_MARGIN_LIST = {0, 6, 6, 0};
    public static final float[] MARVEL_FRAME_MARGIN_LIST = {6f, 0f, 6f, 1f};
    public static final int MARVEL_FRAME_SHADOW_CONTENT_SIZE = 20;
    public static final float[] MARVEL_FRAME_OUTER_SHADOW_SIZE = {29, 0.95f, 28.7f, 3};


//	public static final int[] METAL_FRAME_MARGIN_LIST = {2, 2, 2, 2}; // old
//    public static final float[] METAL_FRAME_MARGIN_LIST = {6f, 0f, 6f, 0.6f};

    public static final int METAL_FRAME_SHADOW_CONTENT_SIZE = 20;
    public static final float[] METAL_FRAME_OUTER_SHADOW_SIZE = {29, 0.5f, 28.7f, 2f};

    public static final int METAL_FRAME_SHADOW_PADDING = 10;
    public static final int[] METAL_FRAME_SHADOW_ONLY_SIZE = {0, 0, 0, 0};

    public static final int[] CALENDAR_FRAME_MARGIN_LIST = {0, 0, 0, 0};

    //	public static final int[] MOUSE_PAD_MARGIN_LIST = {0, 0, 6, 6}; // old skin
    public static final int[] MOUSE_PAD_MARGIN_LIST = {25, 26, 24, 24};

    public static final int[] PHOTO_MUG_MARGIN_LIST = {0, 0, 0, 0};

    //	public static final int[] WOOD_BLOCK_MARGIN_LIST = {26, 25, 25, 26}; // old
    public static final int[] WOOD_BLOCK_MARGIN_LIST = {50, 0, 50, 150};

    public static final float[] ACRYL_FRAME_MARGIN_LIST = {33, 0, 33, 47};
    public static final float[] BOARD_FRAME_MARGIN_LIST = {34, 0, 34, 55};


//    public static final int[] WOOD_BLOCK_CALENDAR_MARGIN_LIST = {115, 0, 0, 230};
//    public static final int[] WOOD_BLOCK_CALENDAR_MARGIN_LIST = {0, 148, 0, 100};

    //	public static final int[] POST_CARD_MARGIN_LIST = {30, 28, 28, 30}; // old
    public static final int[] POST_CARD_MARGIN_LIST = {0, 0, 0, 0};

    public static final int INTERIOR_FRAME_SIMPLE_MARGIN = 30;
    public static final int INTERIOR_FRAME_CLASSIC_MARGIN = 30;
    public static final int INTERIOR_FRAME_ROYAL_MARGIN = 30;
    public static final int INTERIOR_FRAME_ANTIQUE_MARGIN = 30;
    public static final int INTERIOR_FRAME_VINTAGE_MARGIN = 30;

    public static final int[] TTAEBUJI_MARGIN_LIST = {25, 25, 25, 25};

    //	public static final int[] SQUARE_MARGIN_LIST = {32, 28, 28, 32};
    public static final int[] SQUARE_MARGIN_LIST = {0, 0, 0, 0};

    public static final int[] TUMBLER_MARGIN_LIST = {10, 10, 10, 10};

    public static final int[] POLAROID_PACKAGE_MARGIN_LIST = {19, 18, 18, 19};

    public static final int[] NEW_YEARS_CARD_MARGIN_LIST = {0, 0, 0, 0};

    //LEFT TOP RIGHT BOTTOM old skin
//	public static final int[] CARD_COVER_NORMAL_MARGIN_LIST 		= {9, 17, 20, 9};
//	public static final int[] CARD_PAGE_NORMAL_MARGIN_LIST 			= {9, 17, 20, 9};
//
//	public static final int[] CARD_COVER_NORMAL_WIDE_MARGIN_LIST 	= {12, 17, 29, 9};
//	public static final int[] CARD_PAGE_NORMAL_WIDE_MARGIN_LIST 	= {9, 17, 20, 9};
//
//	public static final int[] CARD_COVER_FOLDER_WIDE_MARGIN_LIST 	= {8, 8, 42, 8};
//	public static final int[] CARD_PAGE_FOLDER_WIDE_MARGIN_LIST 	= {14, 12, 11, 14};
//
//	public static final int[] CARD_COVER_FOLDER_MARGIN_LIST 		= {6, 22, 35, 6};
//	public static final int[] CARD_PAGE_FOLDER_MARGIN_LIST 			= {14, 12, 11, 14};

    public static final int[] CARD_COVER_NORMAL_MARGIN_LIST = {49, 22, 50, 15};
    public static final int[] CARD_PAGE_NORMAL_MARGIN_LIST = {9, 17, 20, 9};

    public static final int[] CARD_COVER_NORMAL_WIDE_MARGIN_LIST = {43, 24, 47, 16};
    public static final int[] CARD_PAGE_NORMAL_WIDE_MARGIN_LIST = {9, 17, 20, 9};

    public static final int[] CARD_COVER_FOLDER_WIDE_MARGIN_LIST = {50, 29, 70, 42};
    public static final int[] CARD_PAGE_FOLDER_WIDE_MARGIN_LIST = {14, 12, 11, 14};

    public static final int[] CARD_COVER_FOLDER_MARGIN_LIST = {47, 25, 45, 17};
    public static final int[] CARD_PAGE_FOLDER_MARGIN_LIST = {14, 12, 11, 14};

    public static final int[] CALENDAR_PAGE_MARGIN_LIST = {0, 10, 0, 0};

    public static final int[] PHOTO_CARD_MARGIN_LIST = {64, 10, 64, 9};

    /***
     * 현재 상품이 마블액자 유무
     */
    public static boolean isMarvelFrame() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_MARVEL_FRAME))
            return true;

        return false;
    }

    /***
     * 현재 상품이 마블액자 유무ㅌㅌㅌㅌ@
     *
     * @param productCode
     * @return
     */
    public static boolean isMarvelFrame(String productCode) {
        if (productCode.startsWith(PRODUCT_MARVEL_FRAME))
            return true;

        return false;
    }

    /***
     * 상품이 메탈액자인지 확인하는 함수
     *
     * @param productCode
     * @return
     */
    public static boolean isMetalFrame(String productCode) {
        if (productCode.startsWith(PRODUCT_METAL_FRAME))
            return true;
        return false;
    }

    /***
     * 메탈액자인지 확인하는 함수.
     *
     * @return
     */
    public static boolean isMetalFrame() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_METAL_FRAME))
            return true;
        return false;
    }

    /***
     * 상품이 원목액자인지 확인하는 함수
     *
     * @param productCode
     * @return
     */
    public static boolean isWoodFrame(String productCode) {
        if (productCode.startsWith(PRODUCT_WOOD_FRAME))
            return true;
        return false;
    }

    /***
     * 원목액자인지 확인하는 함수.
     *
     * @return
     */
    public static boolean isWoodFrame() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_WOOD_FRAME))
            return true;
        return false;
    }

    /***
     * 상품이 원목액자인지 확인하는 함수
     *
     * @param productCode
     * @return
     */
    public static boolean isInteiorFrame(String productCode) {
        if (productCode.startsWith(PRODUCT_INTEIOR_FRAME))
            return true;
        return false;
    }

    /***
     * 인테리어액자인지 확인하는 함수.
     *
     * @return
     */
    public static boolean isInteiorFrame() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_INTEIOR_FRAME))
            return true;
        return false;
    }

    public static boolean isFrameProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_FRAME_GROUP) && !isPosterGroupProduct())
            return true;

        if (Config.isCalendar())
            return true;

        return false;
    }

    public static boolean isFrameProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_FRAME_GROUP) && !isPosterGroupProduct(productCode))
            return true;

        if (Config.isCalendar())
            return true;

        return false;
    }

    public static boolean isPremiumAcrylFrameProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_PREMIUM_ACRYL_FRAME))
            return true;

        return false;
    }

    public static boolean isPremiumAcrylFrameProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_PREMIUM_ACRYL_FRAME))
            return true;

        return false;
    }

    public static boolean isBoardFrameProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_FOMAX_FRAME) || pdCode.startsWith(PRODUCT_MULTI_PANNAL_FRAME) || pdCode.startsWith(PRODUCT_PORM_BOARD_FRAME))
            return true;

        return false;
    }

    public static boolean isBoardFrameProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_FOMAX_FRAME) || productCode.startsWith(PRODUCT_MULTI_PANNAL_FRAME) || productCode.startsWith(PRODUCT_PORM_BOARD_FRAME))
            return true;

        return false;
    }

    public static boolean isPolaroidProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_POLAROID_ORIGNAL))
            return true;
        return false;

    }

    public static boolean isPolaroidProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_POLAROID_ORIGNAL))
            return true;
        return false;
    }

    public static boolean isWalletProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_WALLET))
            return true;
        return false;

    }

    public static boolean isWalletProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_WALLET))
            return true;
        return false;
    }

    /***
     * 단일 페이지 상품 군인지 확인하는 함수.
     *
     * @return
     */
    public static boolean isSinglePageProduct() {
        if (Const_PRODUCT.isAcrylicKeyringProduct()) {
            return true;
        }

        if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) return false;

        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();

        if (pdCode.startsWith(PRODUCT_FRAME_GROUP) && !isPosterGroupProduct())
            return true;
        else if (pdCode.startsWith(PRODUCT_MODIFY_PHOTO_GROUP))
            return true;
        else if (Config.isCalendar())
            return true;
        else if (pdCode.startsWith(PRODUCT_MOUSEPAD_GROUP) || pdCode.startsWith(PRODUCT_HARD_PHONE_CASE_GROUP) || pdCode.startsWith(PRODUCT_BUMPER_PHONE_CASE_GROUP))
            return true;
        else if (pdCode.equals(PRODUCT_TUMBLR) || pdCode.equals(PRODUCT_TUMBLR_GRADE) || pdCode.startsWith(PRODUCT_PHOTO_MUGCUP))
            return true;
        else if (isDIYStickerProduct())
            return true;
        else if (isUvPhoneCaseProduct() || isPrintPhoneCaseProduct()) {
            return true;
        }

        return false;
    }

    public static boolean isSinglePageProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_FRAME_GROUP) && !isPosterGroupProduct(productCode))
            return true;
        else if (productCode.startsWith(PRODUCT_MODIFY_PHOTO_GROUP))
            return true;
        else if (productCode.startsWith(PRODUCT_MOUSEPAD_GROUP) || productCode.startsWith(PRODUCT_DESIGN_NOTE_GROUP) || productCode.startsWith(PRODUCT_HARD_PHONE_CASE_GROUP) || productCode.startsWith(PRODUCT_BUMPER_PHONE_CASE_GROUP))
            return true;
        else if (productCode.equals(PRODUCT_TUMBLR) || productCode.equals(PRODUCT_TUMBLR_GRADE) || productCode.startsWith(PRODUCT_PHOTO_MUGCUP))
            return true;
        return false;
    }

    /***
     * 일반 템플릿 상품인지 판단하는 함수..
     *
     * @return
     */
    public static boolean isNormalTemplateProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equalsIgnoreCase(PRODUCT_PHOTO_CARD) || pdCode.equalsIgnoreCase(PRODUCT_NEW_WALLET_PHOTO) || pdCode.equalsIgnoreCase(PRODUCT_TRANSPARENCY_PHOTO_CARD))
            return false;
        else if (pdCode.startsWith(PRODUCT_MODIFY_PHOTO_GROUP) || pdCode.endsWith(Const_PRODUCT.PRODUCT_INTEIOR_FRAME))
            return true;
        return false;
    }

    /**
     * 사용자가 크기를 설정 할 수 있는 상품
     */
    public static boolean isFreeSizeProduct() {
        return isAcrylicKeyringProduct() || isAcrylicStandProduct();
    }

    /***
     * auraorder.xml 생성시 페이지 개념(PageTag)이 있는지 없는지 확인하는 함수 있으면 true, 없으면 false
     *
     * @return
     */
    public static boolean isExistPageTag() {
        // 원목액자,마블액자,폴라로이드,지갑사진은 Page Tag를 사용하지 않는다..
        if (!Const_PRODUCT.isWoodFrame() && !Const_PRODUCT.isMarvelFrame()
                && !Const_PRODUCT.isPolaroidProduct() && !Const_PRODUCT.isWalletProduct()
                && !Const_PRODUCT.isInteiorFrame() && !Config.isIdentifyPhotoPrint()) {
            return true;
        }

        return false;
    }

    public static boolean isLayFlatBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_LAY_FLATBOOK))
            return true;

        return false;
    }

    /***
     * 현재 상품이 심플포토북 유무
     *
     * @param productCode
     * @return
     */
    public static boolean isLayFlatBook(String productCode) {
        if (productCode.startsWith(PRODUCT_LAY_FLATBOOK))
            return true;

        return false;
    }

    // auraorder.xml 생성시 페이지 개념이 있는지 없는지 확인하는 함
    public static boolean isShowPlusButton() {
        if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook()
                || isSinglePageProduct() || isDesignNoteProduct() || isLayFlatBook()
                || isNewKakaoBook() || isSnapsDiary() || isInstagramBook()
                || isFacebookPhotobook() || isPackageProduct() || isCardProduct()
                || isPhotoCardProduct() || isNewWalletProduct() || isNewYearsCardProduct()
                || isStikerGroupProduct() || isAccordionCardProduct() || isPosterGroupProduct()
                || isSloganProduct() || isMiniBannerProduct() || isSmartTalkProduct()
                || isAcrylicKeyringProduct() || isAcrylicStandProduct() || isAirpodsCaseProduct()
                || isBudsCaseProduct() || isFabricPosterProduct() || isButtonProduct() || isTinCaseProduct()
                || isMagicalReflectiveSloganProduct() || isReflectiveSloganProduct() || isHolographySloganProduct()
                || isSealStickerProduct()
        )
            return true;

        return false;
    }

    public static boolean isNewKakaoBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(PRODUCT_NEW_KAKAKO_STORYBOOK_HARD) || pdCode.equals(PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT))
            return true;

        return false;
    }

    public static boolean isNewKakaoBook(String productCode) {
        if (productCode.equals(PRODUCT_NEW_KAKAKO_STORYBOOK_HARD) || productCode.equals(PRODUCT_NEW_KAKAKO_STORYBOOK_SOFT))
            return true;

        return false;
    }

    public static boolean isFacebookPhotobook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(PRODUCT_FACEBOOK_PHOTOBOOK_HARD) || pdCode.equals(PRODUCT_FACEBOOK_PHOTOBOOK_SOFT))
            return true;

        return false;
    }

    public static boolean isFacebookPhotobook(String productCode) {
        if (productCode.equals(PRODUCT_FACEBOOK_PHOTOBOOK_HARD) || productCode.equals(PRODUCT_FACEBOOK_PHOTOBOOK_SOFT))
            return true;

        return false;
    }

    public static boolean isInstagramBook() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(PRODUCT_INSTAGRAM_BOOK_HARD) || pdCode.equals(PRODUCT_INSTAGRAM_BOOK_SOFT))
            return true;

        return false;
    }

    public static boolean isInstagramBook(String productCode) {
        if (productCode.equals(PRODUCT_INSTAGRAM_BOOK_HARD) || productCode.equals(PRODUCT_INSTAGRAM_BOOK_SOFT))
            return true;

        return false;
    }

    public static boolean isSnapsDiary(String productCode) {
        if (productCode.equals(PRODUCT_SNAPS_DIARY_HARD) || productCode.equals(PRODUCT_SNAPS_DIARY_SOFT))
            return true;

        return false;
    }

    public static boolean isSnapsDiary() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(PRODUCT_SNAPS_DIARY_HARD) || pdCode.equals(PRODUCT_SNAPS_DIARY_SOFT))
            return true;

        return false;
    }

    public static boolean isSNSBook() {
        return isNewKakaoBook() || isFacebookPhotobook() || isInstagramBook() || isSnapsDiary();
    }

    public static boolean isSNSBook(String productCode) {
        return isNewKakaoBook(productCode) || isFacebookPhotobook(productCode) || isInstagramBook(productCode) || isSnapsDiary(productCode);
    }

    //탭이 없는 서브 메뉴의 상품 예외 처리
    public static boolean checkDetailProduct(String url) {
        if (url == null || url.length() < 1) return false;
        try {
            HashMap<String, String> params = StringUtil.parseUrl(url);
            if (params == null) return false;

            String sClssCode = params.get("F_SCLSS_CODE");
            if (sClssCode != null) {
                //DETAIL_PRODUCT쪽으로 보낼.. SCLASS_CODE
                /**
                 * 달력, 서브 화면...
                 */
                final String[] ARR_CODES = {"001003011000", "001003009000", "001002001000", "001004001000", "001004002000", "001004003000", "001001016000", //달력
                        "001002002000" //지갑용 사진,
                };

                for (String str : ARR_CODES) {
                    if (str.equals(sClssCode))
                        return true;
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return false;
    }

    /**
     * 행잉 액자인지 체크하는 함수
     *
     * @param productCode
     * @return
     */
    public static boolean isHangingFrameProduct(String productCode) {
        if (productCode.startsWith(PRODUCT_HANGING_FRAME_GROUP))
            return true;
        return false;
    }

    public static boolean isHangingFrameProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(PRODUCT_HANGING_FRAME_GROUP))
            return true;
        return false;
    }

    public static boolean iAccessoryPhotoAlbum(String productCode) {

        if (productCode.equals(ACCESSORY_PHOTO_CARD_ALBUM))
            return true;

        return false;
    }

    public static boolean iAccessoryPhotoAlbum() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(ACCESSORY_PHOTO_CARD_ALBUM))
            return true;

        return false;
    }

    public static boolean iAccessoryGiftBag(String productCode) {
        if (productCode.equals(ACCESSORY_GIFT_BAG))
            return true;

        return false;
    }

    public static boolean iAccessoryGiftBag() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(ACCESSORY_GIFT_BAG))
            return true;

        return false;
    }

    public static boolean iAccessoryPhotoBookGiftBox(String productCode) {
        if (productCode.equals(ACCESSORY_PHOTO_BOOK_GIFT_BOX))
            return true;

        return false;
    }

    public static boolean iAccessoryPhotoBookGiftBox() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(ACCESSORY_PHOTO_BOOK_GIFT_BOX))
            return true;

        return false;
    }

    public static boolean iAccessoryPhotoBookGiftBoxAdvance(String productCode) {
        if (productCode.equals(ACCESSORY_PHOTO_BOOK_GIFT_BOX_ADVANCE))
            return true;

        return false;
    }

    public static boolean iAccessoryPhotoBookGiftBoxAdvance() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.equals(ACCESSORY_PHOTO_BOOK_GIFT_BOX_ADVANCE))
            return true;

        return false;
    }

    public static boolean isAccessoryProductGroup(String productCode) {
        if (productCode.startsWith(ACCESSORY_GROUP))
            return true;

        return false;
    }

    public static boolean isAccessoryProductGroup() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        if (pdCode.startsWith(ACCESSORY_GROUP))
            return true;

        return false;
    }

    public static boolean isMultiImageSelectProduct() {
        return Config.isPhotobooks() || Config.isSnapsPhotoPrint() || isNewPolaroidPackProduct()
                || isPolaroidPackProduct() || isSquareProduct() || isWoodBlockProduct() || isPostCardProduct()
                || isTtabujiProduct() || Config.isCalendar() || Const_PRODUCT.isTransparencyPhotoCardProduct()
                || Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isNewWalletProduct()
                || Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isDesignNoteProduct()
                || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isStikerGroupProduct() || Const_PRODUCT.isPhotoCardProduct()
                || Const_PRODUCT.isPosterGroupProduct() || Const_PRODUCT.isSealStickerProduct();
    }

    public static boolean isCartThumb720x720(String product) {
        if (Const_PRODUCT.isNewPolaroidPackProduct(product)) return true;
        return false;
    }

    public static boolean isCartThumb720x720() {
        if (Const_PRODUCT.isNewPolaroidPackProduct()) return true;
        return false;
    }

    public static boolean isSealStickerProduct(String productCode) {
        return productCode != null && (productCode.equals(SEAL_STICKER));
    }

    public static boolean isSealStickerProduct() {
        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        String pdCode = productInfoManager.getPROD_CODE();
        return pdCode != null && (pdCode.equals(SEAL_STICKER));
    }
}
