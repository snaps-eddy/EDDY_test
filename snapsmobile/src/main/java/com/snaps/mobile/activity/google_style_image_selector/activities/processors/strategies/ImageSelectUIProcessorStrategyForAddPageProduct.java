package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.common.products.card_shape_product.SnapsCardShapeProductEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.PosterEditor;
import com.snaps.mobile.activity.common.products.multi_page_product.StickerEditor;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayDIYStickerAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayEmptyShapeAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTraySmartRecommendBookAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayTemplateShapeAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayTransparencyPhotoCardAdapter;

import java.util.ArrayList;

import static com.snaps.common.utils.constant.Const_PRODUCT.BIG_RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.EXAM_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.LONG_PHOTO_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A2_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A2_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A3_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A3_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A4_HORIZONTAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.POSTER_A4_VERTICAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_FOLDER_ORIGINAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_FOLDER_WIDE;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_NORMAL_ORIGINAL;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_CARD_5_7_NORMAL_WIDE;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PHOTO_CARD;
import static com.snaps.common.utils.constant.Const_PRODUCT.RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.ROUND_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.SQUARE_STICKER;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.TRANSPARENCY_PHOTO_CARD_MAX;

/**
 * Created by ysjeong on 2016. 11. 24..
 * 사진 선택 화면 - 상단에 트레이(포토북 형태)가 존재하는 형태의 상품군
 */
public class ImageSelectUIProcessorStrategyForAddPageProduct extends ImageSelectUIProcessorStrategyForTemplateBase {
    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = uiProcessor.getActivity();

        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.EMPTY);
        pageProgress = new DialogDefaultProgress(uiProcessor.getActivity());

        loadTemplate();
    }

    @Override
    public void postInitialized() {}

    @Override
    public boolean isExistTrayView() {
        return true;
    }

    @Override
    public boolean isExistOnlyTrayAllViewLayout() {
        return false;
    }

    @Override
    public ImageSelectTrayBaseAdapter createTrayAdapter() {
        if (uiProcessor == null) return null;
        return new ImageSelectTrayDIYStickerAdapter(uiProcessor.getActivity());
    }

    @Override
    protected void handleGetTemplateBeforeTask() {}

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {}

    @Override
    protected void onTemplateLoaded() {
        SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
        if (uiProcessor == null || template == null || template.getPages() == null || template.getPages().isEmpty()) return;
        int maxImageCount = getPageMaxPhotoCount(template) * getMaxPage();

        uiProcessor.setMaxImageCount(maxImageCount);
        uiProcessor.setCurrentMaxImageCount(maxImageCount);
        uiProcessor.getTrayAdapter().setImageCount(maxImageCount);
    }

    private int getPageMaxPhotoCount(SnapsTemplate template) {
        ArrayList<SnapsControl> layerLayoutList =  null;
        if(Const_PRODUCT.isPhotoCardProduct()) {
            layerLayoutList =  template.getPages().get(2).getLayerLayouts();
        } else if(Const_PRODUCT.isCardShapeFolder()){
            layerLayoutList =  template.getPages().get(1).getLayerLayouts();
        }    else{
            layerLayoutList =  template.getPages().get(0).getLayerLayouts();
        }


        int count = 0;
        for(SnapsControl snapsControl : layerLayoutList) {
            if(snapsControl._controlType ==  SnapsControl.CONTROLTYPE_IMAGE) {
                count++;
            }
        }
        return count;
    }

    private int getMaxPage() {
        if(Const_PRODUCT.isDIYStickerProduct()) {
            return 1;
        } else {
            switch (Config.getPROD_CODE()) {
                case PRODUCT_CARD_5_7_NORMAL_ORIGINAL:
                case PRODUCT_CARD_5_7_NORMAL_WIDE:
                case PRODUCT_CARD_5_7_FOLDER_ORIGINAL:
                case PRODUCT_CARD_5_7_FOLDER_WIDE:
                    return SnapsCardShapeProductEditor.MAX_CARD_QUANTITY;
                case PRODUCT_PHOTO_CARD:
                    return SnapsCardShapeProductEditor.MAX_PHOTO_CARD_QUANTITY * 2;
                case ROUND_STICKER:
                    return StickerEditor.MAX_ROUND_QUANTITY;
                case SQUARE_STICKER:
                    return StickerEditor.MAX_SQUARE_QUANTITY;
                case RECTANGLE_STICKER:
                    return StickerEditor.MAX_RECTANGLE_QUANTITY;
                case BIG_RECTANGLE_STICKER:
                    return StickerEditor.MAX_BIG_RECTANGLE_QUANTITY;
                case EXAM_STICKER:
                    return StickerEditor.MAX_EXAM_QUANTITY;
                case LONG_PHOTO_STICKER :
                    return StickerEditor.MAX_LONG_PHOTO_QUANTITY;
                case POSTER_A4_VERTICAL:
                case POSTER_A4_HORIZONTAL:
                    return PosterEditor.MAX_A4_QUANTITY;

                case POSTER_A3_VERTICAL:
                case POSTER_A3_HORIZONTAL:
                    return PosterEditor.MAX_A3_QUANTITY;
                case POSTER_A2_VERTICAL:
                case POSTER_A2_HORIZONTAL:
                    return PosterEditor.MAX_A2_QUANTITY;

                default:
                    return 1;
            }

        }

    }
}
