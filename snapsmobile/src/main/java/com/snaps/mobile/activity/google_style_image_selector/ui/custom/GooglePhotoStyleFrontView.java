package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by ysjeong on 2017. 1. 4..
 */

public class GooglePhotoStyleFrontView extends RelativeLayout {

    private FrameLayout animationLayout = null;

    private Context context = null;

    public GooglePhotoStyleFrontView(Context context) {
        super(context);
        init(context);
    }

    public GooglePhotoStyleFrontView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GooglePhotoStyleFrontView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        createAnimationLayout();
    }

    public FrameLayout getAnimationLayout() {
        return animationLayout;
    }

    private void createAnimationLayout() {
        if (context == null) return;
        this.animationLayout = new FrameLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.animationLayout.setLayoutParams(layoutParams);
        this.animationLayout.setBackgroundColor(Color.WHITE);
        this.animationLayout.setVisibility(View.GONE);
        this.addView(animationLayout);
    }

    public void releaseInstance() {
        if (animationLayout != null) {
            animationLayout.removeAllViews();
            animationLayout = null;
        }
    }
}
