package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.data.img.BRect;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BSize;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.spc.SmartRecommendBookEditListItemCanvas;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookMainListItemPinchImageInfo;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;
import font.FTextView;

public class SmartRecommendBookMainListItemPinchZoomDrawer extends FrameLayout {
    private static final String TAG = SmartRecommendBookMainListItemPinchZoomDrawer.class.getSimpleName();

    public enum eZoomMode {
        NONE,
        ZOOM_IN,
        ZOOM_OUT
    }

    private static final float MAX_SCALE_VALUE = 2.15f;
    private static final float MAX_SCALE_VALUE_FOR_COVER = 1.6f; //커버는 더 커서 조금 작게..
    private static final float DEFAULT_SCALE_VALUE = 1.f;
    private static final long AUTO_FIT_TO_MAX_SCALE_ANIMATION_DURING = 120; //자동으로 맞춰주는 시간
    private static final long AUTO_CLOSE_VIEW_DURING = 60; //닫는 애니메이션 시간

    private boolean isPinchZooming = false;

    private PinchZoomingView pinchZoomingView = null;

    private BPoint canvasDragOffsetPoint = new BPoint();
    private int moveX = 0;

    private SnapsPageCanvas pinchTargetPageCanvas = null;

    private float scaleFactor = 1.f;
    private boolean isAutoFullScaleAnimation = false;
    private boolean isAutoDefaultScaleAnimation = false;
    private boolean isFullScaleMode = false;
    private int scaleOffsetX = 0;
    private BRect draggingViewOffsetRect = null;
    private BPoint defaultCenterOffsetPoint = new BPoint();
    private eZoomMode zoomMode = eZoomMode.NONE;

    private boolean isBlockTouchEvent = false;
    private boolean onTouchDownEditBtn = false;

    private int listItemPosition = -1;
    private boolean isCoverItem = false;

    private FTextView editBtn = null;

    private SmartRecommendBookMainListItemPinchZoomDrawer.PinchZoomDrawerAttribute drawerAttribute = null;
    private int bottomLayoutHeight = 0;

    private SmartRecommendBookMainListItemPinchZoomLayout.IPinchZoomLayoutBridge pinchZoomLayoutBridge;

    private SmartSnapsConstants.ePinchZoomPivotX pinchZoomPivotX = SmartSnapsConstants.ePinchZoomPivotX.CENTER;

    public SmartRecommendBookMainListItemPinchZoomDrawer(@NonNull Context context) {
        super(context);

        init();
    }

