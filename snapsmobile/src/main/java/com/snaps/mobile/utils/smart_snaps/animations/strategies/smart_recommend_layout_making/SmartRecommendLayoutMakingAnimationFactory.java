package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making;

import java.util.Random;

import errorhandle.logger.Logg;

public class SmartRecommendLayoutMakingAnimationFactory {
    public enum eSmartRecommendLayoutMakingAnimationType {
        ZOOM_IN,
        ZOOM_IN_WEAK,
        ZOOM_OUT,
        ZOOM_OUT_WEAK,
        TRANSLATE_LEFT_TO_RIGHT,
        TRANSLATE_LEFT_TO_RIGHT_WITH_ZOOM_IN,
        TRANSLATE_LEFT_TO_RIGHT_WITH_ZOOM_OUT,
        TRANSLATE_LEFT_TO_RIGHT_WEAK,
    }

    public static ISmartRecommendLayoutMakingAnimationImp createAnimation(eSmartRecommendLayoutMakingAnimationType type, SmartRecommendLayoutMakingAnimationAttribute animationAttribute) {
        switch (type) {
            case ZOOM_IN:
                return new SmartRecommendLayoutMakingAnimationZoomIn(animationAttribute);
            case ZOOM_IN_WEAK:
                return new SmartRecommendLayoutMakingAnimationZoomInWeak(animationAttribute);
            case ZOOM_OUT:
                return new SmartRecommendLayoutMakingAnimationZoomOut(animationAttribute);
            case ZOOM_OUT_WEAK:
                return new SmartRecommendLayoutMakingAnimationZoomOutWeak(animationAttribute);
            case TRANSLATE_LEFT_TO_RIGHT:
                return new SmartRecommendLayoutMakingAnimationTranslateLeftToRight(animationAttribute);
            case TRANSLATE_LEFT_TO_RIGHT_WEAK:
                return new SmartRecommendLayoutMakingAnimationTranslateLeftToRightWeak(animationAttribute);
            case TRANSLATE_LEFT_TO_RIGHT_WITH_ZOOM_IN:
                return new SmartRecommendLayoutMakingAnimationTranslateLeftToRightWithZoomIn(animationAttribute);
            case TRANSLATE_LEFT_TO_RIGHT_WITH_ZOOM_OUT:
                return new SmartRecommendLayoutMakingAnimationTranslateLeftToRightWithZoomOut(animationAttribute);
        }
        return null;
    }

    public static ISmartRecommendLayoutMakingAnimationImp createRandomAnimation(SmartRecommendLayoutMakingAnimationAttribute animationAttribute) {
        Random random = new Random();
        int randomNumber = random.nextInt(100);

        eSmartRecommendLayoutMakingAnimationType animationType = null;
        if (randomNumber < 30) { //확률
            if (animationAttribute.getPrevAnimation() == eSmartRecommendLayoutMakingAnimationType.ZOOM_OUT_WEAK) {
                return createRandomAnimation(animationAttribute);
            }
            animationType = eSmartRecommendLayoutMakingAnimationType.ZOOM_IN;
        } else  if (randomNumber < 40) {
            if (animationAttribute.getPrevAnimation() == eSmartRecommendLayoutMakingAnimationType.ZOOM_OUT_WEAK) {
                return createRandomAnimation(animationAttribute);
            }
            animationType = eSmartRecommendLayoutMakingAnimationType.ZOOM_IN_WEAK;
        } else  if (randomNumber < 60) {
            if (animationAttribute.getPrevAnimation() == eSmartRecommendLayoutMakingAnimationType.ZOOM_IN_WEAK) {
                return createRandomAnimation(animationAttribute);
            }
            animationType = eSmartRecommendLayoutMakingAnimationType.ZOOM_OUT;
        } else  if (randomNumber < 65) {
            if (animationAttribute.getPrevAnimation() == eSmartRecommendLayoutMakingAnimationType.ZOOM_IN_WEAK) {
                return createRandomAnimation(animationAttribute);
            }
            animationType = eSmartRecommendLayoutMakingAnimationType.ZOOM_OUT_WEAK;
        } else if (randomNumber < 75) {
            animationType = eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT;
        } else if (randomNumber < 85) {
            animationType = eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT_WITH_ZOOM_IN;
        } else if (randomNumber < 90) {
            animationType = eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT_WITH_ZOOM_OUT;
        } else {
            if (animationAttribute.getPrevAnimation() == eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT
                    || animationAttribute.getPrevAnimation() == eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT_WEAK) {
                return createRandomAnimation(animationAttribute);
            }
            animationType = eSmartRecommendLayoutMakingAnimationType.TRANSLATE_LEFT_TO_RIGHT_WEAK;
        }

        return createAnimation(animationType, animationAttribute);
    }
}
