package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayEmptyShapeAdapter;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyForEmpty implements IImageSelectUIProcessorStrategy {
    private ImageSelectUIProcessor uiProcessor = null;

    /**
     * 사진 인화, 스티커킷 같은 형태에서는 상단에 트레이는 있지만, 정해진 템플릿이 없기 때문에 트레이가 비어 있다.
     */
    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.EMPTY);
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
        return new ImageSelectTrayEmptyShapeAdapter(uiProcessor.getActivity());
    }
}
