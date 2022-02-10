package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;

public class SquareRelativeLayout extends RelativeLayout {

    int arrIdxOnAdapter = -1;

    private boolean isSquareShape = false;

    private ImageSelectAdapterHolders.PhotoFragmentItemHolder holder = null;

    public SquareRelativeLayout(Context context) {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getArrIdxOnAdapter() {
        return arrIdxOnAdapter;
    }

    public void setArrIdxOnAdapter(int arrIdxOnAdapter) {
        this.arrIdxOnAdapter = arrIdxOnAdapter;
    }

    public void setSquareShape(boolean squareShape) {
        isSquareShape = squareShape;
    }

    public ImageSelectAdapterHolders.PhotoFragmentItemHolder getHolder() {
        return holder;
    }

    public SquareRelativeLayout setHolder(ImageSelectAdapterHolders.PhotoFragmentItemHolder holder) {
        this.holder = holder;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isSquareShape) {
            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }
    }
}

