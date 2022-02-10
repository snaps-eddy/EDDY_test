package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayDIYStickerAdapter;

import java.util.ArrayList;

public class ImageSelectUIProcessorStrategyForSealSticker extends ImageSelectUIProcessorStrategyForTemplateBase {

    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = this.uiProcessor.getActivity();

        this.uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.EMPTY);
        pageProgress = new DialogDefaultProgress(uiProcessor.getActivity());

        loadTemplate();
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
        if (activity == null) return null;
        return new ImageSelectTrayDIYStickerAdapter(activity);
    }

    @Override
    protected void handleGetTemplateBeforeTask() {
    }

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {
    }

    @Override
    protected void onTemplateLoaded() {
        SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
        if (uiProcessor == null || template == null || template.getPages() == null || template.getPages().isEmpty()) return;
        int maxImageCount = getPageMaxPhotoCount(template);

        uiProcessor.setMaxImageCount(maxImageCount);
        uiProcessor.setCurrentMaxImageCount(maxImageCount);
        uiProcessor.getTrayAdapter().setImageCount(maxImageCount);
    }

    private int getPageMaxPhotoCount(SnapsTemplate template) {
        ArrayList<SnapsControl> layerLayoutList = template.getPages().get(0).getLayerLayouts();
        int count = 0;
        for (SnapsControl snapsControl : layerLayoutList) {
            if (snapsControl instanceof SnapsLayoutControl) {
                if (((SnapsLayoutControl)snapsControl).isForBackground()) {
                    continue;
                }
            }

            if (snapsControl._controlType == SnapsControl.CONTROLTYPE_IMAGE) {
                count++;
            }
        }
        return count;
    }
}
