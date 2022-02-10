package com.snaps.common.spc.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.snaps.common.structure.control.SnapsControl;
import com.snaps.mobile.interfaces.ISnapsControl;

public class SnapsMovableImageView extends androidx.appcompat.widget.AppCompatImageView implements ISnapsControl {
    private static final String TAG = SnapsMovableImageView.class.getSimpleName();
    SnapsControl snapsControl = null;

    public SnapsMovableImageView(Context context, SnapsControl layout) {
        super(context);

        snapsControl = layout;

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                new RelativeLayout.LayoutParams(layout.getIntWidth(), layout.getIntHeight()));
        setLayoutParams(layoutParams);

        setX(layout.getIntX());
        setY(layout.getIntY());

        setBackgroundColor(Color.TRANSPARENT);
    }

    public SnapsMovableImageView(Context context, AttributeSet attribute) {
        super(context, attribute);
    }

    @Override
    public SnapsControl getSnapsControl() {
        return snapsControl;
    }

    @Override
    public void setSnapsControl(SnapsControl layoutControl) {
        this.snapsControl = layoutControl;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        snapsControl.setX(Integer.toString((int)x));
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        snapsControl.setY(Integer.toString((int)y));
    }

    public void setWidth(float width) {
        getLayoutParams().width = (int)width;
        snapsControl.width = String.valueOf(width);
    }

    public void setHeight(float height) {
        getLayoutParams().height = (int)height;
        snapsControl.height = String.valueOf(height);
    }
}