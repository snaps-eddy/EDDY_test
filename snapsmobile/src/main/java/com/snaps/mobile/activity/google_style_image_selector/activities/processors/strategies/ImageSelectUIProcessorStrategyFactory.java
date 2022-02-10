package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import androidx.annotation.NonNull;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;

import static com.snaps.common.utils.constant.Config.getTMPL_CODE;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyFactory {

    public enum eIMAGE_SELECT_UI_TYPE {
        TEMPLATE,
        SNS,
        EMPTY,
        SMART_SNAPS,
        SMART_ANALYSIS,
        SINGLE_CHOOSE,
        MULTI_CHOOSE,
        IDENTIFY_PHOTO
    }

    public static IImageSelectUIProcessorStrategy createImageSelectUI(@NonNull ImageSelectIntentData intentData) {
        if (intentData.isSinglePhotoChoose()) {
            return new ImageSelectUIProcessorStrategyForSinglePhotoChoose();                     //1장의 사진만 선택하는 형태 (테마북 등..)

        } else if (intentData.isMultiPhotoChoose()) {
            return new ImageSelectUIProcessorStrategyForMultiPhotoChoose();

        } else if (intentData.getSmartSnapsImageSelectType() == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_CHOICE) {
            return new ImageSelectUIProcessorStrategyForSmartSnapsSelect();                                 //트레이가 비어 있는 형태(사진인화, 스티커킷..)

        } else if (intentData.getSmartSnapsImageSelectType() == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT) {
            return new ImageSelectUIProcessorStrategyForSmartRecommendBook();

        } else if (Config.isKTBook()) {
            return new ImageSelectUIProcessorStrategyForKTBook();

        } else if (Config.isSNSBook()) {
            return new ImageSelectUIProcessorStrategyForSNSBookExclude();                        //SNS북 (제외할 스토리 선택)

        } else if (Config.isIdentifyPhotoPrint()) {
            return new ImageSelectUIProcessorStrategyForIdentifyPhoto();

        } else if (Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isPosterGroupProduct() || Const_PRODUCT.isStikerGroupProduct() || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isPhotoCardProduct()) {
            return new ImageSelectUIProcessorStrategyForAddPageProduct();

        } else if (Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            return new ImageSelectUIProcessorStrategyForTransparencyPhotoCard();

        } else if (Config.isCalendar()) {
            if (Config.isWoodBlockCalendar())
                return new ImageSelectUIProcessorStrategyForWoodBlockCalendar();
            else
                return new ImageSelectUIProcessorStrategyForCalendar();
        } else if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
            return new ImageSelectUIProcessorStrategyForDiaryServiceAlive();                      //템플릿이 존재하는 형태(포토북, 액자 등..)

        } else if (Const_PRODUCT.isSealStickerProduct()) {
            return new ImageSelectUIProcessorStrategyForSealSticker();

        } else if (getTMPL_CODE() != null && getTMPL_CODE().length() > 0) {
            return new ImageSelectUIProcessorStrategyForTemplateProducts();                      //템플릿이 존재하는 형태(포토북, 액자 등..)

        } else {
            return new ImageSelectUIProcessorStrategyForEmpty();                                 //트레이가 비어 있는 형태(사진인화, 스티커킷..)

        }
    }
}
