package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies;

import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;

/**
 * Created by ysjeong on 2017. 1. 4..
 */

public class GooglePhotoStyleAdapterStrategyFactory {
    public static GooglePhotoStyleAdapterStrategyBase createAdapterStrategyByDepth(ImageSelectActivityV2 activityV2,
                                                                                   GooglePhotoStyleAdapterStrategyBase.AdapterAttribute attribute,
                                                                                   IImageSelectFragmentItemClickListener fragmentItemClickListener) {
        if (attribute == null || attribute.getUiDepth() == null) return null;

        switch (attribute.getUiDepth()) {
            case DEPTH_YEAR: return new GooglePhotoStyleAdapterStrategyDepthYear(activityV2, attribute, fragmentItemClickListener);
            case DEPTH_MONTH: return new GooglePhotoStyleAdapterStrategyDepthMonth(activityV2, attribute, fragmentItemClickListener);
            case DEPTH_DAY: return new GooglePhotoStyleAdapterStrategyDepthDay(activityV2, attribute, fragmentItemClickListener);
            case DEPTH_STAGGERED: return new GooglePhotoStyleAdapterStrategyDepthStaggered(activityV2, attribute, fragmentItemClickListener);
        }

        return null;
    }
}
