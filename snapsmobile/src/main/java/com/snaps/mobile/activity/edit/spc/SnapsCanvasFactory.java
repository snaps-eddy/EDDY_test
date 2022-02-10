package com.snaps.mobile.activity.edit.spc;

import android.app.Activity;
import android.content.Context;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.edit.spc.reflectable_slogan.HolographySloganCanvas;
import com.snaps.mobile.activity.edit.spc.reflectable_slogan.MagicalReflectiveSloganCanvas;
import com.snaps.mobile.activity.edit.spc.reflectable_slogan.ReflectiveSloganCanvas;

import errorhandle.SnapsAssert;

public class SnapsCanvasFactory {

    public enum eSnapsCanvasType {
        SMART_SNAPS_ANALYSIS_PRODUCT_DETAIL_EDIT_CANVAS,
        SMART_SNAPS_ANALYSIS_PRODUCT_EDIT_LIST_ITEM_CANVAS,
        SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COVER_EDIT_CANVAS
    }

    public static SnapsPageCanvas createPageCanvasWithType(eSnapsCanvasType type, Context context) {
        switch (type) {
            case SMART_SNAPS_ANALYSIS_PRODUCT_DETAIL_EDIT_CANVAS:
                return new SmartRecommendBookDetailEditCanvas(context);
            case SMART_SNAPS_ANALYSIS_PRODUCT_EDIT_LIST_ITEM_CANVAS:
                return new SmartRecommendBookEditListItemCanvas(context);
            case SMART_SNAPS_ANALYSIS_PHOTO_BOOK_COVER_EDIT_CANVAS:
                return new SmartRecommendBookCoverEditCanvas(context);
            default:
                SnapsAssert.assertTrue(false);
        }

        return null;
    }

