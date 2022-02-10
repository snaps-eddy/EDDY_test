package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search;

import android.os.Build;
import android.widget.ImageView;

import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

/**
 * Created by ysjeong on 2018. 1. 19..
 */

public class SnapsMatrixSearchFailedAnimation extends SnapsMatrixAnimationStrategy {

    public SnapsMatrixSearchFailedAnimation(SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener ) {
        setImageSearchCompletedListener(imageSearchCompletedListener);
    }

    @Override
    public void initMatrixWithImageView(ImageView view) {}

    @Override
    public void setDefaultMatrix() {
        if (getImageView() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getImageView().setForeground(null);
            }
        }
    }

    @Override
    public void performAnimation() {
        if (isSuspend() || getImageView() == null) {
            handleSmartSnapsSearchFailed();
            return;
        }

        handleSmartSnapsSearchFailed();

        if (imageSearchCompletedListener != null)
            imageSearchCompletedListener.onfinished();
    }

    @Override
    public void suspendAnimation() {
        setSuspend(true);
        setActiveAnimation(false);
        removeImageLoadProgress();
    }
}
