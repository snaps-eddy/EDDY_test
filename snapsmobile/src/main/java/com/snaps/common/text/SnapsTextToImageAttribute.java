package com.snaps.common.text;

import android.graphics.Paint;

import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.control.TextFormat;

/**
 * Created by ysjeong on 2018. 3. 12..
 */

public class SnapsTextToImageAttribute {
    private SnapsTextControl snapsTextControl = null;
    private String controlType;
    private Paint.Align textAlign = Paint.Align.CENTER;
    private boolean isSpineText = false;
    private boolean isThumbnail = false;
    private float thumbnailRatioX = 1.f, thumbnailRatioY = 1.f;
    private int mImageScale;

    private SnapsTextToImageAttribute() {
    }

    public static SnapsTextToImageAttribute createAttribute(SnapsTextControl snapsTextControl) {
        SnapsTextToImageAttribute attribute = new SnapsTextToImageAttribute();
        attribute.setSnapsTextControl(snapsTextControl);
        attribute.setIsSpineTextWithTextControl(snapsTextControl);
        return attribute;
    }

    public SnapsTextControl getSnapsTextControl() {
        return snapsTextControl;
    }

    public void setSnapsTextControl(SnapsTextControl snapsTextControl) {
        this.snapsTextControl = snapsTextControl;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public Paint.Align getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(Paint.Align textAlign) {
        this.textAlign = textAlign;
    }

    public boolean isSpineText() {
        return isSpineText;
    }

    public void setIsSpineTextWithTextControl(SnapsTextControl snapsTextControl) {
        if (snapsTextControl == null || snapsTextControl.format == null) return;
        TextFormat textFormat = snapsTextControl.format;
        isSpineText = textFormat.verticalView != null && textFormat.verticalView.equals("true");
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }

    public float getThumbnailRatioX() {
        return thumbnailRatioX;
    }

    public void setThumbnailRatioX(float thumbnailRatioX) {
        this.thumbnailRatioX = thumbnailRatioX;
    }

    public float getThumbnailRatioY() {
        return thumbnailRatioY;
    }

    public void setThumbnailRatioY(float thumbnailRatioY) {
        this.thumbnailRatioY = thumbnailRatioY;
    }

    public void setImageScale(int imageScale) {
        this.mImageScale = imageScale;
    }

    public int getImageScale() {
        return mImageScale;
    }
}
