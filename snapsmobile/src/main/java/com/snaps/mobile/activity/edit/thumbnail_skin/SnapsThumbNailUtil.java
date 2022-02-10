package com.snaps.mobile.activity.edit.thumbnail_skin;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.constant.Const_ThumbNail;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.skin.SnapsSkinConstants;
import com.snaps.mobile.activity.edit.spc.MiniBannerCanvas;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by kimduckwon on 2018. 3. 12..
 */

public class SnapsThumbNailUtil {

    private static final String TAG = SnapsThumbNailUtil.class.getSimpleName();

    public interface SnapsSkinLoadListener {
        void onSkinLoaded(Bitmap bitmap);
    }

    public static void downSkinImage(Context context, String product, final SnapsSkinLoadListener listener) {
        String url = SnapsAPI.DOMAIN() + SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_RESOURCE_URL + product + ".png";

        int width = 480;
        int height = 480;
        if (Const_PRODUCT.isCartThumb720x720()) {
            width = 720;
            height = 720;
        }

        ImageLoader.with(context).load(url).override(width, height).skipMemoryCache(false).into(new SnapsCustomTargets<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                listener.onSkinLoaded(resource);
            }
        });
    }

    public static String[] getThumbNailData(String productCode) {
        return getThumbNailData(productCode, null);
    }

    public static String[] getThumbNailData(String productCode, SnapsPageCanvas canvas) {
        if (productCode.startsWith(Const_ThumbNail.PRODUCT_HARD_PHONE_CASE_GROUP) || productCode.startsWith(Const_ThumbNail.PRODUCT_BUMPER_PHONE_CASE_GROUP)) {
            return getThumbNailDataPhoneCase(productCode, canvas.getOrgHeight());
        } else {
            return getThumbNailDataDetail(productCode, canvas);
        }
    }

    /**
     * 2020.04.03 폰케이스 통합 작업으로 인해 폰케이스의 height 는 630 으로 고정되서 들어온다.
     *
     * @param productCode
     * @return
     */
    private static String[] getThumbNailDataPhoneCase(String productCode, int templateHeight) {
        Dlog.d("templateHeight " + templateHeight);
        String name = "phone_case_" + productCode;
        String[] result = {name, templateHeight == 630 ? "0.60" : "0.35"}; //0.35
        return result;
    }

    private static String[] getThumbNailDataDetail(String productCode, SnapsPageCanvas canvas) {
        switch (productCode) {
            //포토북류
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_6X6_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_6X6_SOFT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_6X6_E_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_6X6_E_SOFT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_SOFT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_LEATHER:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_HARD_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_LEATHER_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_E_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_E_SOFT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X8_E_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_10X10_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_10X10_LEATHER:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_10X10_HARD_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_10X10_LEATHER_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_10X10_E_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_10X10_E_HARD_LAYFLAT:
            case Const_ThumbNail.PRODUCT_KT_BOOK_6X6_SOFT:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_BOOK_SQ_SOFT;
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_5X7_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_5X7_SOFT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_5X7_LEATHER:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X10_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X10_LEATHER:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X10_HARD_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_8X10_LEATHER_LAYFLAT:
            case Const_ThumbNail.PRODUCT_INSTARGRAM_BOOK_HARD:
            case Const_ThumbNail.PRODUCT_INSTARGRAM_BOOK_SOFT:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_BOOK_WD_SOFT;
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_A4_HARD:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_A4_SOFT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_A4_LEATHER:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_A4_HARD_LAYFLAT:
            case Const_ThumbNail.PRODUCT_PHOTO_BOOK_A4_LEATHER_LAYFLAT:
            case Const_ThumbNail.PRODUCT_FACEBOOK_BOOK_SOFT:
            case Const_ThumbNail.PRODUCT_FACEBOOK_BOOK_HARD:
            case Const_ThumbNail.PRODUCT_KAKAOSTORY_BOOK_SOFT:
            case Const_ThumbNail.PRODUCT_KAKAOSTORY_BOOK_HARD:
            case Const_ThumbNail.PRODUCT_FANBOOK_SOFT_A4:
            case Const_ThumbNail.PRODUCT_FANBOOK_HARD_A4:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_BOOK_A4_SOFT;
            case Const_ThumbNail.PRODUCT_DIARY_SOFT:
            case Const_ThumbNail.PRODUCT_DIARY_HARD:
            case Const_ThumbNail.PRODUCT_FANBOOK_SOFT_A5:
            case Const_ThumbNail.PRODUCT_FANBOOK_HARD_A5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_BOOK_A5_SOFT;
            //사진인화
            case Const_ThumbNail.PRODUCT_PHOTO_PRINT_3X5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_3X5;
            case Const_ThumbNail.PRODUCT_PHOTO_PRINT_4X6:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_4X6;
            case Const_ThumbNail.PRODUCT_PHOTO_PRINT_5X7:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_5X7;
            case Const_ThumbNail.PRODUCT_PHOTO_PRINT_8X10:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_8X10;
            case Const_ThumbNail.PRODUCT_PHOTO_PRINT_11X14:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_11X14;

            //포토카드
            case Const_ThumbNail.PRODUCT_PHOTO_CARD:
            case Const_ThumbNail.PRODUCT_TRANSPARENCY_PHOTO_CARD:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_PHOTO_CARD;

            //증명사진
            case Const_ThumbNail.PRODUCT_IDENTIFY_PHOTO:
                return getIdentifyPhoto(Config.getTMPL_CODE());

            //지갑용사진
            case Const_ThumbNail.PRODUCT_NEW_WALLET_PHOTO:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_WALLET;

            //스퀘어프린트 팩
            case Const_ThumbNail.PRODUCT_PACKAGE_SQUARE_4X4:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_SQUARE_4X4;
            case Const_ThumbNail.PRODUCT_PACKAGE_SQUARE_5X5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_SQUARE_5X5;

            //엽서팩
            case Const_ThumbNail.PRODUCT_PACKAGE_POST_CARD:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_POST_CARD;

            //우드블럭+프린트
            case Const_ThumbNail.PRODUCT_PACKAGE_WOOD_BLOCK:
            case Const_ThumbNail.PRODUCT_WOOD_BLOCK_CALENDAR:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_WOOD_BLOCK;

            //폴라로이드팩
            case Const_ThumbNail.PRODUCT_PACKAGE_NEW_POLAROID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_POLAROID;

            case Const_ThumbNail.PRODUCT_PACKAGE_NEW_POLAROID_MINI:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_POLAROID_MINI;

            //메탈액자 마블액자 아크릴액자 보드액자
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_5X5:
            case Const_ThumbNail.PRODUCT_FRAME_MARBLE_5X5:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_5X5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_5X5;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_5X7:
            case Const_ThumbNail.PRODUCT_FRAME_MARBLE_5X7:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_5X7:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_5X7;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_8X8:
            case Const_ThumbNail.PRODUCT_FRAME_MARBLE_8X8:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_8X8:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_8X8;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_8X10:
            case Const_ThumbNail.PRODUCT_FRAME_MARBLE_8X10:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_8X10:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_8X10:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_8X10:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_8X10:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_8X10;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_10X10:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_10X10:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_10X10:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_10X10:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_10X10:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_10X10;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_11X14:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_11X14:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_11X14:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_11X14:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_11X14:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_11X14;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_12X12:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_12X12:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_12X12:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_12X12:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_12X12:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_12X12;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_12X17:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_12X17:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_12X17:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_12X17:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_12X17:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_12X17;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_14X14:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_14X14:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_14X14:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_14X14:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_14X14:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_14X14;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_16X20:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_16X20:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_16X20:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_16X20:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_16X20:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_16X20;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_A2:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_A2:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_A2:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_A2:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_A2:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_A2;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_A3:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_A3:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_A3:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_A3:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_A3:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_A3;
            case Const_ThumbNail.PRODUCT_FRAME_MATAL_A4:
            case Const_ThumbNail.PRODUCT_FRAME_ACRYL_A4:
            case Const_ThumbNail.PRODUCT_FRAME_FORMBOARD_A4:
            case Const_ThumbNail.PRODUCT_FRAME_FOMEX_A4:
            case Const_ThumbNail.PRODUCT_FRAME_MULTIPANNEL_A4:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_A4;

            //원목액자
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_4X6:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_5X5:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_5X7:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_8X8:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_8X10:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_10X10:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_11X14:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_12X12:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_12X17:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_14X14:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_16X20:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_A2:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_A3:
            case Const_ThumbNail.PRODUCT_FRAME_WOOD_A4:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_WOOD;

            //행잉액자
            case Const_ThumbNail.PRODUCT_HANGING_A1:
                return getHangingFrameA1(canvas.getSnapsPage().info.F_FRAME_ID);
            case Const_ThumbNail.PRODUCT_HANGING_A2:
                return getHangingFrameA2(canvas.getSnapsPage().info.F_FRAME_ID);
            case Const_ThumbNail.PRODUCT_HANGING_A3:
                return getHangingFrameA3(canvas.getSnapsPage().info.F_FRAME_ID);
            //인테리어액자
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR:
                return getInteriorFrame(canvas);
            //탁상달력
            case Const_ThumbNail.PRODUCT_CALENDAR_ORIGINAL_HORIZONTAL:
            case Const_ThumbNail.PRODUCT_CALENDAR_LARGE:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_DESK_ORIGINAL_HORIZONTAL;
            case Const_ThumbNail.PRODUCT_CALENDAR_ORIGINAL_VERTICAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_DESK_ORIGINAL_VERTICAL;
            case Const_ThumbNail.PRODUCT_CALENDAR_SMALL_HORIZONTAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_DESK_SMALL_HORIZONTAL;
            case Const_ThumbNail.PRODUCT_CALENDAR_SMALL_VERTICAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_DESK_SMALL_VERTICAL;
            case Const_ThumbNail.PRODUCT_CALENDAR_MINI:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_MINI;

            //벽걸이달력
            case Const_ThumbNail.PRODUCT_WALL_CALENDAR_ORIGINAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_WAL_ORIGINAL;
            case Const_ThumbNail.PRODUCT_WALL_CALENDAR_LARGE:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_WAL_LARGE;

            //스케줄러
            case Const_ThumbNail.PRODUCT_SCHEDULE_CALENDAR:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CALENDAR_SCHEDULER;

            //카드
            case Const_ThumbNail.PRODUCT_CARD_FLAT_HORIZONTAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CARD_FLAT_HORIZONTAL;
            case Const_ThumbNail.PRODUCT_CARD_FLAT_VERTICAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CARD_FLAT_VERTICAL;
            case Const_ThumbNail.PRODUCT_CARD_FOLDER_HORIZONTAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CARD_FOLDER_HORIZONTAL;
            case Const_ThumbNail.PRODUCT_CARD_FOLDER_VERTICAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_CARD_FOLDER_VERTICAL;

            //포토 텀블러
            case Const_ThumbNail.PRODUCT_THMBLER_LOW:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_THMBLER_LOW;
            case Const_ThumbNail.PRODUCT_THMBLER_HIGH:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_THMBLER_HIGH;

            //머그컵
            case Const_ThumbNail.PRODUCT_MUG_CUP:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_CUP;

            //노트
            case Const_ThumbNail.PRODUCT_DESIGN_NORMAL_NOTE_A5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_NOTE_A5_SOFT;
            case Const_ThumbNail.PRODUCT_DESIGN_NORMAL_NOTE_B5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_NOTE_B5_SOFT;
            case Const_ThumbNail.PRODUCT_DESIGN_SPRING_NOTE_A5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_A5_SPRING;
            case Const_ThumbNail.PRODUCT_DESIGN_SPRING_NOTE_B5:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_B5_SPRING;
            case Const_ThumbNail.PRODUCT_STUDY_PLANNER:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_GIFT_STUDY_PLANNER_A5_SPRING;

            //스티커
            case Const_ThumbNail.PRODUCT_STICKER_ROUND:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_ROUND;
            case Const_ThumbNail.PRODUCT_STICKER_SQUARE:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_SQUARE;
            case Const_ThumbNail.PRODUCT_STICKER_RECTANGLE:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_RECTANGLE;
            case Const_ThumbNail.PRODUCT_STICKER_EXAM:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_CSAT;
            case Const_ThumbNail.PRODUCT_STICKER_NAME:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_NAME;
            case Const_ThumbNail.PRODUCT_STICKER_BIG_RECTANGLE_2:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_BIG_RECTANGEL_2;
            case Const_ThumbNail.PRODUCT_STICKER_LONG_PHOTO:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_LONG_PHOTO;
            case Const_ThumbNail.PRODUCT_STICKER_EXAN_2020:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_EXAM_2020;
            case Const_ThumbNail.PRODUCT_STICKER_STICKERKIT:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_PHOTO;
            case Const_ThumbNail.PRODUCT_STICKER_DIY_A4:
                if (Config.getFRAME_TYPE().equals("389000"))
                    return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_DIY_A4;
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_DIY_A4_LINE;
            case Const_ThumbNail.PRODUCT_STICKER_DIY_A5:
                if (Config.getFRAME_TYPE().equals("389000"))
                    return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_DIY_A5;
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_DIY_A5_LINE;
            case Const_ThumbNail.PRODUCT_STICKER_DIY_A6:
                if (Config.getFRAME_TYPE().equals("389000"))
                    return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_DIY_A6;
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_STICKER_DIY_A6_LINE;

            case Const_ThumbNail.PRODUCT_ACCORDION_CARD_NORMAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_ACCORDION_CARD_NOMAL;
            case Const_ThumbNail.PRODUCT_ACCORDION_CARD_MINI:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_ACCORDION_CARD_MINI;
            case Const_ThumbNail.PRODUCT_POSTER_A2_HORIZONTAL:
            case Const_ThumbNail.PRODUCT_POSTER_A3_HORIZONTAL:
            case Const_ThumbNail.PRODUCT_POSTER_A4_HORIZONTAL:
            case Const_ThumbNail.PRODUCT_POSTER_A2_VERTICAL:
            case Const_ThumbNail.PRODUCT_POSTER_A3_VERTICAL:
            case Const_ThumbNail.PRODUCT_POSTER_A4_VERTICAL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_POSTER;
            case Const_ThumbNail.PRODUCT_SLOGAN:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_SLOGAN;
            case Const_ThumbNail.PRODUCT_STICKER_BABY_NAME_MINI:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_BABY_NAME_STICKER_MINI;
            case Const_ThumbNail.PRODUCT_STICKER_BABY_NAME_SMALL:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_BABY_NAME_STICKER_SMALL;
            case Const_ThumbNail.PRODUCT_STICKER_BABY_NAME_MEDIUM:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_BABY_NAME_STICKER_MEDIUM;
            case Const_ThumbNail.PRODUCT_STICKER_BABY_NAME_LARGE:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_BABY_NAME_STICKER_LARGE;

            case Const_ThumbNail.PRODUCT_SMART_TOK_CIRCLE:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_SNART_TOK_CIRCLE;
            case Const_ThumbNail.PRODUCT_SMART_TOK_HEART:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_SNART_TOK_HEART;

            case Const_ThumbNail.PRODUCT_MINI_BANNER_BASIC:
            case Const_ThumbNail.PRODUCT_MINI_BANNER_CLEAR:
            case Const_ThumbNail.PRODUCT_MINI_BANNER_CANVAS:
                return getMiniBannerType();

            case Const_ThumbNail.PRODUCT_ACRYLIC_KEYRING:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_ACRYLIC_KEYRING;
            case Const_ThumbNail.PRODUCT_ACRYLIC_STAND:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_ACRYLIC_STAND;

            case Const_ThumbNail.PRODUCT_REFLECTIVE_SLOGAN_18X6:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_REFLECTIVE_SLOGAN_18X6;
            case Const_ThumbNail.PRODUCT_REFLECTIVE_SLOGAN_60X20:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_REFLECTIVE_SLOGAN_16X20;

            case Const_ThumbNail.PRODUCT_HOLOGRAPHY_SLOGAN_18X6:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_HOLOGRAPHY_SLOGAN_18X6;
            case Const_ThumbNail.PRODUCT_HOLOGRAPHY_SLOGAN_60X20:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_HOLOGRAPHY_SLOGAN_16X20;

            case Const_ThumbNail.PRODUCT_MAGICAL_REFLECTIVE_SLOGAN_18X6:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGICAL_HOLOGRAPHY_SLOGAN_18X6;
            case Const_ThumbNail.PRODUCT_MAGICAL_REFLECTIVE_SLOGAN_60X20:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGICAL_HOLOGRAPHY_SLOGAN_16X20;

            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_CIRCLE_32X32:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_CIRCLE_32X32;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_CIRCLE_38X38:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_CIRCLE_38X38;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_CIRCLE_44X44:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_CIRCLE_44X44;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_CIRCLE_58X58:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_CIRCLE_58X58;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_CIRCLE_75X75:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_CIRCLE_75X75;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_HEART_57X52:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_HEART_57X52;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_SQUARE_37X37:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_SQUARE_37X37;
            case Const_ThumbNail.PRODUCT_PIN_BACK_BUTTON_SQUARE_50X50:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_PIN_BACK_BUTTON_SQUARE_50X50;

            case Const_ThumbNail.PRODUCT_MIRROR_BACK_BUTTON_CIRCLE_58X58:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MIRROR_BACK_BUTTON_CIRCLE_58X58;
            case Const_ThumbNail.PRODUCT_MIRROR_BACK_BUTTON_CIRCLE_75X75:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MIRROR_BACK_BUTTON_CIRCLE_75X75;

            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_CIRCLE_32X32:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_CIRCLE_32X32;
            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_CIRCLE_38X38:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_CIRCLE_38X38;
            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_CIRCLE_44X44:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_CIRCLE_44X44;
            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_CIRCLE_58X58:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_CIRCLE_58X58;
            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_HEART_57X52:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_HEART_57X52;
            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_SQUARE_37X37:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_SQUARE_37X37;
            case Const_ThumbNail.PRODUCT_MAGNET_BACK_BUTTON_SQUARE_50X50:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_MAGNET_BACK_BUTTON_SQUARE_50X50;

            case Const_ThumbNail.PRODUCT_BUDS_CASE:
                return getBudsCase();
            case Const_ThumbNail.PRODUCT_AIRPODS_CASE:
                return getAirpodsCase();
            case Const_ThumbNail.PRODUCT_AIRPODS_PRO_CASE:
                return getAirpodsProCase();

            case Const_ThumbNail.PRODUCT_FABRIC_POSTER_A1:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_FABRIC_POSTER_A1;
            case Const_ThumbNail.PRODUCT_FABRIC_POSTER_A2:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_FABRIC_POSTER_A2;
            case Const_ThumbNail.PRODUCT_FABRIC_POSTER_A3:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_FABRIC_POSTER_A3;

            case Const_ThumbNail.PRODUCT_TIN_CASE_S_V:
            case Const_ThumbNail.PRODUCT_TIN_CASE_M_V:
            case Const_ThumbNail.PRODUCT_TIN_CASE_L_V:
            case Const_ThumbNail.PRODUCT_TIN_CASE_S_H:
            case Const_ThumbNail.PRODUCT_TIN_CASE_M_H:
            case Const_ThumbNail.PRODUCT_TIN_CASE_L_H:
                return getTinCase(productCode);

            case Const_ThumbNail.PRODUCT_SEAL_STICKER:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SEAL_STICKER;

            default:
                return null;
        }
    }

    private static String[] getTinCase(String productCode) {
        Map<String, Map<String, String[]>> thumbNailMap = new HashMap<>();

        Map<String, String[]> small_vertical = new HashMap<>();
        small_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_WHITE_S_V);
        small_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_BLACK_S_V);
        small_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_SILVER_S_V);
        thumbNailMap.put(Const_ThumbNail.PRODUCT_TIN_CASE_S_V, small_vertical);

        Map<String, String[]> middle_vertical = new HashMap<>();
        middle_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_WHITE_M_V);
        middle_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_BLACK_M_V);
        middle_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_SILVER_M_V);
        thumbNailMap.put(Const_ThumbNail.PRODUCT_TIN_CASE_M_V, middle_vertical);

        Map<String, String[]> large_vertical = new HashMap<>();
        large_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_WHITE_L_V);
        large_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_BLACK_L_V);
        large_vertical.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_SILVER_L_V);
        thumbNailMap.put(Const_ThumbNail.PRODUCT_TIN_CASE_L_V, large_vertical);

        Map<String, String[]> small_horizontal = new HashMap<>();
        small_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_WHITE_S_H);
        small_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_BLACK_S_H);
        small_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_SILVER_S_H);
        thumbNailMap.put(Const_ThumbNail.PRODUCT_TIN_CASE_S_H, small_horizontal);

        Map<String, String[]> middle_horizontal = new HashMap<>();
        middle_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_WHITE_M_H);
        middle_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_BLACK_M_H);
        middle_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_SILVER_M_H);
        thumbNailMap.put(Const_ThumbNail.PRODUCT_TIN_CASE_M_H, middle_horizontal);

        Map<String, String[]> large_horizontal = new HashMap<>();
        large_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_WHITE, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_WHITE_L_H);
        large_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_BLACK, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_BLACK_L_H);
        large_horizontal.put(Const_VALUES.COLOR_CODE_TINCASE_SILVER, SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_TINCASE_SILVER_L_H);
        thumbNailMap.put(Const_ThumbNail.PRODUCT_TIN_CASE_L_H, large_horizontal);

        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        String[] snaps_thumb = thumbNailMap.get(productCode).get(caseColor);
        return snaps_thumb;
    }

    private static String[] getBudsCase() {
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        if (caseColor.equals(Const_VALUES.COLOR_CODE_BLACK)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_BLACK;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_LAVENDER)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_LAVENDAR;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_TRANSPARENCY)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_LIMPIDITY;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_MINT)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_MINT;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_NAVY)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_NAVY;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_PINK)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_PINK;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_SKYBLUE)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_SKYBLUE;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_WHITE)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_WHITE;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_YELLOW)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_BUDS_CASE_YELLOW;
        }

        return null;
    }

    private static String[] getAirpodsCase() {
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        if (caseColor.equals(Const_VALUES.COLOR_CODE_BLACK)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_BLACK;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_LAVENDER)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_LAVENDAR;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_TRANSPARENCY)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_LIMPIDITY;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_MINT)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_MINT;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_NAVY)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_NAVY;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_PINK)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_PINK;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_SKYBLUE)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_SKYBLUE;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_WHITE)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_WHITE;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_YELLOW)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_CASE_YELLOW;
        }

        return null;
    }

    private static String[] getAirpodsProCase() {
        SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
        String caseColor = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);

        if (caseColor.equals(Const_VALUES.COLOR_CODE_BLACK)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_BLACK;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_LAVENDER)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_LAVENDAR;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_TRANSPARENCY)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_LIMPIDITY;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_MINT)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_MINT;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_NAVY)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_NAVY;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_PINK)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_PINK;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_SKYBLUE)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_SKYBLUE;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_WHITE)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_WHITE;
        } else if (caseColor.equals(Const_VALUES.COLOR_CODE_YELLOW)) {
            return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_AIRPODS_PRO_CASE_YELLOW;
        }

        return null;
    }

    private static String[] getMiniBannerType() {
        String productCode = Config.getPROD_CODE();
        String frameType = Config.getFRAME_TYPE();

        if (productCode.equals(Const_PRODUCT.PRODUCT_MINI_BANNER_BASIC)) {
            if (frameType.equals(MiniBannerCanvas.HOLDER)) return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_MINI_BANNER_BASIC_HOLDER;
            if (frameType.equals(MiniBannerCanvas.NOT_HOLDER)) return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_MINI_BANNER_BASIC_NOT_HOLDER;
        }

        if (productCode.equals(Const_PRODUCT.PRODUCT_MINI_BANNER_CLEAR)) {
            if (frameType.equals(MiniBannerCanvas.HOLDER)) return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_MINI_BANNER_CLEAR_HOLDER;
            if (frameType.equals(MiniBannerCanvas.NOT_HOLDER)) return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_MINI_BANNER_CLEAR_NOT_HOLDER;
        }

        if (productCode.equals(Const_PRODUCT.PRODUCT_MINI_BANNER_CANVAS)) {
            if (frameType.equals(MiniBannerCanvas.HOLDER)) return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_MINI_BANNER_CANVAS_HOLDER;
            if (frameType.equals(MiniBannerCanvas.NOT_HOLDER)) return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_MINI_BANNER_CANVAS_NOT_HOLDER;
        }

        return null;
    }

    private static String[] getIdentifyPhoto(String tmpleCode) {
        switch (tmpleCode) {
            case Const_ThumbNail.PRODUCT_IDENTIFY_PHOTO_HALF_NAME_CARD:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_ID_CARD_HALF;
            case Const_ThumbNail.PRODUCT_IDENTIFY_PHOTO_NAME_CARD:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_ID_CARD_FULL;
            case Const_ThumbNail.PRODUCT_IDENTIFY_PHOTO_HALF_PASS_PORT:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_PHOTO_PRINT_ID_CARD_PASSPORT;
            default:
                return null;
        }
    }


    private static String[] getHangingFrameA1(String frameId) {
        switch (frameId) {
            case Const_ThumbNail.PRODUCT_HANGING_NATURAL_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A1_N;
            case Const_ThumbNail.PRODUCT_HANGING_WALNUT_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A1_W;
            case Const_ThumbNail.PRODUCT_HANGING_BLACK_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A1_B;
            default:
                return null;
        }
    }

    private static String[] getHangingFrameA2(String frameId) {
        switch (frameId) {
            case Const_ThumbNail.PRODUCT_HANGING_NATURAL_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A2_N;
            case Const_ThumbNail.PRODUCT_HANGING_WALNUT_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A2_W;
            case Const_ThumbNail.PRODUCT_HANGING_BLACK_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A2_B;
            default:
                return null;
        }
    }

    private static String[] getHangingFrameA3(String frameId) {
        switch (frameId) {
            case Const_ThumbNail.PRODUCT_HANGING_NATURAL_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A3_N;
            case Const_ThumbNail.PRODUCT_HANGING_WALNUT_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A3_W;
            case Const_ThumbNail.PRODUCT_HANGING_BLACK_FRAME_ID:
                return SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_HANGING_A3_B;
            default:
                return null;
        }
    }

    private static String[] getInteriorFrame(SnapsPageCanvas canvas) {
        String frameId = canvas.getSnapsPage().info.F_FRAME_ID;
        boolean orientation = canvas.getSnapsPage().getWidth() > canvas.getSnapsPage().getHeight();
        switch (frameId) {
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_SIMPLE_FLAT_BLACK_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_SIMPLE_HORIZONTAL_BLACK : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_SIMPLE_VERTICAL_BLACK;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_SIMPLE_FLAT_SILVER_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_SIMPLE_HORIZONTAL_SILVER : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_SIMPLE_VERTICAL_SILVER;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_SIMPLE_FLAT_GOLD_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_SIMPLE_HORIZONTAL_GOLD : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_SIMPLE_VERTICAL_GOLD;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_ROYAL_SAND_BROWN_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_ROYAL_HORIZONTAL_BROWN : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_ROYAL_VERTICAL_BROWN;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_ROYAL_SAND_DARK_GOLE_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_ROYAL_HORIZONTAL_DARK_GOLD : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_ROYAL_VERTICAL_DARK_GOLD;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_ROYAL_SAND_SILVER_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_ROYAL_HORIZONTAL_SILVER : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_ROYAL_VERTICAL_SILVER;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_CLASSIC_WOOD_BLACK_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_CLASSIC_HORIZONTAL_BLACK : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_CLASSIC_VERTICAL_BLACK;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_CLASSIC_WOOD_BROWN_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_CLASSIC_HORIZONTAL_BROWN : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_CLASSIC_VERTICAL_BROWN;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_CLASSIC_WOOD_WHITE_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_CLASSIC_HORIZONTAL_WHITE : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_CLASSIC_VERTICAL_WHITE;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_CLASSIC_VINTAGE_MARBLE_BROWN_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_VINTAGE_HORIZONTAL_BROWN : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_VINTAGE_VERTICAL_BROWN;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_CLASSIC_VINTAGE_MARBLE_DARK_GRAY_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_VINTAGE_HORIZONTAL_DARK_GRAY : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_VINTAGE_VERTICAL_DARK_GRAY;
            case Const_ThumbNail.PRODUCT_FRAME_INTERIOR_CLASSIC_VINTAGE_MARBLE_GOLD_FRAME_ID:
                return orientation ? SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_VINTAGE_HORIZONTAL_GOLD : SnapsThumbNailSkinConstants.SNAPS_THUMB_NAIL_SKIN_FRAME_INTERIOR_VINTAGE_VERTICAL_GOLD;

            default:
                return null;

        }
    }


}
