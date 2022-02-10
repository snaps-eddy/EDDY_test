package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

import android.animation.ValueAnimator;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;

public class SmartRecommendLayoutMakingAnimationTranslateLeftToRightWithZoomIn extends SmartRecommendLayoutMakingAnimationBase {
    private static final String TAG = SmartRecommendLayoutMakingAnimationTranslateLeftToRightWithZoomIn.class.getSimpleName();

    public SmartRecommendLayoutMakingAnimationTranslateLeftToRightWithZoomIn(SmartRecommendLayoutMakingAnimationAttribute animationAttribute) {
        super(animationAttribute);
    }

    @Override
    public SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType getAnimationType() {
        return SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT;
    }

    @Override
    public void startAnimation() {
        if (isStopped()) return;

        try {
            final ImageView visibleImageView = getAnimationAttribute().getImageView();

            int imageViewWidth = visibleImageView.getMeasuredWidth() > 0 ? visibleImageView.getMeasuredWidth() : UIUtil.getScreenWidth(getAnimationAttribute().getActivity());

            final float defaultScaleValue = 1.03f;

            final int centerX = imageViewWidth/2;
            final int centerY = imageViewWidth/2;
            visibleImageView.setPivotX(centerX);
            visibleImageView.setPivotY(centerY);
            visibleImageView.setScaleX(defaultScaleValue);
            visibleImageView.setScaleY(defaultScaleValue);
            visibleImageView.setTranslationX(0);
            visibleImageView.setTranslationY(0);

            visibleImageView.setAlpha(1.f);

            float drawableWidth = imageViewWidth * defaultScaleValue;
            final int diff = (int) ((drawableWidth - imageViewWidth) / 2);
            if (diff <= 0) return;

            visibleImageView.setTranslationY(diff);

            ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0f, 1f);
            viewParamsAnimator.setDuration(getAnimationAttribute().getAnimationTime());
            viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        if (animation == null || isStopped()) return;

                        float animatedValue = (float) animation.getAnimatedValue();
                        if (visibleImageView != null) {
                            float translateX = diff * animatedValue;//;1.f + (float) (0.03 * animatedValue);
                            visibleImageView.setTranslationX(translateX);

                            float reverseValue = 1f - animatedValue;
                            float scale = defaultScaleValue + (float) (0.025 * reverseValue);
                            visibleImageView.setScaleX(scale);
                            visibleImageView.setScaleY(scale);
                        }
                    } catch (Exception e) { Dlog.e(TAG, e); }
                }
            });

            viewParamsAnimator.start();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
