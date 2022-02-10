package com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.type;

import android.view.ViewGroup;

import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialImp;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialListener;

public class SnapsGIFTutorialFactory {
    public static SnapsGIFTutorialImp createGIFTutorialHandler(ViewGroup parent, GIFTutorialView.Builder attribute, SnapsGIFTutorialListener gifTutorialListener) {
        switch (attribute.getTutorialType()) {
            case PHONE_FRAGMENT_PINCH_MOTION:
                return new SnapsGIFTutorialPhoneFragmentPinchMotion(parent, attribute, gifTutorialListener);
            case SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP:
                return new SnapsGIFTutorialSmartRecommendBookDrag(parent, attribute, gifTutorialListener);
            case SMART_RECOMMEND_BOOK_SWIPE_PAGE:
                return new SnapsGIFTutorialSmartRecommendBookSwipePage(parent, attribute, gifTutorialListener);
        }
        return null;
    }
}
