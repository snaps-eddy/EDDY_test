package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

public interface ISmartRecommendLayoutMakingAnimationImp {
    void startAnimation();
    SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType getAnimationType();
    void stop();
}
