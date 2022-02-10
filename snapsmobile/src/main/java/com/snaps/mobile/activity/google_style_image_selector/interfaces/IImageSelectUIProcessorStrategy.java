package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public interface IImageSelectUIProcessorStrategy {

    boolean isExistOnlyTrayAllViewLayout();

    boolean isExistTrayView();

    ImageSelectTrayBaseAdapter createTrayAdapter();

    void initialize(ImageSelectUIProcessor activity);

    void postInitialized();
}
