package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayTemplateShapeAdapter;

/**
 * Created by ysjeong on 2016. 11. 24..
 * 사진 선택 화면 - 상단에 트레이(포토북 형태)가 존재하는 형태의 상품군
 */
public class ImageSelectUIProcessorStrategyForTemplateProducts extends ImageSelectUIProcessorStrategyForTemplateBase {
    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = uiProcessor.getActivity();

        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.TEMPLATE);
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
        return new ImageSelectTrayTemplateShapeAdapter(activity);
    }

    @Override
    protected void handleGetTemplateBeforeTask() {}

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {}

    @Override
    protected void onTemplateLoaded() {
        setTemplateThumbnails();
    }
}
