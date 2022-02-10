package com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.GifTutorialView;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;

import static com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil.TERM_OF_TUTORIAL_NO_SHOW_10_DAYS;

/**
 * Created by ysjeong on 2016. 12. 15..
 */

public class ImageSelectUITutorial {
    private GIFTutorialView tutorialView = null;
    private Activity activity = null;

    public ImageSelectUITutorial(Activity activity ) {
        this.activity = activity;
    }

    /**
     *  튜토리얼 뷰을 띄운다.
     */
    public void showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType) {
        if (activity == null || tutorialType == null) return;

        GIFTutorialView.Builder builder = null;
        switch (tutorialType) {
            case PHONE_FRAGMENT_PINCH_MOTION:
                if (SnapsTutorialUtil.isShowConditionSatisfaction(activity, tutorialType, TERM_OF_TUTORIAL_NO_SHOW_10_DAYS)) {
                    builder = new GIFTutorialView.Builder()
                            .setTitle(activity.getString(R.string.img_sel_phone_pic_tutorial_pinch))
                            .setAnimation(SnapsFrameAnimationResFactory.eSnapsFrameAnimation.IMAGE_SELECT_PINCH)
                            .setTutorialType(tutorialType).create();
                }
                break;
        }

        if (builder != null) {
            tutorialView = new GIFTutorialView(activity, builder);
            activity.addContentView(tutorialView, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public void showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType, GIFTutorialView.CloseListener closeListener) {
        if (activity == null || tutorialType == null) return;

        switch (tutorialType) {
            case PHONE_FRAGMENT_PINCH_MOTION:
                if (SnapsTutorialUtil.isShowConditionSatisfaction(activity, tutorialType, TERM_OF_TUTORIAL_NO_SHOW_10_DAYS)) {
                    SnapsTutorialUtil.showGifViewAlways(activity, new SnapsTutorialAttribute.Builder().setGifType(SnapsTutorialAttribute.GIF_TYPE.PINCH_ZOOM_AND_DRAG).create(), new GifTutorialView.CloseListener() {
                        @Override
                        public void close() {
                            ImageSelectUtils.setShownDatePhoneFragmentTutorial(activity, ISnapsImageSelectConstants.eTUTORIAL_TYPE.PHONE_FRAGMENT_PINCH_MOTION);
                        }
                    });
                } else {
                    closeListener.close();
                }
                break;
        }
    }

    public boolean removeTutorial() {
		if (tutorialView != null) {
			ViewGroup g = (ViewGroup) tutorialView.getParent();
			if (g != null) {
				g.removeView(tutorialView);
				tutorialView = null;
				return true;
			}
		}
		tutorialView = null;
        return false;
    }

}
