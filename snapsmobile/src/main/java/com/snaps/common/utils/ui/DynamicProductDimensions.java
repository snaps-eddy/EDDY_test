package com.snaps.common.utils.ui;

public class DynamicProductDimensions {

    private int width;
    private int height;
    private float postScaleFactor;

    DynamicProductDimensions(int width, int height, float postScaleFactor) {
        this.width = width;
        this.height = height;
        this.postScaleFactor = postScaleFactor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getPostScaleFactor() {
        return postScaleFactor;
    }

}
