package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

import android.animation.ValueAnimator;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;

public class SmartRecommendLayoutMakingAnimationZoomInWeak extends SmartRecommendLayoutMakingAnimationBase {
    private static final String TAG = SmartRecommendLayoutMakingAnimationZoomInWeak.class.getSimpleName();

    public SmartRecommendLayoutMakingAnimationZoomInWeak(SmartRecommendLayoutMakingAnimationAttribute animationAttribute) {
        super(animationAttribute);
    }

    @Override
    public SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType getAnimationType() {
        return SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType.ZOOM_IN_WEAK;
    }

    @Override
    public void startAnimation() {
        try {
            final ImageView visibleImageView = getAnimationAttribute().getImageView();

            int imageWidth = visibleImageView.getMeasuredWidth() > 0 ? visibleImageView.getMeasuredWidth() : UIUtil.getScreenWidth(getAnimationAttribute().getActivity());
            int imageHeight = visibleImageView.getMeasuredHeight() > 0 ? visibleImageView.getMeasuredHeight() : imageWidth;

            final int centerX = imageWidth/2;
            final int centerY = imageHeight/2;
            visibleImageView.setPivotX(centerX);
            visibleImageView.setPivotY(centerY);
            visibleImageView.setTranslationX(0);
            visibleImageView.setTranslationY(0);
            visibleImageView.setScaleX(1.f);
            visibleImageView.setScaleY(1.f);
            visibleImageView.setAlpha(1.f);

            ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(1f, 0f);
            viewParamsAnimator.setDuration(getAnimationAttribute().getAnimationTime());
            viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        if (animation == null) return;

                        float animatedValue = (float) animation.getAnimatedValue();

                        if (visibleImageView != null) {
                            float scale = 1.f + (float) (0.03 * animatedValue);
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
