package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

public abstract class SmartRecommendLayoutMakingAnimationBase implements ISmartRecommendLayoutMakingAnimationImp {
    private SmartRecommendLayoutMakingAnimationAttribute animationAttribute;
    private boolean isStopped = false;

    private SmartRecommendLayoutMakingAnimationBase() {}

    public SmartRecommendLayoutMakingAnimationBase(SmartRecommendLayoutMakingAnimationAttribute animationAttribute) {
        this.animationAttribute = animationAttribute;
    }

    public SmartRecommendLayoutMakingAnimationAttribute getAnimationAttribute() {
        return animationAttribute;
    }

    @Override
    public void stop() {
        isStopped = true;
    }

    public boolean isStopped() {
        return isStopped;
    }
}
