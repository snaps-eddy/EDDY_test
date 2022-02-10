package com.snaps.common.utils.ui;

/**
 * Created by ifunbae on 2017. 1. 9..
 */

public class BSize {
    float width = 0;
    float height = 0;

    public BSize() {
        width = 0;
        height = 0;
    }

    public BSize(float width, float height){
        this.width = width;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void set(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void set(BSize size) {
        if (size == null) return;
        this.width = size.width;
        this.height = size.height;
    }
}
