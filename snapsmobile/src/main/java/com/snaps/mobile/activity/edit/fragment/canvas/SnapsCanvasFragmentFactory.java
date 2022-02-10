package com.snaps.mobile.activity.edit.fragment.canvas;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.edit.fragment.canvas.reflectable_slogan.HolographySloganCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.reflectable_slogan.MagicalReflectiveSloganFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.reflectable_slogan.ReflectiveSloganFragment;

public class SnapsCanvasFragmentFactory {

    public SnapsCanvasFragment createCanvasFragment(String productCode) {

        SnapsCanvasFragment snapscanvasfragment = null;

        if (Config.isSimplePhotoBook(productCode) || Const_PRODUCT.isLayFlatBook(productCode))
            if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
                snapscanvasfragment = new SmartRecommendPhotoBookCanvasFragment();
            } else {
                snapscanvasfragment = new SimplePhotoBookCanvasFragment();
            }
        else if (Config.isSimpleMakingBook(productCode))
            snapscanvasfragment = new SimpleMakingBookCanvasFragment();
        else if (Config.isSnapsSticker(productCode))
            snapscanvasfragment = new StickerCanvasFragment();
        else if (Config.isThemeBook(productCode))
            snapscanvasfragment = new ThemeBookCanvasFragment();
        else if (Config.isWoodBlockCalendar(productCode))
            snapscanvasfragment = new WoodBlockCalendarCanvasFragment();
        else if (Config.isCalendar(productCode))
            snapscanvasfragment = new CalendarCanvasFragment();
        else if (Const_PRODUCT.isPhotoMugCupProduct(productCode))
            snapscanvasfragment = new PhotoMugCupCanvasFragment();
        else if (Const_PRODUCT.isTumblerProduct(productCode))
            snapscanvasfragment = new TumblerCanvasFragment();
        else if (Const_PRODUCT.isMarvelFrame(productCode) || Const_PRODUCT.isBoardFrameProduct(productCode) || Const_PRODUCT.isPremiumAcrylFrameProduct(productCode))
            snapscanvasfragment = new MarvelFrameCanvasFragment();
        else if (Const_PRODUCT.isMetalFrame(productCode))
            snapscanvasfragment = new MetalFrameCanvasFragment();
        else if (Const_PRODUCT.isWoodFrame(productCode))
            snapscanvasfragment = new WoodFrameCanvasFragment();
        else if (Const_PRODUCT.isPolaroidProduct(productCode))
            snapscanvasfragment = new PolaroidCanvasFragment();
        else if (Const_PRODUCT.isWalletProduct(productCode))
            snapscanvasfragment = new WalletCanvasFragment();
        else if (Const_PRODUCT.isLegacyPhoneCaseProduct(productCode))
            snapscanvasfragment = new PhoneCaseCanvasFragment();
        else if (Const_PRODUCT.isUvPhoneCaseProduct(productCode) || Const_PRODUCT.isPrintPhoneCaseProduct(productCode))
            snapscanvasfragment = new NewPhoneCaseCanvasFragment();
        else if (Const_PRODUCT.isDesignNoteProduct(productCode))
            snapscanvasfragment = new DesignNoteCanvasFragment();
        else if (Const_PRODUCT.isMousePadProduct(productCode))
            snapscanvasfragment = new MousePadCanvasFragment();
        else if (Const_PRODUCT.isSNSBook(productCode))
            snapscanvasfragment = new SNSBookCanvasFragment();
        else if (Const_PRODUCT.isInteiorFrame(productCode))
            snapscanvasfragment = new InteiorFrameCanvasFragment();
        else if (Const_PRODUCT.isWoodBlockProduct(productCode))
            snapscanvasfragment = new WoodBlockCanvasFragment();
        else if (Const_PRODUCT.isSquareProduct(productCode))
            snapscanvasfragment = new SquareKitCanvasFragment();
        else if (Const_PRODUCT.isPostCardProduct(productCode))
            snapscanvasfragment = new PostCardKitCanvasFragment();
        else if (Const_PRODUCT.isTtabujiProduct(productCode))
            snapscanvasfragment = new TtaebujiKitCanvasFragment();
        else if (Const_PRODUCT.isPolaroidPackProduct(productCode) || Const_PRODUCT.isNewPolaroidPackProduct(productCode))
            snapscanvasfragment = new PolaroidPackageKitCanvasFragment();
        else if (Const_PRODUCT.isCardProduct(productCode))
            snapscanvasfragment = new CardCanvasFragment();
        else if (Const_PRODUCT.isHangingFrameProduct(productCode))
            snapscanvasfragment = new HangingFrameCanvasFragment();
        else if (Const_PRODUCT.isPhotoCardProduct(productCode) || Const_PRODUCT.isTransparencyPhotoCardProduct())
            snapscanvasfragment = new PhotoCardCanvasFragment();
        else if (Const_PRODUCT.isNewWalletProduct(productCode))
            snapscanvasfragment = new NewWalletPhotoCanvasFragment();
        else if (Config.isIdentifyPhotoPrint(productCode))
            snapscanvasfragment = new IdentifyPhotoCanvasFragment();
        else if (Const_PRODUCT.isNewYearsCardProduct(productCode))
            snapscanvasfragment = new NewYearsCardCanvasfragment();
        else if (Const_PRODUCT.isStikerGroupProduct(productCode))
            snapscanvasfragment = new StickerCanvasFragment();
        else if (Const_PRODUCT.isBabyNameStikerGroupProduct(productCode))
            snapscanvasfragment = new StickerCanvasFragment();
        else if (Const_PRODUCT.isAccordionCardProduct(productCode))
            snapscanvasfragment = new AccordionCardCanvasFragment();
        else if (Const_PRODUCT.isPosterGroupProduct(productCode))
            snapscanvasfragment = new PosterCanvasFragment();

        else if (Const_PRODUCT.isSloganProduct(productCode)) {
            snapscanvasfragment = new SloganCanvasFragment();

        } else if (Const_PRODUCT.isReflectiveSloganProduct()) {
            snapscanvasfragment = new ReflectiveSloganFragment();

        } else if (Const_PRODUCT.isHolographySloganProduct()) {
            snapscanvasfragment = new HolographySloganCanvasFragment();

        } else if (Const_PRODUCT.isMagicalReflectiveSloganProduct()) {
            snapscanvasfragment = new MagicalReflectiveSloganFragment();

        } else if (Const_PRODUCT.isSealStickerProduct()){
            snapscanvasfragment = new SealStickerCanvasFragment();

        } else {
            snapscanvasfragment = new ThemeBookCanvasFragment();
        }


        return snapscanvasfragment;
    }
}
