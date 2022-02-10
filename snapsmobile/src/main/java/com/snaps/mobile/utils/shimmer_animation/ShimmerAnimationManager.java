package com.snaps.mobile.utils.shimmer_animation;

import android.animation.ValueAnimator;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ShimmerAnimationManager {

    private static final String TAG = ShimmerAnimationManager.class.getSimpleName();

    private Shimmer.AlphaHighlightBuilder shimmer;

    private ShimmerAnimationManager() {
        this.shimmer = new Shimmer.AlphaHighlightBuilder();
    }

    private static class LazyHolder {
        static final ShimmerAnimationManager INSTANCE = new ShimmerAnimationManager();
    }

    public static ShimmerAnimationManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Shimmer getAcrylicShimmer() {
        return shimmer.setBaseAlpha(0.0f)
                .setHighlightAlpha(0.5f)
                .setTilt(35.0f)
                .setDuration(500)
                .setRepeatMode(ValueAnimator.REVERSE)
                .setRepeatDelay(2000)
                .setDropoff(0.3f)
                .setClipToChildren(true)
                .setAutoStart(false)
                .build();
    }

    public Shimmer getSloganShimmer() {
        return shimmer.setBaseAlpha(0.0f)
                .setHighlightAlpha(0.7f)
                .setTilt(45.0f)
                .setRepeatCount(1)
                .setDuration(1)
                .setRepeatDelay(2000)
                .setRepeatMode(ValueAnimator.REVERSE)
                .setDropoff(0.5f)
                .setClipToChildren(true)
                .setAutoStart(false)
                .build();
    }

    public Disposable startShimmer(ShimmerFrameLayout animator, long interval) {
        return Observable.interval(0, interval, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (animator == null || animator.isShimmerStarted()) {
                        return;
                    }
                    animator.startShimmer();
                });
    }
}