    public SnapsPageCanvas createPageCanvas(Activity activity, String productCode) {

        SnapsPageCanvas canvas = null;

        if (Config.isSnapsSticker(productCode))
            canvas = new StickerPageCanvas(activity);

        else if (Config.isThemeBook(productCode))
            canvas = new ThemeBookCanvas(activity);
        else if (Config.isSimplePhotoBook(productCode) || Const_PRODUCT.isLayFlatBook(productCode)) {
            if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
                canvas = new SmartRecommendBookEditListItemCanvas(activity);
                canvas.setIsPreview(true);
                canvas.setEnableButton(true);
                canvas.setIsPageSaving(false);
                canvas.setLandscapeMode(true);
            } else {
                canvas = new SimplePhotoBookCanvas(activity);
            }
        } else if (Config.isSimpleMakingBook())
            canvas = new SimpleMakingBookCanvas(activity);
        else if (Const_PRODUCT.isMarvelFrame(productCode))
            canvas = new MarvelFrameCanvas(activity);
        else if (Const_PRODUCT.isMetalFrame(productCode))
            canvas = new MetalFrameCanvas(activity);
        else if (Const_PRODUCT.isWoodFrame(productCode))
            canvas = new WoodFrameCanvas(activity);
        else if (Const_PRODUCT.isPremiumAcrylFrameProduct(productCode))
            canvas = new PremiumAcrylFrameCanvas(activity);
        else if (Const_PRODUCT.isBoardFrameProduct(productCode))
            canvas = new BoardFrameCanvas(activity);
        else if (Config.isCalendar()) {
            if (Config.isWoodBlockCalendar())
                canvas = new WoodBlockCalendarCanvas(activity);
            else
                canvas = new CalendarCanvas(activity);
        } else if (Const_PRODUCT.isPhotoMugCupProduct(productCode))
            canvas = new PhotoMugCupCanvas(activity);
        else if (Const_PRODUCT.isTumblerProduct(productCode))
            canvas = new TumblerCanvas(activity);
        else if (Const_PRODUCT.isPolaroidProduct(productCode))
            canvas = new PolaroidCanvas(activity);
        else if (Const_PRODUCT.isWalletProduct())
            canvas = new WalletCanvas(activity);
        else if (Const_PRODUCT.isLegacyPhoneCaseProduct(productCode))
            canvas = new PhoneCaseCanvas(activity);
        else if (Const_PRODUCT.isUvPhoneCaseProduct(productCode) || Const_PRODUCT.isPrintPhoneCaseProduct(productCode))
            canvas = new NewPhoneCaseCanvas(activity);
        else if (Const_PRODUCT.isDesignNoteProduct(productCode))
            canvas = new DesignNoteCanvas(activity);
        else if (Const_PRODUCT.isMousePadProduct(productCode))
            canvas = new MousePadCanvas(activity);
        else if (Const_PRODUCT.isSNSBook(Config.getPROD_CODE()))
            canvas = new SNSBookPageCanvas(activity);
        else if (Const_PRODUCT.isInteiorFrame(productCode)) {
            canvas = new InteiorFrameCanvas(activity);
        } else if (Const_PRODUCT.isWoodBlockProduct(productCode))
            canvas = new WoodBlockCanvas(activity);
        else if (Const_PRODUCT.isSquareProduct(productCode))
            canvas = new SquareKitCanvas(activity);
        else if (Const_PRODUCT.isPostCardProduct(productCode))
            canvas = new PostCardKitCanvas(activity);
        else if (Const_PRODUCT.isTtabujiProduct(productCode))
            canvas = new TtaebujiKitCanvas(activity);
        else if (Const_PRODUCT.isPolaroidPackProduct(productCode) || Const_PRODUCT.isNewPolaroidPackProduct(productCode))
            canvas = new PolaroidPackageKitCanvas(activity);
        else if (Const_PRODUCT.isCardProduct(productCode))
            canvas = new CardCanvas(activity);
        else if (Const_PRODUCT.isHangingFrameProduct(productCode))
            canvas = new HangingFrameCanvas(activity);
        else if (Const_PRODUCT.isPhotoCardProduct(productCode))
            canvas = new PhotoCardCanvas(activity);
        else if (Const_PRODUCT.isNewWalletProduct(productCode))
            canvas = new NewWalletPhotoCanvas(activity);
        else if (Config.isIdentifyPhotoPrint())
            canvas = new IdentifyPhotoCanvas(activity);
        else if (Const_PRODUCT.isNewYearsCardProduct(productCode))
            canvas = new NewYearsCardCanvas(activity);
        else if (Const_PRODUCT.isDIYStickerProduct(productCode))
            canvas = new DIYStickerPageCanvas(activity);
        else if (Const_PRODUCT.isStikerGroupProduct(productCode))
            canvas = new StickerPageCanvas(activity);
        else if (Const_PRODUCT.isBabyNameStikerGroupProduct(productCode))
            canvas = new BabyNameStickerPageCanvas(activity);
        else if (Const_PRODUCT.isAccordionCardProduct(productCode))
            canvas = new AccordionCardCanvas(activity);
        else if (Const_PRODUCT.isPosterGroupProduct(productCode))
            canvas = new PosterCanvas(activity);
        else if (Const_PRODUCT.isSloganProduct(productCode))
            canvas = new SloganCanvas(activity);
        else if (Const_PRODUCT.isMiniBannerProduct(productCode))
            canvas = new MiniBannerCanvas(activity);
        else if (Const_PRODUCT.isTransparencyPhotoCardProduct(productCode))
            canvas = new TransparencyPhotoCardCanvas(activity);
        else if (Const_PRODUCT.isSmartTalkProduct(productCode))
            canvas = new SmartTalkCanvas(activity);

        else if (Const_PRODUCT.isAcrylicKeyringProduct()) {
            canvas = new AcrylicKeyringCanvas(activity);

        } else if (Const_PRODUCT.isAcrylicStandProduct()) {
            canvas = new AcrylicStandCanvas(activity);

        } else if (Const_PRODUCT.isButtonProduct()) {
            canvas = new ButtonsCanvas(activity);

        } else if (Const_PRODUCT.isMagicalReflectiveSloganProduct()) {
            canvas = new MagicalReflectiveSloganCanvas(activity);

        } else if (Const_PRODUCT.isReflectiveSloganProduct()) {
            canvas = new ReflectiveSloganCanvas(activity);

        } else if (Const_PRODUCT.isHolographySloganProduct()) {
            canvas = new HolographySloganCanvas(activity);

        } else if (Const_PRODUCT.isBudsCaseProduct()) {
            canvas = new BudsCaseCanvas(activity);

        } else if (Const_PRODUCT.isAirpodsCaseProduct()) {
            canvas = new AirpodsCaseCanvas(activity);

        } else if (Const_PRODUCT.isFabricPosterProduct()) {
            canvas = new FabricPosterCanvas(activity);

        } else if (Const_PRODUCT.isTinCaseProduct()) {
            canvas = new TinCaseCanvas(activity);

        } else if (Const_PRODUCT.isSealStickerProduct()) {
            canvas = new SealStickerPageCanvas(activity);
        }

        return canvas;
    }
}
