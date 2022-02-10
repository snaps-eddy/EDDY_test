package com.snaps.mobile.tutorial.tooltip_tutorial.attributes;

import com.snaps.mobile.tutorial.SnapsTutorialConstants;

import font.FTextView;

/**
 * Created by ysjeong on 2017. 8. 1..
 */

public interface TooltipTutorialCreator {
    FTextView createTooltipTextView() throws Exception;

    int fixLeftMargin();
    int fixTopMargin();
    int getTutorialStringResId();
    SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION getTutorialViewDirection();
    boolean isAutoHideAfterDelay();
    long getAutoHideDelay();
    int getTooltipResIdWithDirection(SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION direction);
}
