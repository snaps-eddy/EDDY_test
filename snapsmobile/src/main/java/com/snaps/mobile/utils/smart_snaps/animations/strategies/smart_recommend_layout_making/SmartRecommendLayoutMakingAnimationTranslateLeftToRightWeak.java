package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

import android.animation.ValueAnimator;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;

public class SmartRecommendLayoutMakingAnimationTranslateLeftToRightWeak extends SmartRecommendLayoutMakingAnimationBase {
    private static final String TAG = SmartRecommendLayoutMakingAnimationTranslateLeftToRightWeak.class.getSimpleName();

    public SmartRecommendLayoutMakingAnimationTranslateLeftToRightWeak(SmartRecommendLayoutMakingAnimationAttribute animationAttribute) {
        super(animationAttribute);
    }

    @Override
    public SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType getAnimationType() {
        return SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT_WEAK;
    }

    @Override
    public void startAnimation() {
        try {
            final ImageView visibleImageView = getAnimationAttribute().getImageView();

            int imageViewWidth = visibleImageView.getMeasuredWidth() > 0 ? visibleImageView.getMeasuredWidth() : UIUtil.getScreenWidth(getAnimationAttribute().getActivity());

            float scaleValue = 1.03f;

            final int centerX = imageViewWidth/2;
            final int centerY = imageViewWidth/2;
            visibleImageView.setPivotX(centerX);
            visibleImageView.setPivotY(centerY);
            visibleImageView.setScaleX(scaleValue);
            visibleImageView.setScaleY(scaleValue);
            visibleImageView.setTranslationX(0);
            visibleImageView.setTranslationY(0);
            visibleImageView.setAlpha(1.f);

            float drawableWidth = imageViewWidth * scaleValue;
            final int diff = (int) ((drawableWidth - imageViewWidth) / 2);
            if (diff <= 0) return;

            visibleImageView.setTranslationY(diff);

            ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0f, 1f);
            viewParamsAnimator.setDuration(getAnimationAttribute().getAnimationTime());
            viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        if (animation == null) return;

                        float animatedValue = (float) animation.getAnimatedValue();
                        if (visibleImageView != null) {
                            float translateX = diff * animatedValue;//;1.f + (float) (0.03 * animatedValue);
                            visibleImageView.setTranslationX(translateX);
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });

            viewParamsAnimator.start();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
