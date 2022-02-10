package com.snaps.mobile.tutorial.tooltip_tutorial.attributes;

import android.app.Activity;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;

import font.FTextView;

/**
 * Created by ysjeong on 2017. 8. 1..
 */

public class TooltipTutorialForNewYearsCardThumbnailChangeQuantity extends TooltipTutorialBase {

    public TooltipTutorialForNewYearsCardThumbnailChangeQuantity(Activity activity, SnapsTutorialAttribute tutorialAttribute) {
        super(activity, tutorialAttribute);
    }

    public static TooltipTutorialCreator createInstanceWithTutorialAttribute(Activity activity, SnapsTutorialAttribute attribute) {
        return new TooltipTutorialForNewYearsCardThumbnailChangeQuantity(activity, attribute);
    }

    @Override
    public FTextView createTooltipTextView() throws Exception {
        return createTutorialTooltipView();
    }

    @Override
    public int fixLeftMargin() {
        return UIUtil.convertDPtoPX(getActivity(), isLandscapeMode() ? 60 : 60);
    }

    @Override
    public int fixTopMargin() {
        return UIUtil.convertDPtoPX(getActivity(), isLandscapeMode() ? 118 : 88);
    }

    @Override
    public int getTutorialStringResId() {
        return R.string.guide_change_count_msg;
    }

    @Override
    public SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION getTutorialViewDirection() {
        return SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION.ABOVE_OF_VIEW;
    }

    @Override
    public boolean isAutoHideAfterDelay() {
        return true;
    }

    @Override
    public int getTooltipResIdWithDirection(SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION direction) {
        return R.drawable.img_tutorial_speech_bubble_for_above_of_view_left;
    }
}
