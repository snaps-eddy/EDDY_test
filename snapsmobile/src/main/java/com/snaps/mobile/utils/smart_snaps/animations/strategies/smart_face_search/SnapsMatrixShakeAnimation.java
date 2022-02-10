package com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search;

import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.snaps.common.R;
import com.snaps.common.utils.ui.ContextUtil;

/**
 * Created by ysjeong on 2018. 1. 19..
 */

public class SnapsMatrixShakeAnimation extends SnapsMatrixAnimationStrategy {

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

        removeImageLoadProgress();

        Animation shakeAnimation = AnimationUtils.loadAnimation(ContextUtil.getContext(), R.anim.anim_shake);
        shakeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setActiveAnimation(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        getImageView().startAnimation(shakeAnimation);
    }

    @Override
    public void suspendAnimation() {
        setSuspend(true);
        removeImageLoadProgress();
    }
}
