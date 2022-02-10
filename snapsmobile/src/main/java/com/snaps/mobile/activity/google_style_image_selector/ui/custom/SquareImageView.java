package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

    private boolean isSquareShape = false;
    private Context context;

    public SquareImageView(Context context) {
        super(context);
        this.context = context;
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setSquareShape(boolean squareShape) {
        isSquareShape = squareShape;
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

