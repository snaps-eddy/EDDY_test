package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.pinch_handler.UIPinchMotionDetector;

/**
 * Created by ysjeong on 2017. 1. 17..
 */

public interface IGoogleStylePinchGesture {
    void tryCalculateAnimationViewsOffset(UIPinchMotionDetector.eMOTION_STATE motionType);

    void scrollToPositionWithTargetOffset(ISnapsImageSelectConstants.eMOTION_STATE motionState);
}
