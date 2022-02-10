package com.snaps.mobile.activity.common.products;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.common.products.book_product.KTBookEditor;
import com.snaps.mobile.activity.common.products.book_product.SimpleMakingBookEditor;
import com.snaps.mobile.activity.common.products.book_product.SimplePhotoBookEditor;
import com.snaps.mobile.activity.common.products.card_shape_product.CardEditor;
import com.snaps.mobile.activity.common.products.card_shape_product.NewWalletPhotoEditor;
import com.snaps.mobile.activity.common.products.card_shape_product.NewYearsCardEditor;
import com.snaps.mobile.activity.common.products.card_shape_product.PhotoCardEditor;
import com.snaps.mobile.activity.common.products.card_shape_product.TransparencyPhotoCardEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.AccordionCardEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.BabyNameStickerEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.CalendarEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.PackageKitEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.PosterEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.SloganEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.WoodBlockCalendarEditor;
import com.snaps.mobile.activity.common.products.reflectable_slogans.HolographySloganEditor;
import com.snaps.mobile.activity.common.products.reflectable_slogans.MagicalReflectiveSloganEditor;
import com.snaps.mobile.activity.common.products.reflectable_slogans.ReflectiveSloganEditor;
import com.snaps.mobile.activity.common.products.single_page_product.AcrylicKeyringEditor;
import com.snaps.mobile.activity.common.products.single_page_product.AcrylicStandEditor;
import com.snaps.mobile.activity.common.products.single_page_product.AirpodsCaseEditor;
import com.snaps.mobile.activity.common.products.single_page_product.BudsCaseEditor;
import com.snaps.mobile.activity.common.products.single_page_product.ButtonsEditor;
import com.snaps.mobile.activity.common.products.single_page_product.DIYStickerProductEditor;
import com.snaps.mobile.activity.common.products.single_page_product.FabricPosterEditor;
import com.snaps.mobile.activity.common.products.single_page_product.FrameProductEditor;
import com.snaps.mobile.activity.common.products.single_page_product.IdentifyPhotoEditor;
import com.snaps.mobile.activity.common.products.single_page_product.MiniBannerProductEditor;
import com.snaps.mobile.activity.common.products.single_page_product.NewPhoneCaseEditor;
import com.snaps.mobile.activity.common.products.single_page_product.PhoneCaseEditor;
import com.snaps.mobile.activity.common.products.single_page_product.SealStickerProductEditor;
import com.snaps.mobile.activity.common.products.single_page_product.SmartTalkProductEditor;
import com.snaps.mobile.activity.common.products.single_page_product.TinCaseEditor;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class SnapsProductEditorFactory {

    public static SnapsProductEditorAPI createProductEditor(FragmentActivity fragmentActivity) {

        SnapsProductEditConstants.eSnapsProductKind productName = SnapsProductEditorFactory.getProductKind(fragmentActivity);
        switch (productName) {
            case SIMPLE_PHOTO_BOOK:
                return new SimplePhotoBookEditor(fragmentActivity);
            case SIMPLE_MAKING_BOOK:
                return new SimpleMakingBookEditor(fragmentActivity);
            case FRAME:
                return new FrameProductEditor(fragmentActivity);
            case PHONE_CASE:
                return new PhoneCaseEditor(fragmentActivity);
            case IDENTIFY_PHOTO:
                return new IdentifyPhotoEditor(fragmentActivity);
            case CALENDAR:
                return new CalendarEditor(fragmentActivity);
            case WOODBLOCK_CALENDAR:
                return new WoodBlockCalendarEditor(fragmentActivity);
            case PACKAGE_KIT:
                return new PackageKitEditor(fragmentActivity);
            case CARD:
                return new CardEditor(fragmentActivity);
            case PHOTO_CARD:
                return new PhotoCardEditor(fragmentActivity);
            case WALLET_PHOTO:
                return new NewWalletPhotoEditor(fragmentActivity);
            case NEW_YEARS_CARD:
                return new NewYearsCardEditor(fragmentActivity);
            case STICKER:
                return new StickerEditor(fragmentActivity);
            case BABY_NAME_STICKER:
                return new BabyNameStickerEditor(fragmentActivity);
            case DIY_STICKER:
                return new DIYStickerProductEditor(fragmentActivity);
            case ACCORDION_CARD:
                return new AccordionCardEditor(fragmentActivity);
            case POSTER:
                return new PosterEditor(fragmentActivity);
            case SLOGAN:
                return new SloganEditor(fragmentActivity);
            case MINI_BANNER:
                return new MiniBannerProductEditor(fragmentActivity);
            case TRANSPARENCY_PHOTO_CARD:
                return new TransparencyPhotoCardEditor(fragmentActivity);
            case SMART_TALK:
                return new SmartTalkProductEditor(fragmentActivity);
            case KT_Book:
                return new KTBookEditor(fragmentActivity);
            case ACRYLIC_KEYRING:
                return new AcrylicKeyringEditor(fragmentActivity);
            case ACRYLIC_STAND:
                return new AcrylicStandEditor(fragmentActivity);
            case BUTTONS:
                return new ButtonsEditor(fragmentActivity);
            case REFLECTIVE_SLOGAN:
                return new ReflectiveSloganEditor(fragmentActivity);
            case MAGICAL_REFLECTIVE_SLOGAN:
                return new MagicalReflectiveSloganEditor(fragmentActivity);
            case HOLOGRAPHY_SLOGAN:
                return new HolographySloganEditor(fragmentActivity);
            case TIN_CASE:
                return new TinCaseEditor(fragmentActivity);
            case AIRPODS_CASE:
                return new AirpodsCaseEditor(fragmentActivity);
            case BUDS_CASE:
                return new BudsCaseEditor(fragmentActivity);
            case FABRIC_POSTER:
                return new FabricPosterEditor(fragmentActivity);
            case NEW_PHONE_CASE:
                return new NewPhoneCaseEditor(fragmentActivity);
            case SEAL_STICKER:
                return new SealStickerProductEditor(fragmentActivity);
        }
        return null;
    }

    private static SnapsProductEditConstants.eSnapsProductKind getProductKind(Activity activity) {
        int ordinal = activity.getIntent().getIntExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, -1);
        return SnapsProductEditConstants.eSnapsProductKind.values()[ordinal];
    }
}
