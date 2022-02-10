package com.snaps.mobile.tutorial.tooltip_tutorial.attributes;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;

import font.FTextView;

/**
 * Created by ysjeong on 2017. 8. 1..
 */

public class TooltipTutorialBase implements TooltipTutorialCreator {

    protected final long DEFAULT_TOOLTIP_AUTO_HIDE_TIME = 4000l;

    private SnapsTutorialAttribute tutorialAttribute;
    private Activity activity;

    public TooltipTutorialBase(Activity activity,  SnapsTutorialAttribute tutorialAttribute) {
        this.tutorialAttribute = tutorialAttribute;
        this.activity = activity;
    }

    public SnapsTutorialAttribute getTutorialAttribute() {
        return tutorialAttribute;
    }

    public Activity getActivity() {
        return activity;
    }

    protected Rect getTooltipTutorialViewLocations(View targetView, SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION direction) throws Exception {
        Rect locationRect = getTooltipTutorialTargetViewLocation(targetView);
        if (locationRect == null) return null;

        switch (direction) {
            case ABOVE_OF_VIEW:
                locationRect.offset(0, -UIUtil.convertDPtoPX(ContextUtil.getContext(), SnapsTutorialConstants.DEFAULT_TOOLTIP_HEIGHT));
                break;
            case BELOW_OF_VIEW:
                int heightOfTargetView = targetView.getMeasuredHeight();
                locationRect.offset(0, heightOfTargetView);
                break;
            //TODO  left, right
        }

        return locationRect;
    }

    protected Drawable getTooltipDrawableWithDirection(@NonNull Activity activity, SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION direction) throws Exception {
        return ImageUtil.getNinePatchDrawableFromResourceId(getTooltipResIdWithDirection(direction), activity);
    }

    protected Rect getTooltipTutorialTargetViewLocation(View targetView) throws Exception {
        Rect targetOffsetRect = new Rect();
        targetView.getGlobalVisibleRect(targetOffsetRect);
        targetOffsetRect.offset(0, -UIUtil.getStatusBarHeight());
        return targetOffsetRect;
    }

    protected FTextView createTutorialTooltipView() throws Exception {
        SnapsTutorialAttribute attribute = getTutorialAttribute();
        View targetView = attribute.getTargetView();
        Rect locationRect = getTooltipTutorialViewLocations(targetView, getTutorialViewDirection());
        FTextView tutorialTooltipView = createDefaultTooltipTextViewWithLocation(locationRect);
        setTutorialTextForTooltipView(tutorialTooltipView);
        setBalloonBackgroundForTooltipView(tutorialTooltipView);
        return tutorialTooltipView;
    }

    private FTextView createDefaultTooltipTextViewWithLocation(Rect locationRect) throws Exception {
        FTextView tutorialTooltipView = new FTextView(getActivity());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = locationRect.left + fixLeftMargin();
        layoutParams.topMargin = locationRect.top + fixTopMargin();
        tutorialTooltipView.setPadding(UIUtil.convertDPtoPX(getActivity(), 16), UIUtil.convertDPtoPX(getActivity(), 13), UIUtil.convertDPtoPX(getActivity(), 16), UIUtil.convertDPtoPX(getActivity(), 14));
        tutorialTooltipView.setLayoutParams(layoutParams);
        return tutorialTooltipView;
    }

    private void setTutorialTextForTooltipView(FTextView tutorialTooltipView) throws Exception {
        tutorialTooltipView.setTextColor(Color.WHITE);
        tutorialTooltipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        if (getTutorialStringResId() > 0)
            tutorialTooltipView.setText(getTutorialStringResId());
    }

    private void setBalloonBackgroundForTooltipView(FTextView tutorialTooltipView) throws Exception {
        Drawable tooltipDrawable = getTooltipDrawableWithDirection(getActivity(), getTutorialViewDirection());
        if (tooltipDrawable != null)
            tutorialTooltipView.setBackgroundDrawable(tooltipDrawable);
    }

    @Override
    public FTextView createTooltipTextView() throws Exception {
        return null;
    }

    @Override
    public int fixLeftMargin() {
        return 0;
    }

    @Override
    public int fixTopMargin() {
        return 0;
    }

    @Override
    public int getTutorialStringResId() {
        return 0;
    }

    @Override
    public SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION getTutorialViewDirection() {
        return null;
    }

    @Override
    public boolean isAutoHideAfterDelay() {
        return false;
    }

    @Override
    public long getAutoHideDelay() {
        return DEFAULT_TOOLTIP_AUTO_HIDE_TIME;
    }

    @Override
    public int getTooltipResIdWithDirection(SnapsTutorialConstants.eTUTORIAL_VIEW_DIRECTION direction) {
        return 0;
    }

    protected boolean isLandscapeMode() {
        return getTutorialAttribute().isLandscapeMode();
    }
}
