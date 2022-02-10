package com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.type;

import android.content.Context;
import android.view.ViewGroup;

import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialImp;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialListener;

public abstract class SnapsGIFTutorialBase implements SnapsGIFTutorialImp {
    private ViewGroup parent;
    private Context context;
    private GIFTutorialView.Builder attribute;
    private SnapsGIFTutorialListener gifTutorialListener;

    private SnapsGIFTutorialBase() {}

    SnapsGIFTutorialBase(ViewGroup parent, GIFTutorialView.Builder attribute, SnapsGIFTutorialListener gifTutorialListener) {
        this.parent = parent;
        this.attribute = attribute;
        this.context = parent.getContext();
        this.gifTutorialListener = gifTutorialListener;

        initializeWithBuilder(attribute);
    }

    public SnapsGIFTutorialListener getGifTutorialListener() {
        return gifTutorialListener;
    }

    public ViewGroup getParent() {
        return parent;
    }

    public Context getContext() {
        return context;
    }

    public GIFTutorialView.Builder getAttribute() {
        return attribute;
    }
}
