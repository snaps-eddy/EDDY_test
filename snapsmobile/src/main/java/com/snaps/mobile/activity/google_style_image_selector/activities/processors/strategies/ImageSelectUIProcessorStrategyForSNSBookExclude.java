package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyForSNSBookExclude implements IImageSelectUIProcessorStrategy {
    /**
     * SNS북에서 진입했을 경우는 제외할 사진을 선택하는 형태이다.
     */
    @Override
    public void initialize(ImageSelectUIProcessor activity) {
        if (activity == null) return;
        activity.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SNS);
    }

    @Override
    public void postInitialized() {}

    @Override
    public boolean isExistTrayView() {
        return false;
    }

    @Override
    public boolean isExistOnlyTrayAllViewLayout() {
        return false;
    }

    @Override
    public ImageSelectTrayBaseAdapter createTrayAdapter() {
        return null;
    }
}
