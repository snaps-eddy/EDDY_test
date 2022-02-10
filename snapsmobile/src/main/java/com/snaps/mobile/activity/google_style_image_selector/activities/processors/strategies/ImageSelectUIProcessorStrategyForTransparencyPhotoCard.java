package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayTransparencyPhotoCardAdapter;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.TRANSPARENCY_PHOTO_CARD_MAX;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyForTransparencyPhotoCard extends ImageSelectUIProcessorStrategyForTemplateBase implements IImageSelectUIProcessorStrategy {
    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = uiProcessor.getActivity();
        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.EMPTY);

        uiProcessor.setMaxImageCount(TRANSPARENCY_PHOTO_CARD_MAX);
        uiProcessor.setCurrentMaxImageCount(TRANSPARENCY_PHOTO_CARD_MAX);
    }

    @Override
    public void postInitialized() {
    }

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
        return new ImageSelectTrayTransparencyPhotoCardAdapter(uiProcessor.getActivity());
    }

    @Override
    protected void handleGetTemplateBeforeTask() {}

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {}

    @Override
    protected void onTemplateLoaded() {}
}
