package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;

/**
 * Created by ysjeong on 2016. 11. 24..
 */
public class ImageSelectUIProcessorStrategyForSinglePhotoChoose implements IImageSelectUIProcessorStrategy {
    /**
     * 테마 포토북, 포토북 편집 화면에서 사진 변경 등의 동작을 할때는 1장의 사진만 선택한다.
     */
    @Override
    public void initialize(ImageSelectUIProcessor activity) {
        if (activity == null) return;
        activity.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SINGLE_CHOOSE);
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
