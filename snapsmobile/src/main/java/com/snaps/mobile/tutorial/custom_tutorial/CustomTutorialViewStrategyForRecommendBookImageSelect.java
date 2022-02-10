package com.snaps.mobile.tutorial.custom_tutorial;

import android.app.Dialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

public class CustomTutorialViewStrategyForRecommendBookImageSelect extends CustomTutorialBaseStrategy {

    private static final long ANIMATION_VIEW_TIME_FOR_SHOW_UP = 200;
    private static final long ANIMATION_VIEW_TIME_FOR_WAIT = 1000;
    private static final long ANIMATION_VIEW_TIME_FOR_TRANSLATE = 800;

    private View tutorialView = null;

    public CustomTutorialViewStrategyForRecommendBookImageSelect(Dialog dialog) {
        super(dialog);
    }

    @Override
    public int getContentViewLayoutId() {
        return R.layout.smart_recommend_book_image_select_tutorial;
    }

    @Override
    public void showTutorialView() {
        if (tutorialView == null) return;

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -UIUtil.convertDPtoPX(getDialogContext().getContext(), 40));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.f, 1.f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        animationSet.setFillEnabled(true);
        animationSet.setDuration(ANIMATION_VIEW_TIME_FOR_SHOW_UP);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                startHideAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        tutorialView.clearAnimation();
        tutorialView.startAnimation(animationSet);
    }

    private void startHideAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -UIUtil.convertDPtoPX(getDialogContext().getContext(), 40), -UIUtil.convertDPtoPX(getDialogContext().getContext(), 150));

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.f, 0.f);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        animationSet.setFillEnabled(true);
        animationSet.setDuration(ANIMATION_VIEW_TIME_FOR_TRANSLATE);
        animationSet.setStartOffset(ANIMATION_VIEW_TIME_FOR_WAIT);
        tutorialView.startAnimation(animationSet);
    }

    @Override
    public void initTutorialView() {
        if (getDialogContext() == null) return;
        tutorialView = getDialogContext().findViewById(R.id.smart_recommend_book_image_select_tutorial_view);
    }

    @Override
    public long getTutorialAutoCloseTime() {
        return ANIMATION_VIEW_TIME_FOR_SHOW_UP + ANIMATION_VIEW_TIME_FOR_WAIT + ANIMATION_VIEW_TIME_FOR_TRANSLATE + 200;
    }
}
