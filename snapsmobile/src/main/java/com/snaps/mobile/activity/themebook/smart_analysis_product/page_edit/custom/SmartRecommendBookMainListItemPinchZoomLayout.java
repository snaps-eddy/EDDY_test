package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.custom;

import android.content.Context;
import android.graphics.PointF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.data.img.BRect;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookMainListItemPinchImageInfo;

public class SmartRecommendBookMainListItemPinchZoomLayout extends RelativeLayout implements View.OnTouchListener {

    public interface IPinchZoomLayoutBridge {
        SnapsPageCanvas findSnapsPageCanvasWithTouchOffsetRect(BRect bRect);
        void forceListItemClick(int position);
        void onTouchDown();
    }

    private ScaleGestureDetector scaleDetector;
    private IPinchZoomLayoutBridge pinchZoomLayoutBridge;
    private SmartRecommendBookMainListItemPinchZoomDrawer pinchZoomDrawer = null;
    private PointF ptActionDown = new PointF();
    private int touchPosX1 = 0, touchPosY1 = 0;

    private boolean isSetPinchSettingValue = false;

    public SmartRecommendBookMainListItemPinchZoomLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public SmartRecommendBookMainListItemPinchZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmartRecommendBookMainListItemPinchZoomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPinchZoomLayoutBridge(IPinchZoomLayoutBridge pinchZoomLayoutBridge) {
        this.pinchZoomLayoutBridge = pinchZoomLayoutBridge;

        if (this.pinchZoomDrawer != null) {
            this.pinchZoomDrawer.setPinchZoomLayoutBridge(pinchZoomLayoutBridge);
        }
    }

    private void init() {
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        setOnTouchListener(this);
    }

    public void setPinchZoomDrawer(@NonNull SmartRecommendBookMainListItemPinchZoomDrawer pinchZoomDrawer, SmartRecommendBookMainListItemPinchZoomDrawer.PinchZoomDrawerAttribute attribute) {
        this.pinchZoomDrawer = pinchZoomDrawer;

        this.pinchZoomDrawer.setDrawerAttribute(attribute);
    }

    public boolean isZoomMode() {
        return pinchZoomDrawer != null && pinchZoomDrawer.isPinchZooming();
    }

    public void stopListItemZoomMode() {
        if (pinchZoomDrawer == null) return;
        pinchZoomDrawer.stopPinchZoom();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int act = event.getAction();
        switch(act & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: //첫번째 손가락 터치(드래그 용도)
                touchPosX1 = (int) event.getX();
                touchPosY1 = (int) event.getY();

                if (pinchZoomLayoutBridge != null) {
                    pinchZoomLayoutBridge.onTouchDown();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!pinchZoomDrawer.isPinchZooming()) {
                    if (event.getPointerCount() > 1) {
                        int touchPosX2 = (int) event.getX(1);
                        int touchPosY2 = (int) event.getY(1);
                        if (pinchZoomLayoutBridge != null) {
                            BRect touchRect = getRectOnActionPointDown(touchPosX1, touchPosX2, touchPosY1, touchPosY2);
                            SnapsPageCanvas snapsPageCanvas = pinchZoomLayoutBridge.findSnapsPageCanvasWithTouchOffsetRect(touchRect);
                            if (snapsPageCanvas != null && pinchZoomDrawer != null) {
                                SmartRecommendBookMainListItemPinchImageInfo pinchImageInfo = new SmartRecommendBookMainListItemPinchImageInfo.Builder(getContext())
                                        .setView(snapsPageCanvas)
                                        .setPinchPosition(snapsPageCanvas.getPageNumber(), touchRect.centerX())
                                        .create();
                                BPoint initPoint = pinchZoomDrawer.createPinchZoomView(pinchImageInfo);
                                if (initPoint != null) {
                                    pinchZoomDrawer.setDraggingOffset(initPoint.getX(), initPoint.getY());
                                }

                                pinchZoomDrawer.startPinchZoom();
                                return true;
                            }
                        }

                        if (!isSetPinchSettingValue) { //매번 저장하면 부담되니 그나마, boolean 으로 1번만 저장하도록 처리..
                            isSetPinchSettingValue = true;
                            Setting.set(getContext(), Const_VALUE.KEY_USER_HAD_PINCH_ZOOM_ON_MAIN_LIST, true);
                        }
                    }
                }
                break;
            default :
                break;
        }

        if (pinchZoomDrawer != null && pinchZoomDrawer.isPinchZooming()) return true;

        return super.onInterceptTouchEvent(event);
    }

    private BRect getRectOnActionPointDown(int x1, int x2, int y1, int y2) {
        int maxX = Math.max(x1, x2);
        int minX = Math.min(x1, x2);
        int maxY = Math.max(y1, y2);
        int minY = Math.min(y1, y2);
        return new BRect(minX, minY, maxX, maxY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (pinchZoomDrawer != null && pinchZoomDrawer.isBlockTouchEvent()) return false;

        int act = event.getAction();
        switch(act & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ptActionDown.set(event.getX(), event.getY());

                if (pinchZoomDrawer != null)
                    pinchZoomDrawer.handleOnTouchActionDown(new BPoint((int)event.getX(), (int)event.getY()));
                return true;
            case MotionEvent.ACTION_MOVE:
                if (pinchZoomDrawer != null) {
                    int offsetX = (int) event.getX();
                    if (pinchZoomDrawer.isFullScaleMode()) {
                        int moveX = touchPosX1 - offsetX;

                        pinchZoomDrawer.setMoveX(moveX);

                        pinchZoomDrawer.invalidate();
                    }
                }

                break;

            case MotionEvent.ACTION_UP: // 첫번째 손가락을 떼었을 경우
//            case MotionEvent.ACTION_POINTER_UP: // 두번째 손가락을 떼었을 경우
            case MotionEvent.ACTION_CANCEL:
                if (pinchZoomDrawer != null) {
                    if (checkClickAction(event)) {
                        pinchZoomDrawer.handleOnClick(new BPoint((int)event.getX(), (int)event.getY()));
                    }

                    pinchZoomDrawer.handleOnTouchActionUp();
                }
                break;

            default :
                break;

        }

        scaleDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private boolean checkClickAction(MotionEvent event) {
        if (event == null || ptActionDown == null) return false;
        float moveX = Math.abs(ptActionDown.x - event.getX());
        float moveY = Math.abs(ptActionDown.y - event.getY());
        return moveX < 20 && moveY < 20;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (pinchZoomDrawer != null) {
                if (!pinchZoomDrawer.isBlockTouchEvent()) {
                    pinchZoomDrawer.updateScaleFactor(detector.getScaleFactor());
                }
            }
            return true;
        }
    }
}

