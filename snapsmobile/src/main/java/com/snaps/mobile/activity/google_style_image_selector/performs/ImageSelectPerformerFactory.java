package com.snaps.mobile.activity.google_style_image_selector.performs;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;

import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_ACCORDION_CARD;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_CALENDAR;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_CARD;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_DIY_STICKER;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_FACEBOOK_PHOTOBOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_FRAME;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_KT_BOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_MARVEL_FRAME;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_METAL_FRAME;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_MULTI_CHOOSE_TYPE;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_NEW_KAKAOBOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_NEW_STICKER;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_PACKAGE_KIT;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_PHONE_CASE;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_PHOTO_CARD;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_PHOTO_PRINT;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_POSTER;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SEAL_STICKER;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SIMPLEPHOTO_BOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SIMPLE_MAKING_BOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SINGLE_CHOOSE_TYPE;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SMART_ANALYSIS_PHOTO_BOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SMART_SIMPLEPHOTO_BOOK;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SNAPS_DIARY;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SNAPS_IDENTIFY_PHOTO;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_SNAPS_REMOVE_DIARY;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_STICKER;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_TRANSPARENCY_PHOTO_CARD;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_WALLET_PHOTO;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_WOOD_BLOCK_CALENDAR;
import static com.snaps.common.utils.constant.ISnapsConfigConstants.SELECT_WOOD_FRAME;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectPerformerFactory {

    /**
     * @param imgSelectAct
     * @param product
     * @return
     */
    public static IImageSelectProductPerform createPerformer(ImageSelectActivityV2 imgSelectAct, int product) {
        if (imgSelectAct == null) return null;

        ImageSelectUIProcessor uiProcessor = imgSelectAct.getUIProcessor();

        // 임시로 만들어 놓은 코드. Performer에 대한 정리가 필요.
        if (uiProcessor != null && uiProcessor.isSingleChooseType() && Config.isKTBook()) {
            return new ImageSelectPerformForSingleChooseTypePhoneOnly(imgSelectAct);

        } else if (uiProcessor != null && uiProcessor.isSingleChooseType() && (Const_PRODUCT.isAcrylicKeyringProduct() || Const_PRODUCT.isAcrylicStandProduct())) {
            return new ImageSelectPerformForAcrylicProduct(imgSelectAct);

        } else if (uiProcessor != null && uiProcessor.isSingleChooseType()) {
            return new ImageSelectPerformForSingleChoose(imgSelectAct);

        } else if (uiProcessor != null && uiProcessor.isMultiChooseType()) {
            return new ImageSelectPerformForMultiChooseType(imgSelectAct);
        }

        switch (product) {
            case SELECT_STICKER:
                return new ImageSelectPerformForStickerkit(imgSelectAct);
            case SELECT_PHOTO_PRINT:
                return new ImageSelectPerformForPhotoPrint(imgSelectAct);
            case SELECT_SINGLE_CHOOSE_TYPE:
                return new ImageSelectPerformForSingleChooseType(imgSelectAct);
            case SELECT_MULTI_CHOOSE_TYPE:
                return new ImageSelectPerformForMultiChooseType(imgSelectAct);
            case SELECT_SIMPLEPHOTO_BOOK:
                return new ImageSelectPerformForSimplePhotoBook(imgSelectAct);
            case SELECT_SMART_SIMPLEPHOTO_BOOK:
                return new ImageSelectPerformForSmartSimplePhotoBook(imgSelectAct);
            case SELECT_SMART_ANALYSIS_PHOTO_BOOK:
                return new ImageSelectPerformForSmartRecommendPhotoBook(imgSelectAct);
            case SELECT_CALENDAR:
            case SELECT_WOOD_BLOCK_CALENDAR:
                return new ImageSelectPerformForCalendar(imgSelectAct);
            case SELECT_METAL_FRAME:
            case SELECT_MARVEL_FRAME:
            case SELECT_WOOD_FRAME:
            case SELECT_FRAME:
                return new ImageSelectPerformForFrame(imgSelectAct);
            case SELECT_NEW_KAKAOBOOK:
                return new ImageSelectPerformForNewKakaoBook(imgSelectAct);
            case SELECT_PACKAGE_KIT:
                return new ImageSelectPerformForPackageKit(imgSelectAct);
            case SELECT_SIMPLE_MAKING_BOOK:
                return new ImageSelectPerformForSimpleMakingBook(imgSelectAct);
            case SELECT_KT_BOOK:
                return new ImageSelectPerformForKTBook(imgSelectAct);
//            case SELECT_CARD :
//                return new ImageSelectPerformForCard(imgSelectAct);
            case SELECT_FACEBOOK_PHOTOBOOK:
                return new ImageSelectPerformForFacebookPhotobook(imgSelectAct);
            case SELECT_SNAPS_DIARY:
                return new ImageSelectPerformForDiaryWrite(imgSelectAct);
            case SELECT_SNAPS_REMOVE_DIARY:
                return new ImageSelectPerformForDiaryBookRemoveStory(imgSelectAct);
            case SELECT_SNAPS_IDENTIFY_PHOTO:
                return new ImageSelectPerformForIdentifyPhotoPrint(imgSelectAct);

            case SELECT_TRANSPARENCY_PHOTO_CARD:
                return new ImageSelectPerformForTemplateFreeChoose(imgSelectAct);
            case SELECT_WALLET_PHOTO:
            case SELECT_ACCORDION_CARD:
            case SELECT_PHOTO_CARD:
            case SELECT_CARD:
            case SELECT_NEW_STICKER:
            case SELECT_POSTER:
            case SELECT_DIY_STICKER:
                return new ImageSelectPerformForAddFreeProduct(imgSelectAct);
            case SELECT_SEAL_STICKER:
                return new ImageSelectPerformForSealStickerProduct(imgSelectAct);
            case SELECT_PHONE_CASE:
                return new ImageSelectPerformForPhoneCase(imgSelectAct);
        }

        return null;
    }
}
