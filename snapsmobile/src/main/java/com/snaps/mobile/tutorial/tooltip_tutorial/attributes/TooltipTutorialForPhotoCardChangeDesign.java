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

public class TooltipTutorialForPhotoCardChangeDesign extends TooltipTutorialBase {

    public TooltipTutorialForPhotoCardChangeDesign(Activity activity, SnapsTutorialAttribute tutorialAttribute) {
        super(activity, tutorialAttribute);
    }

    public static TooltipTutorialCreator createInstanceWithTutorialAttribute(Activity activity, SnapsTutorialAttribute attribute) {
        return new TooltipTutorialForPhotoCardChangeDesign(activity, attribute);
    }

    @Override
    public FTextView createTooltipTextView() throws Exception {
        return createTutorialTooltipView();
    }

    @Override
    public int fixLeftMargin() {
        return UIUtil.convertDPtoPX(getActivity(), isLandscapeMode() ? 10 : 9);
    }

    @Override
    public int fixTopMargin() {
        return UIUtil.convertDPtoPX(getActivity(), isLandscapeMode() ? 18 : -2);
    }

    @Override
    public int getTutorialStringResId() {
        return R.string.guide_change_design_msg;
    }

    @Override
    public SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION getTutorialViewDirection() {
        return SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION.BELOW_OF_VIEW;
    }

    @Override
    public boolean isAutoHideAfterDelay() {
        return true;
    }

    @Override
    public int getTooltipResIdWithDirection(SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION direction) {
        return R.drawable.img_tutorial_speech_bubble_below_of_view_left;
    }
}
