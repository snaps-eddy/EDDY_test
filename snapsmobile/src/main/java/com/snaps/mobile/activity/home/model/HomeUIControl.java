package com.snaps.mobile.activity.home.model;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.ui.TabStyleNativeScrollViewController;
import com.snaps.mobile.R;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeUIControl {
    private TabStyleNativeScrollViewController wvController = null;
    private View mHomeAlpha; //homeForegroundAlphaView
    private TextView txtPresentBadge;
    private TextView txtCartBadge;
    private LinearLayout linearLayoutWebViewProgress;
    private ImageView imageViewWebViewProgress;

    public TabStyleNativeScrollViewController getWvController() {
        return wvController;
    }

    public void setWvController(TabStyleNativeScrollViewController wvController) {
        this.wvController = wvController;
    }

    public View getmHomeAlpha() {
        return mHomeAlpha;
    }

    public void setmHomeAlpha(View mHomeAlpha) {
        this.mHomeAlpha = mHomeAlpha;
    }

    public TextView getTxtPresentBadge() {
        return txtPresentBadge;
    }

    public void setTxtPresentBadge(TextView txtPresentBadge) {
        this.txtPresentBadge = txtPresentBadge;
    }

    public TextView getTxtCartBadge() {
        return txtCartBadge;
    }

    public void setTxtCartBadge(TextView txtCartBadge) {
        this.txtCartBadge = txtCartBadge;
    }

    public LinearLayout getLinearLayoutWebViewProgress() {
        return linearLayoutWebViewProgress;
    }

    public void setLinearLayoutWebViewProgress(LinearLayout linearLayoutWebViewProgress) {
        this.linearLayoutWebViewProgress = linearLayoutWebViewProgress;
    }

    public ImageView getImageViewWebViewProgress() {
        return imageViewWebViewProgress;
    }

    public void setImageViewWebViewProgress(ImageView imageViewWebViewProgress) {
        this.imageViewWebViewProgress = imageViewWebViewProgress;
        this.imageViewWebViewProgress.setBackgroundResource(R.drawable.webview_progress_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable)imageViewWebViewProgress.getBackground();
        animationDrawable.start();
    }
}
