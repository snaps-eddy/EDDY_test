package com.snaps.mobile.tutorial.custom_tutorial;

import android.app.Dialog;

import com.snaps.mobile.tutorial.SnapsTutorialAttribute;

public class CustomTutorialViewStrategyFactory {
    public static CustomTutorialInterface createTutorialStrategy(Dialog dialog, SnapsTutorialAttribute.eCustomTutorialType tutorialType) {
        switch (tutorialType) {
            case RECOMMEND_BOOK_IMAGE_SELECT:
                return new CustomTutorialViewStrategyForRecommendBookImageSelect(dialog);
        }
        return null;
    }
}