    public SmartRecommendBookMainListItemPinchZoomDrawer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SmartRecommendBookMainListItemPinchZoomDrawer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        createEditBtn();
    }

    private void createEditBtn() {
        editBtn = new FTextView(getContext());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, UIUtil.convertDPtoPX(getContext(), 48));
        layoutParams.gravity = Gravity.BOTTOM;
        editBtn.setLayoutParams(layoutParams);
        editBtn.setBackgroundColor(Color.parseColor("#e6e36a63"));
        editBtn.setText(R.string.do_edit);
        editBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.f);
        editBtn.setTextColor(Color.WHITE);
        editBtn.setGravity(Gravity.CENTER);

        FontUtil.applyTextViewTypeface(editBtn, FontUtil.eSnapsFonts.YOON_GOTHIC_740);

        editBtn.setAlpha(0.f);

        addView(editBtn);
    }

    public void setDrawerAttribute(SmartRecommendBookMainListItemPinchZoomDrawer.PinchZoomDrawerAttribute attribute) {
        this.drawerAttribute = attribute;
        bottomLayoutHeight = (int) getResources().getDimension(R.dimen.smart_recommend_book_main_act_bottom_layout_height);
    }

    public void setPinchZoomLayoutBridge(SmartRecommendBookMainListItemPinchZoomLayout.IPinchZoomLayoutBridge pinchZoomLayoutBridge) {
        this.pinchZoomLayoutBridge = pinchZoomLayoutBridge;
    }

    public void startPinchZoom() {
        scaleFactor = 1.f;
        isAutoFullScaleAnimation = false;
        isAutoDefaultScaleAnimation = false;
        isBlockTouchEvent = false;

        isPinchZooming = true;

        draggingViewOffsetRect = getPinchViewOffsetRect();
        if (draggingViewOffsetRect != null) {
            defaultCenterOffsetPoint = new BPoint(draggingViewOffsetRect.centerX(), draggingViewOffsetRect.centerY());
        }
    }

    private int getDefaultCenterOffsetY() {
        return defaultCenterOffsetPoint != null ? defaultCenterOffsetPoint.getY() : 0;
    }

    private int getDefaultCenterOffsetX() {
        return defaultCenterOffsetPoint != null ? defaultCenterOffsetPoint.getX() : 0;
    }

    public BPoint createPinchZoomView(SmartRecommendBookMainListItemPinchImageInfo pinchImageInfo) {
        if (pinchImageInfo == null) return null;

        pinchTargetPageCanvas = pinchImageInfo.getView();
        if (pinchTargetPageCanvas == null) return null;

        pinchZoomPivotX = pinchImageInfo.getPinchZoomPivotX();

        listItemPosition = pinchImageInfo.getItemPosition();

        try {
            isCoverItem= pinchImageInfo.getItemPosition() == 0;
            View bonusLayer = pinchTargetPageCanvas.getBonusLayer();
            if (isCoverItem)
                bonusLayer.setVisibility(View.GONE);

            if (pinchTargetPageCanvas instanceof SmartRecommendBookEditListItemCanvas) {
                ((SmartRecommendBookEditListItemCanvas)pinchTargetPageCanvas).setSelectorViewAlpha(0.f);
            }

            pinchTargetPageCanvas.setDrawingCacheEnabled(true);
            Bitmap draggingImageBitmap = Bitmap.createBitmap(pinchTargetPageCanvas.getDrawingCache());
            draggingImageBitmap = CropUtil.getInSampledScaleBitmap(draggingImageBitmap, 1.f);
            Bitmap closeBtnBitmap = CropUtil.getInSampledDecodeBitmapFromResource(getResources(), R.drawable.btn_close_white);

            if (BitmapUtil.isUseAbleBitmap(draggingImageBitmap) && BitmapUtil.isUseAbleBitmap(closeBtnBitmap)) {
                pinchZoomingView = new PinchZoomingView.Builder()
                        .setZoomImageBitmap(draggingImageBitmap)
                        .setZoomImageSize(new BSize(draggingImageBitmap.getWidth(), draggingImageBitmap.getHeight()))
                        .setCloseBtnBitmap(closeBtnBitmap)
                        .setCloseBtnSize(new BSize(closeBtnBitmap.getWidth(), closeBtnBitmap.getHeight()))
                        .create();
            }

            if (isCoverItem)
                bonusLayer.setVisibility(View.VISIBLE);

            pinchTargetPageCanvas.setAlpha(.0f);
        } catch (Exception | OutOfMemoryError e) {
            pinchTargetPageCanvas.setAlpha(1f);
        }

        Rect viewRect = new Rect();
        pinchTargetPageCanvas.getGlobalVisibleRect(viewRect);
        try {
            viewRect.offset(0, -UIUtil.getStatusBarHeight());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        try {
            return new BPoint(viewRect.centerX(), viewRect.centerY());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public void handleOnClick(BPoint clickPoint) {
        if (clickPoint == null || !isFullScaleMode()) return;

        if (isCloseBtnClicked(clickPoint)) {
            startClosePinchModeAnimation(false);
        } else if (isEditBtnClicked(clickPoint)) {
            startClosePinchModeAnimation(true);
        }

        onTouchDownEditBtn = false;
    }

    private boolean isCloseBtnClicked(BPoint clickPoint) {
        if (clickPoint == null || pinchZoomingView == null || pinchZoomingView.getCloseBtnRect() == null) return false;
        return pinchZoomingView.getCloseBtnRect().contains(clickPoint.getX(), clickPoint.getY());
    }

    private boolean isEditBtnClicked(BPoint clickPoint) {
        if (clickPoint == null || pinchZoomingView == null || pinchZoomingView.getEditBtnRect() == null) return false;
        return pinchZoomingView.getEditBtnRect().contains(clickPoint.getX(), clickPoint.getY());
    }

    public void handleOnTouchActionDown(BPoint clickPoint) {
        if (clickPoint == null || !isFullScaleMode()) return;

        if (isEditBtnClicked(clickPoint)) {
            onTouchDownEditBtn = true;
            invalidate();
        }
     }

    public void handleOnTouchActionUp() {
        if (isAutoFullScaleAnimation) {
            updateBottomLayoutState(1.f);

            isFullScaleMode = true;
            isAutoFullScaleAnimation = false;

            OrientationSensorManager.getInstance().setBlockSensorEvent(true);

            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_scaleupPage)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(listItemPosition))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        } else if (isAutoDefaultScaleAnimation) {
            isFullScaleMode = false;
            isAutoDefaultScaleAnimation = false;
        } else if (isFullScaleMode) {
            if (draggingViewOffsetRect != null)
                draggingViewOffsetRect.offset(-moveX, 0);
            moveX = 0;
        } else {
            stopPinchZoom();
        }

        onTouchDownEditBtn = false;
    }

    public void stopPinchZoom() {
        if (!isPinchZooming) return;
        try {
            OrientationSensorManager.getInstance().setBlockSensorEvent(false);

            isPinchZooming = false;
            isAutoFullScaleAnimation = false;
            isAutoDefaultScaleAnimation = false;
            isFullScaleMode = false;
            setBackgroundColor(0);

            if (pinchTargetPageCanvas != null) {
                pinchTargetPageCanvas.setAlpha(1f);
            }

            canvasDragOffsetPoint.set(-1, -1);

            moveX = 0;

            if (pinchZoomingView != null) {
                pinchZoomingView.releaseBitmap();
                pinchZoomingView = null;
            }

            if (editBtn != null) {
                editBtn.setAlpha(0.f);
            }

            updateBottomLayoutState(0.f);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private BRect getPinchViewOffsetRect() {
        if (pinchZoomingView == null) return null;

        BSize size = pinchZoomingView.getZoomImageBitmapSize();
        if (size != null) {
            int resultX = (int) (canvasDragOffsetPoint.getX() - (size.getWidth()/2));
            int resultY = (int) (canvasDragOffsetPoint.getY() - (size.getHeight()/2));
            return new BRect(resultX, resultY, (int) (resultX + size.getWidth()), (int) (resultY + size.getHeight()));
        }

        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isShowDraggingView()) return;

        if (isFullScaleMode) {
            drawListItemWhenFullScaleMode(canvas);
        } else {
            drawListItem(canvas);
        }
    }

    private void drawListItemWhenFullScaleMode(Canvas canvas) {
        if (canvas == null) return;
        int centerDistance = 0;
        float updateValue = 0.f;
        int restoreScaleCenterOffsetX = scaleOffsetX;
        int targetCenterOffsetY = 0;
        int movedX = moveX;
        switch (zoomMode) {
            case ZOOM_IN:
                targetCenterOffsetY = UIUtil.getScreenHeight(getContext()) / 2;
                centerDistance = targetCenterOffsetY - draggingViewOffsetRect.centerY();
                updateValue = updateScaleValue();

                centerDistance = (int) (centerDistance * updateValue);
                break;
            case ZOOM_OUT:
                targetCenterOffsetY = getDefaultCenterOffsetY();
                centerDistance = targetCenterOffsetY - draggingViewOffsetRect.centerY();
                updateValue = updateScaleValue();

                float reverseUpdateValue = 1.f - updateValue;  //0 TO 1
                centerDistance = (int) (centerDistance * reverseUpdateValue);

                int movedOffsetX = (draggingViewOffsetRect.centerX() - getDefaultCenterOffsetX());
                movedX = (int) (movedOffsetX * reverseUpdateValue);
                break;
        }

        int scaleCenterOffsetY = draggingViewOffsetRect.centerY() + centerDistance;

        int fixOffsetTop = scaleCenterOffsetY - (draggingViewOffsetRect.height() / 2);

        draggingViewOffsetRect.set(draggingViewOffsetRect.left, fixOffsetTop, draggingViewOffsetRect.left + draggingViewOffsetRect.width(), fixOffsetTop + draggingViewOffsetRect.height());

        canvas.save();

        canvas.scale(scaleFactor, scaleFactor, restoreScaleCenterOffsetX, scaleCenterOffsetY);
        canvas.drawBitmap(pinchZoomingView.getZoomImageBitmap(), draggingViewOffsetRect.left-movedX, draggingViewOffsetRect.top, null);
        canvas.restore();

        drawOptionBtn(canvas, draggingViewOffsetRect);
    }

    private void drawListItem(Canvas canvas) {
        if (canvas == null) return;

        switch (pinchZoomPivotX) {
            case LEFT:
                scaleOffsetX = UIUtil.convertDPtoPX(getContext(), 60);
                break;
            case CENTER:
                scaleOffsetX = draggingViewOffsetRect.centerX();
                break;
            case RIGHT:
                scaleOffsetX = UIUtil.getScreenWidth(getContext()) - UIUtil.convertDPtoPX(getContext(), 60);
                break;
        }

        int screenCenterY = UIUtil.getScreenHeight(getContext()) / 2;

        int centerDistance = screenCenterY - draggingViewOffsetRect.centerY();

        centerDistance = (int) (centerDistance * updateScaleValue());

        int scaleCenterOffsetY = draggingViewOffsetRect.centerY() + centerDistance;

        int fixOffsetTop = scaleCenterOffsetY - (draggingViewOffsetRect.height() / 2);

        draggingViewOffsetRect.set(draggingViewOffsetRect.left, fixOffsetTop, draggingViewOffsetRect.left + draggingViewOffsetRect.width(), fixOffsetTop + draggingViewOffsetRect.height());

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor, scaleOffsetX, scaleCenterOffsetY);
        canvas.drawBitmap(pinchZoomingView.getZoomImageBitmap(), draggingViewOffsetRect.left, draggingViewOffsetRect.top, null);
        canvas.restore();

        drawOptionBtn(canvas, draggingViewOffsetRect);

        updateBottomLayoutState(updateScaleValue());
    }

    private void updateBottomLayoutState(float value) { //0 to 1
        if (drawerAttribute == null) return;

        float reverseValue = 1.f - value;

        View bottomLayout = drawerAttribute.getBottomLayout();
        if (bottomLayout != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
            layoutParams.bottomMargin = (int) -(bottomLayoutHeight * value);
            bottomLayout.setLayoutParams(layoutParams);

            bottomLayout.setAlpha(reverseValue);
        }

        View fullScreenBtn = drawerAttribute.getFullScreenBtn();
        if (fullScreenBtn != null) {
            fullScreenBtn.setAlpha(reverseValue);
        }

        View scrollToTopBtn = drawerAttribute.getScrollToTopBtn();
        if (scrollToTopBtn != null) {
            scrollToTopBtn.setAlpha(reverseValue);
        }
    }

    private void drawOptionBtn(Canvas canvas, BRect listItemRect) {
        if (canvas == null || listItemRect == null) return;

        BSize closeBtnImageSize = pinchZoomingView.getCloseBtnSize();
        if (closeBtnImageSize == null) return;

        int closeBtnOffsetLeft = (int) (UIUtil.getScreenWidth(getContext()) - closeBtnImageSize.getWidth() - UIUtil.convertDPtoPX(getContext(),8));
        int closeBtnOffsetTop = UIUtil.convertDPtoPX(getContext(),5);

        Paint closeBtnPaint = new Paint();
        final int MAX_ALPHA = 255;
        int alpha = (int) (MAX_ALPHA * updateScaleValue());
        if (alpha < 210) alpha *= .7f;
        closeBtnPaint.setAlpha(alpha);
        canvas.drawBitmap(pinchZoomingView.getCloseBtnBitmap(), closeBtnOffsetLeft, closeBtnOffsetTop, closeBtnPaint);

        if (editBtn != null) {
            editBtn.setAlpha(updateScaleValue());
            int editBtnBgColor = onTouchDownEditBtn ? Color.parseColor("#e6c6564f") : Color.parseColor("#e6e36a63");
            editBtn.setBackgroundColor(editBtnBgColor);

            Rect viewRect = new Rect();
            editBtn.getGlobalVisibleRect(viewRect);

            pinchZoomingView.setEditBtnRect(new BRect(viewRect.left, viewRect.top, viewRect.right, viewRect.bottom));
        }

        pinchZoomingView.setCloseBtnRect(new BRect(closeBtnOffsetLeft, closeBtnOffsetTop, (int) (closeBtnOffsetLeft + closeBtnImageSize.getWidth()), (int) (closeBtnOffsetTop + closeBtnImageSize.getHeight())));
    }

    private boolean isShowDraggingView() {
        return canvasDragOffsetPoint.getX() > 0 && canvasDragOffsetPoint.getY() > 0 && pinchZoomingView != null && draggingViewOffsetRect != null;
    }

    public boolean isPinchZooming() {
        return isPinchZooming;
    }

    public boolean isFullScaleMode() {
        return isFullScaleMode;
    }

    public void setDraggingOffset(int draggingOffsetX, int draggingOffsetY) {
        canvasDragOffsetPoint.set(draggingOffsetX, draggingOffsetY);
    }

    //확대하고 나서 좌우로 스크롤 되는 처리.
    public void setMoveX(int moveX) {
        if (draggingViewOffsetRect == null) return;

        final int CENTER_OFFSET_X = draggingViewOffsetRect.centerX()-moveX;

        final int SCREEN_WIDTH = UIUtil.getScreenWidth(getContext());

        switch (pinchZoomPivotX) {
            case LEFT:
//                Logg.y("center : " + CENTER_OFFSET_X + ", center*2 " + (CENTER_OFFSET_X*2));
                if ((CENTER_OFFSET_X*2) - UIUtil.convertDPtoPX(getContext(), 45) > SCREEN_WIDTH || CENTER_OFFSET_X < UIUtil.convertDPtoPX(getContext(), 25)) {
                    return;
                }
                break;
            case CENTER:
//                Logg.y("center : " + CENTER_OFFSET_X + ", center*2 " + (CENTER_OFFSET_X*2));
                if (CENTER_OFFSET_X + UIUtil.convertDPtoPX(getContext(), 80) > SCREEN_WIDTH || CENTER_OFFSET_X-UIUtil.convertDPtoPX(getContext(), 85) < 0) {
                    return;
                }
                break;
            case RIGHT:
//                Logg.y("center : " + CENTER_OFFSET_X + ", center*2 " + (CENTER_OFFSET_X*2));
                if ((CENTER_OFFSET_X*2) + UIUtil.convertDPtoPX(getContext(), 45) < SCREEN_WIDTH || CENTER_OFFSET_X+UIUtil.convertDPtoPX(getContext(), 25) > SCREEN_WIDTH) {
                    return;
                }
                break;
        }

        this.moveX = moveX;
    }

    public void updateScaleFactor(float factor) {
        if (isActiveScaleAnimation()) return;

        zoomMode = factor > 1 ? eZoomMode.ZOOM_IN : factor < 1 ? eZoomMode.ZOOM_OUT : eZoomMode.NONE;

        if (shouldAutoScaleToMaxScale()) {
            startFullScaleAnimation();
        } else if (shouldAutoScaleToDefaultScale()) {
            startDefaultScaleAnimation();
        } else {
            this.scaleFactor *= factor;
            this.scaleFactor = Math.max(1, Math.min(this.scaleFactor, getMaxScaleValue()));
            updateBackgroundColorByScaleFactor();
        }

        invalidate();
    }

    private boolean shouldAutoScaleToMaxScale() {
        return !isFullScaleMode && scaleFactor > getAutoScaleToMaxScaleOffset();
    }

    private boolean shouldAutoScaleToDefaultScale() {
        return isFullScaleMode && scaleFactor < getAutoScaleToDefaultScaleOffset();
    }

    private float getAutoScaleToMaxScaleOffset() {
        return DEFAULT_SCALE_VALUE + .15f;
    }

    private float getAutoScaleToDefaultScaleOffset() {
        float offsetValue = isCoverItem ? MAX_SCALE_VALUE_FOR_COVER : MAX_SCALE_VALUE;
        return offsetValue - .15f;
    }

    private float getMaxScaleValue() {
        return isCoverItem ? MAX_SCALE_VALUE_FOR_COVER : MAX_SCALE_VALUE;
    }

    private void updateBackgroundColorByScaleFactor() {
        if (!isPinchZooming || drawerAttribute == null) return;

        final int MAX_ALPHA = 214;
        int alpha = Math.min(MAX_ALPHA, Math.max(0, (int) (MAX_ALPHA * updateScaleValue())));
        setBackgroundColor(Color.argb(alpha, 25, 25, 25));
    }

    private float updateScaleValue() {
        float diffScale = getMaxScaleValue() - DEFAULT_SCALE_VALUE;
        float increaseValue = scaleFactor - DEFAULT_SCALE_VALUE;
        return increaseValue / diffScale;
    }

    private void startFullScaleAnimation() {
        if (isFullScaleMode || isActiveScaleAnimation()) return;

        isAutoFullScaleAnimation = true;
        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        viewParamsAnimator.setDuration(AUTO_FIT_TO_MAX_SCALE_ANIMATION_DURING);
        viewParamsAnimator.setInterpolator(new AccelerateInterpolator());
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation != null) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    float remainScaleValue = (getMaxScaleValue() - getAutoScaleToMaxScaleOffset()) * animatedValue;
                    scaleFactor = Math.min(getMaxScaleValue(), getAutoScaleToMaxScaleOffset() + remainScaleValue);
                    updateBackgroundColorByScaleFactor();
                    SmartRecommendBookMainListItemPinchZoomDrawer.this.invalidate();
                }
            }
        });

        viewParamsAnimator.start();
    }

    private void startDefaultScaleAnimation() {
        if (!isFullScaleMode || isActiveScaleAnimation()) return;

        isBlockTouchEvent = true;
        isAutoDefaultScaleAnimation = true;
        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        viewParamsAnimator.setDuration(AUTO_FIT_TO_MAX_SCALE_ANIMATION_DURING);
        viewParamsAnimator.setInterpolator(new AccelerateInterpolator());
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation != null) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    float remainScaleValue = (getAutoScaleToDefaultScaleOffset() - DEFAULT_SCALE_VALUE) * animatedValue;
                    scaleFactor = Math.max(DEFAULT_SCALE_VALUE, getAutoScaleToDefaultScaleOffset() - remainScaleValue);
                    updateBackgroundColorByScaleFactor();
                    SmartRecommendBookMainListItemPinchZoomDrawer.this.invalidate();

                    if (animatedValue == 1.f) {
                        stopPinchZoom();
                    }
                }
            }
        });

        viewParamsAnimator.start();
    }

    private void startClosePinchModeAnimation(final boolean shouldEditItemAfterClosed) {
        if (!isFullScaleMode || isActiveScaleAnimation()) return;

        isBlockTouchEvent = true;
        isAutoDefaultScaleAnimation = true;
        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        viewParamsAnimator.setDuration(AUTO_CLOSE_VIEW_DURING);
        viewParamsAnimator.setInterpolator(new AccelerateInterpolator());
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation != null) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    float remainScaleValue = (getAutoScaleToDefaultScaleOffset() - DEFAULT_SCALE_VALUE) * animatedValue;
                    scaleFactor = Math.max(DEFAULT_SCALE_VALUE, getAutoScaleToDefaultScaleOffset() - remainScaleValue);
                    updateBackgroundColorByScaleFactor();
                    SmartRecommendBookMainListItemPinchZoomDrawer.this.invalidate();

                    if (animatedValue == 1.f) {
                        stopPinchZoom();

                        if (shouldEditItemAfterClosed && pinchZoomLayoutBridge != null) {
                            pinchZoomLayoutBridge.forceListItemClick(listItemPosition);
                        }
                    }
                }
            }
        });

        viewParamsAnimator.start();
    }

    public boolean isBlockTouchEvent() {
        return isBlockTouchEvent;
    }

    private boolean isActiveScaleAnimation() {
        return isAutoFullScaleAnimation || isAutoDefaultScaleAnimation;
    }

    public static class PinchZoomDrawerAttribute {
        private View bottomLayout;
        private View fullScreenBtn;
        private View scrollToTopBtn;

        private PinchZoomDrawerAttribute(Builder builder) {
            this.bottomLayout = builder.bottomLayout;
            this.fullScreenBtn = builder.fullScreenBtn;
            this.scrollToTopBtn = builder.scrollToTopBtn;
        }

        public View getBottomLayout() {
            return bottomLayout;
        }

        public View getFullScreenBtn() {
            return fullScreenBtn;
        }

        public View getScrollToTopBtn() {
            return scrollToTopBtn;
        }

        public static class Builder {
            private View bottomLayout;
            private View fullScreenBtn;
            private View scrollToTopBtn;

            public Builder setBottomLayout(View bottomLayout) {
                this.bottomLayout = bottomLayout;
                return this;
            }

            public Builder setFullScreenBtn(View fullScreenBtn) {
                this.fullScreenBtn = fullScreenBtn;
                return this;
            }

            public Builder setScrollToTopBtn(View scrollToTopBtn) {
                this.scrollToTopBtn = scrollToTopBtn;
                return this;
            }

            public PinchZoomDrawerAttribute create() {
                return new PinchZoomDrawerAttribute(this);
            }
        }
    }

    public static class PinchZoomingView {
        private Bitmap zoomImageBitmap = null;
        private Bitmap closeBtnBitmap = null;
        private BSize zoomImageBitmapSize = null;
        private BSize closeBtnSize = null;

        private BRect closeBtnRect = null;
        private BRect editBtnRect = null;

        private PinchZoomingView(Builder builder) {
            this.zoomImageBitmap = builder.zoomImageBitmap;
            this.closeBtnBitmap = builder.closeBtnBitmap;
            this.zoomImageBitmapSize = builder.zoomImageSize;
            this.closeBtnSize = builder.closeBtnSize;
        }

        public void releaseBitmap() {
            BitmapUtil.bitmapRecycle(getZoomImageBitmap());
            BitmapUtil.bitmapRecycle(getCloseBtnBitmap());
        }

        public BRect getCloseBtnRect() {
            return closeBtnRect;
        }

        public void setCloseBtnRect(BRect closeBtnRect) {
            this.closeBtnRect = closeBtnRect;
        }

        public BRect getEditBtnRect() {
            return editBtnRect;
        }

        public void setEditBtnRect(BRect editBtnRect) {
            this.editBtnRect = editBtnRect;
        }

        public Bitmap getZoomImageBitmap() {
            return zoomImageBitmap;
        }

        public Bitmap getCloseBtnBitmap() {
            return closeBtnBitmap;
        }

        public BSize getZoomImageBitmapSize() {
            return zoomImageBitmapSize;
        }

        public BSize getCloseBtnSize() {
            return closeBtnSize;
        }

        public static class Builder {
            private Bitmap zoomImageBitmap = null;
            private Bitmap closeBtnBitmap = null;
            private BSize zoomImageSize = null;
            private BSize closeBtnSize = null;
            private BSize editBtnSize = null;

            public Builder setZoomImageBitmap(Bitmap zoomImageBitmap) {
                this.zoomImageBitmap = zoomImageBitmap;
                return this;
            }

            public Builder setCloseBtnBitmap(Bitmap closeBtnBitmap) {
                this.closeBtnBitmap = closeBtnBitmap;
                return this;
            }

            public Builder setEditBtnSize(BSize editBtnSize) {
                this.editBtnSize = editBtnSize;
                return this;
            }

            public Builder setZoomImageSize(BSize zoomImageSize) {
                this.zoomImageSize = zoomImageSize;
                return this;
            }

            public Builder setCloseBtnSize(BSize closeBtnSize) {
                this.closeBtnSize = closeBtnSize;
                return this;
            }

            public PinchZoomingView create() {
                return new PinchZoomingView(this);
            }
        }
    }
}

