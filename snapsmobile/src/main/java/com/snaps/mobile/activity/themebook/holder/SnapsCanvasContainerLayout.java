package com.snaps.mobile.activity.themebook.holder;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsPageCanvas;

public class SnapsCanvasContainerLayout extends RelativeLayout {

    private SnapsPageCanvas snapsPageCanvas = null;

    public SnapsCanvasContainerLayout(Context context) {
        super(context);
    }

    public SnapsCanvasContainerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapsCanvasContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SnapsPageCanvas getSnapsPageCanvas() {
        return snapsPageCanvas;
    }

    public void setSnapsPageCanvas(SnapsPageCanvas snapsPageCanvas) {
        if (this.snapsPageCanvas != snapsPageCanvas) {
            removeAllViews();
            this.snapsPageCanvas = snapsPageCanvas;
            addView(this.snapsPageCanvas);
        }

        invalidate();
    }

    public void refreshSnapsPageCanvas() {
        if (snapsPageCanvas == null) return;
        snapsPageCanvas.refresh();
    }
}
