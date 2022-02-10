package com.snaps.mobile.order.order_v2.task.prepare_process;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.instagram.utils.instagram.Const;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

/**
 * Created by ysjeong on 2017. 4. 14..
 */

public class SnapsOrderPrepareHandlerFactory {
    public static SnapsOrderPrepareBaseHandler createOrderPrepareHandler(SnapsOrderAttribute attribute, SnapsOrderActivityBridge snapsOrderActivityBridge) {
        if (SnapsDiaryDataManager.isAliveSnapsDiaryService())
            return SnapsOrderPrepareHandlerForDiary.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Config.isIdentifyPhotoPrint()) {
            if (Config.isPassportPhoto()) {
                return SnapsOrderPrepareHandlerPassportPhoto.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
            } else {
                return SnapsOrderPrepareHandlerIdentifyPhoto.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
            }
        } else if (Config.isSNSBook())
            return SnapsOrderPrepareHandlerForSNSBook.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Config.isSnapsSticker()) //TODO  곧 없어질 제품이다..
            return SnapsOrderPrepareHandlerForTempStickerKit.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct())
            return SnapsOrderPrepareHandlerForPhotoCard.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isNewYearsCardProduct())
            return SnapsOrderPrepareHandlerNewYearsCard.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isCardProduct())
            return SnapsOrderPrepareHandlerForCard.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isStikerGroupProduct())
            return SnapsOrderPrepareHandlerForSticker.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isPosterGroupProduct())
            return SnapsOrderPrepareHandlerForPoster.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isSloganProduct())
            return SnapsOrderPrepareHandlerForSlogan.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isBabyNameStikerGroupProduct())
            return SnapsOrderPrepareHandlerForBabyNameSticker.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Config.isCalendar())
            return SnapsOrderPrepareHandlerForCalendar.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Config.isSmartSnapsRecommendLayoutPhotoBook())
            return SnapsOrderPrepareHandlerRecommendPhotoBook.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct())
            return SnapsOrderPrepareHandlerForNewPhoneCase.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isSealStickerProduct())
            return SnapsOrderPrepareHandlerForSealSticker.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
        else if (Const_PRODUCT.isFrameProduct())
            return SnapsOrderPrepareHandlerForFrameProduct.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);

        return SnapsOrderPrepareHandlerDefault.createInstanceWithAttribute(attribute, snapsOrderActivityBridge);
    }
}
