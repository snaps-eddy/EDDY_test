package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.custom;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.snaps.common.data.img.BPoint;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BSize;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookEditDragImageInfo;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies.ISmartRecommendBookPageEditDragNDropBridge;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SmartRecommendBookPageEditDragNDropLayout extends FrameLayout implements View.OnDragListener {
    private static final String TAG = SmartRecommendBookPageEditDragNDropLayout.class.getSimpleName();

    private boolean isDragging = false;

    private SmartRecommendBookEditDragImageInfo dragImageInfo = null;

    private ISmartRecommendBookPageEditDragNDropBridge editDragNDropBridge = null;

    private DraggingView draggingView = null;

    private Set<Rect> imageControlRectSet = null;
    private Map<Rect, SnapsLayoutControl> imageLayerMap = null;

    private Paint imageControlLinePaint, imageControlFillPaint;
    private int draggingOffsetX = -1, draggingOffsetY = -1;

    private AtomicBoolean isOverlapDragView = new AtomicBoolean(false);
    private Object overlapViewCheckSync = new Object();

    private ImageView dragView = null;

    private Bitmap replaceIconBitmap;
    private BSize replaceIconBitmapSize = null;

    private boolean isCreatedBitmap = false;

    public SmartRecommendBookPageEditDragNDropLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public SmartRecommendBookPageEditDragNDropLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmartRecommendBookPageEditDragNDropLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setEditDragNDropBridge(ISmartRecommendBookPageEditDragNDropBridge editDragNDropBridge) {
        this.editDragNDropBridge = editDragNDropBridge;
    }

    public void releaseInstance() {
        BitmapUtil.bitmapRecycle(replaceIconBitmap);

        stopDragging();
    }

    public boolean isSwappingImage() {
        return dragImageInfo != null && dragImageInfo.isSwapping();
    }

    public SnapsLayoutControl getLongClickedLayoutControl() {
        return dragImageInfo != null ? dragImageInfo.getLongClickedLayoutControl() : null;
    }

    public void startDragging(SmartRecommendBookEditDragImageInfo dragPhoto) {
        if (dragPhoto == null) return;
        this.dragImageInfo = dragPhoto;
        isDragging = true;

        initImageControlRectSet();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startDragAndDrop(ClipData.newPlainText("",""),
                    new MyDragShadowBuilder(dragPhoto.getView()), null, View.DRAG_FLAG_GLOBAL);
        } else {
            startDrag(ClipData.newPlainText("",""),
                    new View.DragShadowBuilder(), null, 0);
        }

        setVisibility(View.VISIBLE);

        UIUtil.performWeakVibration(getContext(), 80);
    }


    // https://codeday.me/ko/qa/20190524/616867.html
    // 어차피 그림자를 그리지 않으니까 아래와 같이 계산 할 필요가 없지만 추후 그림자가 필요할까봐
    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private Point mScaleFactor;
        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v) {
            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            // Defines local variables
            int width = 10;
            int height = 10;

            // Sets the width of the shadow to half the width of the original View
            width = Math.max(width, getView().getWidth() / 2);

            // Sets the height of the shadow to half the height of the original View
            height = Math.max(height, getView().getHeight() / 2);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);
            // Sets size parameter to member that will be used for scaling shadow image.
            mScaleFactor = size;

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            //아래 코드 주석 풀면 이미지가 반으로 축소되서 가운데 한 더 그려짐.. 추후 참고용이어서 그냥 둠
            // Draws the ColorDrawable in the Canvas passed in from the system.
            //canvas.scale(mScaleFactor.x/(float)getView().getUserSelectWidth(), mScaleFactor.y/(float)getView().getHeight());
            //getView().draw(canvas);
        }
    }



    private void init() {
        imageControlLinePaint = new Paint();
        imageControlLinePaint.setColor(Color.BLACK);
        imageControlLinePaint.setStrokeWidth(UIUtil.convertDPtoPX(getContext(), 1));
        imageControlLinePaint.setStyle(Paint.Style.STROKE);

        imageControlFillPaint = new Paint();
        imageControlFillPaint.setColor(Color.parseColor("#7f191919"));
        imageControlFillPaint.setStyle(Paint.Style.FILL);

        replaceIconBitmap = CropUtil.getInSampledDecodeBitmapFromResource(getResources(), R.drawable.edit_replace);
        if (BitmapUtil.isUseAbleBitmap(replaceIconBitmap)) {
            replaceIconBitmapSize = new BSize(replaceIconBitmap.getWidth(), replaceIconBitmap.getHeight());
        }

        isCreatedBitmap = false;

        setOnDragListener(this);
    }

    private void initImageControlRectSet() {
        if (editDragNDropBridge == null) return;

        imageLayerMap = editDragNDropBridge.getCurrentlyVisibleControlsRect();
        if (imageLayerMap == null) return;

        imageControlRectSet = imageLayerMap.keySet();
    }

    private void createDraggingView() {
        if (dragImageInfo == null) return;

         dragView = dragImageInfo.getView();
        if (dragView == null) return;

        dragView.setAlpha(.5f);

        try {
            dragView.setDrawingCacheEnabled(true);
            Bitmap draggingImageBitmap = Bitmap.createBitmap(dragView.getDrawingCache());
            draggingImageBitmap = CropUtil.getInSampledScaleBitmap(draggingImageBitmap, getDraggingViewScaleValue(draggingImageBitmap));
            if (BitmapUtil.isUseAbleBitmap(draggingImageBitmap) && BitmapUtil.isUseAbleBitmap(replaceIconBitmap)) {
                draggingView = new DraggingView.Builder()
                        .setDraggingImageBitmap(draggingImageBitmap)
                        .setDraggingImageBitmapSize(new BSize(draggingImageBitmap.getWidth(), draggingImageBitmap.getHeight()))
                        .create();
            }
        } catch (Exception | OutOfMemoryError e) {
            dragView.setAlpha(1f);
        }

        isCreatedBitmap = true;
    }

    private float getDraggingViewScaleValue(Bitmap bitmap) {
        if (dragImageInfo == null || !dragImageInfo.isSwapping()) return 1.12f;
        float result = 1.f;
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            ZoomViewCoordInfo zoomViewCoordInfo = dataTransManager.getZoomViewCoordInfo();
            if (zoomViewCoordInfo != null) {
                result = zoomViewCoordInfo.getScaleFactor();
            }
        }

        float weightScaleValue = getScaleWeightValueWithBitmap(bitmap);
        return result + weightScaleValue;
    }

    //작은 이미지는 잘 안보이니까 좀 크게 확대해준다.
    private float getScaleWeightValueWithBitmap(Bitmap bitmap) {
        if (!BitmapUtil.isUseAbleBitmap(bitmap)) return 0;

        final int offset = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (offset < 100) return .4f;
        else if (offset < 200) return .3f;
        else if (offset < 300) return .2f;
        else return .12f;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                createDraggingView();
                return true;
            case DragEvent.ACTION_DROP:
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                stopDragging();
                break;
            default:
                break;
        }

        int offsetX = (int) event.getX();
        int offsetY = (int) event.getY();

        if (isCreatedBitmap) {
            if (offsetX != 0) {
                draggingOffsetX = offsetX;
            }

            if (offsetY != 0) {
                draggingOffsetY = offsetY;
            }
        }

        invalidate();

        return true;
    }

    private void requestChangeImageDataOnLayoutControl(Rect overlappedRect) throws Exception {
        if (editDragNDropBridge != null) {
            if (imageLayerMap != null) {
                SnapsLayoutControl targetSnapsLayoutControl = imageLayerMap.get(overlappedRect);
                if (targetSnapsLayoutControl != null) {
                    if (targetSnapsLayoutControl.imgData != dragImageInfo.getImageData()) {
                        MyPhotoSelectImageData imageDataOfDragView = dragImageInfo.getImageData();
                        if (imageDataOfDragView != null) {
                            MyPhotoSelectImageData newImageData = new MyPhotoSelectImageData();
                            newImageData.weakCopy(imageDataOfDragView);

                            editDragNDropBridge.onStopDragging(targetSnapsLayoutControl, newImageData);
                        }
                    }
                }
            }
        }
    }

    public void stopDragging() {
        if (!isDragging) return;
        try {
            isDragging = false;
            setVisibility(View.GONE);

            if (dragView != null) {
                dragView.setAlpha(1f);

                Rect overlappedRect = findRectOfOverlapView();
                if (overlappedRect != null) {
                    requestChangeImageDataOnLayoutControl(overlappedRect);
                }
                dragImageInfo = null;
            }

            draggingOffsetX = -1;
            draggingOffsetY = -1;

            if (imageControlRectSet != null && !imageControlRectSet.isEmpty()) {
                imageControlRectSet.clear();
            }

            if (draggingView != null) {
                draggingView.releaseBitmap();
                draggingView = null;
            }

            isCreatedBitmap = false;
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private BPoint getDraggingViewOffsetPoint() {
        if (draggingView == null) return null;

        BSize size = draggingView.getDraggingImageBitmapSize();
        if (size != null) {
            int resultX = (int) (draggingOffsetX - (size.getWidth()/2));
            int resultY = (int) (draggingOffsetY - (size.getHeight()/2));

            Dlog.d("getDraggingViewOffsetPoint() x:" + resultX + ", y:" + resultY);

            return new BPoint(resultX, resultY);
        }

        return null;
    }

    private BPoint getReplaceViewOffsetPoint() {
        if (draggingView == null) return null;

        BPoint draggingViewPoint = getDraggingViewOffsetPoint();
        if (draggingViewPoint != null) {
            if (replaceIconBitmapSize != null) {
                return new BPoint((int) (draggingViewPoint.getX() - replaceIconBitmapSize.getWidth()/2), (int) (draggingViewPoint.getY() - replaceIconBitmapSize.getHeight()/2));
            }
        }

        return null;
    }

    private boolean isShowDraggingView() {
        return draggingOffsetX > 0 && draggingOffsetY > 0 && draggingView != null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isShowDraggingView()) {
            Rect overlappedRect = findRectOfOverlapView();
            final boolean isContainRect = overlappedRect != null;
            if (isContainRect) { //다켓이 되는 뷰를 표시해 준다.
                canvas.drawRect(overlappedRect, imageControlFillPaint);
                canvas.drawRect(overlappedRect, imageControlLinePaint);
            }

            BPoint draggingViewOffsetPoint = getDraggingViewOffsetPoint();
            if (draggingViewOffsetPoint != null) { //드래그 하고 있는 뷰를 그린다.
                canvas.drawBitmap(draggingView.getDraggingImageBitmap(), draggingViewOffsetPoint.getX(), draggingViewOffsetPoint.getY(), null);
            }

            if (isContainRect) {
                BPoint replaceViewOffsetPoint = getReplaceViewOffsetPoint();
                if (replaceViewOffsetPoint != null) {
                    canvas.drawBitmap(replaceIconBitmap, replaceViewOffsetPoint.getX(), replaceViewOffsetPoint.getY(), null);
                }
            }
        }
    }

    private synchronized Rect findRectOfOverlapView() {
        if (imageControlRectSet == null || imageControlRectSet.isEmpty()) return null;
        for (Rect rect : imageControlRectSet) {
            if (rect == null) continue;

            if (rect.contains(draggingOffsetX, draggingOffsetY)) {
                return rect;
            }
        }
        return null;
    }

    public boolean isOverlapDragView() {
        return isOverlapDragView.get();
    }

    public Object getOverlapViewCheckSync() {
        return overlapViewCheckSync;
    }

    public static class DraggingView {
        private Bitmap draggingImageBitmap = null;
        private BSize draggingImageBitmapSize = null;

        private DraggingView(Builder builder) {
            this.draggingImageBitmap = builder.draggingImageBitmap;
            this.draggingImageBitmapSize = builder.draggingImageBitmapSize;
        }

        public void releaseBitmap() {
            BitmapUtil.bitmapRecycle(getDraggingImageBitmap());
        }

        public Bitmap getDraggingImageBitmap() {
            return draggingImageBitmap;
        }

        public BSize getDraggingImageBitmapSize() {
            return draggingImageBitmapSize;
        }

        public static class Builder {
            private Bitmap draggingImageBitmap = null;
            private BSize draggingImageBitmapSize = null;

            public Builder setDraggingImageBitmap(Bitmap draggingImageBitmap) {
                this.draggingImageBitmap = draggingImageBitmap;
                return this;
            }

            public Builder setDraggingImageBitmapSize(BSize draggingImageBitmapSize) {
                this.draggingImageBitmapSize = draggingImageBitmapSize;
                return this;
            }

            public DraggingView create() {
                return new DraggingView(this);
            }
        }
    }
}
