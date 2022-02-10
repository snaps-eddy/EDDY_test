package com.snaps.mobile.utils.smart_snaps.animations;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search.SnapsMatrixAnimationStrategy;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_face_search.SnapsMatrixDefaultAnimation;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

/**
 * Created by ysjeong on 2018. 1. 15..
 */

public class SnapsMatrixAnimation {
    private SnapsMatrixAnimationStrategy matrixAnimationStrategy = null;
    private boolean isInitialized = false;

    public void initAnimation(SnapsMatrixAnimationAttribute matrixAnimationAttribute) {
        this.isInitialized = true;
        this.matrixAnimationStrategy = new SnapsMatrixDefaultAnimation();
        this.matrixAnimationStrategy.init(matrixAnimationAttribute);

        setDefaultMatrix();
    }

    public void changeAnimationStrategy(SnapsMatrixAnimationStrategy strategy) {
        SnapsMatrixAnimationStrategy prevAnimationStrategy = matrixAnimationStrategy;
        if (prevAnimationStrategy != null && strategy != null) {
            strategy.changeStrategy(prevAnimationStrategy);
            strategy.setDefaultMatrix();
        }
        this.matrixAnimationStrategy = strategy;
    }

    public void startAnimation() throws Exception {
        if (matrixAnimationStrategy != null) {
            matrixAnimationStrategy.performAnimation();
        }
    }

    private void setDefaultMatrix() { //center 맞추기
        if (matrixAnimationStrategy != null)
            matrixAnimationStrategy.setDefaultMatrix();
    }

    public void suspendAnimation() {
        if (matrixAnimationStrategy != null) {
            matrixAnimationStrategy.setActiveAnimation(false);
            matrixAnimationStrategy.suspendAnimation();
        }
    }

    public boolean isActiveAnimation() {
        return matrixAnimationStrategy != null && matrixAnimationStrategy.isActiveAnimation();
    }

    public void setActiveAnimation(boolean isActive) {
        if (matrixAnimationStrategy != null)
            matrixAnimationStrategy.setActiveAnimation(isActive);
    }

    public void setImageData(MyPhotoSelectImageData imageData) {
        if (matrixAnimationStrategy != null)
            matrixAnimationStrategy.setImageData(imageData);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public static class SnapsMatrixAnimationAttribute {
        private Context context;
        private ImageView iv;
        private Bitmap imageBitmap;
        private MyPhotoSelectImageData imageData;
        private long animationDuring;
        private boolean isRealPagerView;
        private int controlWidth, controlHeight;
        private SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener;

        private SnapsMatrixAnimationAttribute(Builder builder) {
            this.context = builder.context;
            this.iv = builder.iv;
            this.imageBitmap = builder.imageBitmap;
            this.imageData = builder.imageData;
            this.animationDuring = builder.animationDuring;
            this.isRealPagerView = builder.isRealPagerView;
            this.controlWidth = builder.controlWidth;
            this.controlHeight = builder.controlHeight;
            this.imageSearchCompletedListener = builder.imageSearchCompletedListener;
        }

        public int getControlWidth() {
            return controlWidth;
        }

        public int getControlHeight() {
            return controlHeight;
        }

        public Bitmap getImageBitmap() {
            return imageBitmap;
        }

        public Context getContext() {
            return context;
        }

        public ImageView getIv() {
            return iv;
        }

        public MyPhotoSelectImageData getImageData() {
            return imageData;
        }

        public long getAnimationDuring() {
            return animationDuring;
        }

        public boolean isRealPagerView() {
            return isRealPagerView;
        }

        public SnapsImageViewTarget.ImageSearchCompletedListener getImageSearchCompletedListener() {
            return imageSearchCompletedListener;
        }

        public static class Builder {
            private Context context;
            private ImageView iv;
            private Bitmap imageBitmap;
            private MyPhotoSelectImageData imageData;
            private long animationDuring;
            private boolean isRealPagerView;
            private int controlWidth, controlHeight;
            private SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener;

            public Builder setControlWidth(int controlWidth) {
                this.controlWidth = controlWidth;
                return this;
            }

            public Builder setControlHeight(int controlHeight) {
                this.controlHeight = controlHeight;
                return this;
            }

            public Builder setImageBitmap(Bitmap imageBitmap) {
                this.imageBitmap = imageBitmap;
                return this;
            }

            public Builder setContext(Context context) {
                this.context = context;
                return this;
            }

            public Builder setIv(ImageView iv) {
                this.iv = iv;
                return this;
            }

            public Builder setImageData(MyPhotoSelectImageData imageData) {
                this.imageData = imageData;
                return this;
            }

            public Builder setAnimationDuring(long animationDuring) {
                this.animationDuring = animationDuring;
                return this;
            }

            public Builder setRealPagerView(boolean realPagerView) {
                isRealPagerView = realPagerView;
                return this;
            }

            public Builder setImageSearchCompletedListener(SnapsImageViewTarget.ImageSearchCompletedListener imageSearchCompletedListener) {
                this.imageSearchCompletedListener = imageSearchCompletedListener;
                return this;
            }

            public SnapsMatrixAnimationAttribute create() {
                return new SnapsMatrixAnimationAttribute(this);
            }
        }

    }
}
