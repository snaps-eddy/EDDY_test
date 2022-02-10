package com.snaps.mobile.component.image_edit_componet;

import com.snaps.common.utils.imageloader.recoders.AdjustableCropInfo;

/**
 * Created by ysjeong on 2017. 6. 7..
 */

public class ImgRectAttribute {
    private float movedX, movedY, width, height, centerX, centerY, scale;

    public void clear() {
        movedX = 0.f;
        movedY = 0.f;
        width = 0.f;
        height = 0.f;
        centerX = 0.f;
        centerY = 0.f;
        scale = 0.f;
    }

    public void set(AdjustableCropInfo.CropImageRect attr) {
        movedX = attr.movedX;
        movedY = attr.movedY;
        width = attr.width;
        height = attr.height;
        centerX = attr.centerX;
        centerY = attr.centerY;
        scale = attr.scaleX;
    }

    public void addMovedX(float movedX) {
        this.movedX += movedX;
    }

    public float getMovedX() {
        return movedX;
    }

    public void setMovedX(float movedX) {
        this.movedX = movedX;
    }

    public float getMovedY() {
        return movedY;
    }

    public void addMovedY(float movedY) {
        this.movedY += movedY;
    }

    public void setMovedY(float movedY) {
        this.movedY = movedY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
