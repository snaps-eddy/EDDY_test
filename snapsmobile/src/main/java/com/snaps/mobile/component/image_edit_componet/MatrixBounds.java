package com.snaps.mobile.component.image_edit_componet;

import android.graphics.PointF;

/**
 * Created by ysjeong on 2017. 6. 7..
 */

public class MatrixBounds {
    public PointF LT = new PointF();
    public PointF RT = new PointF();
    public PointF RB = new PointF();
    public PointF LB = new PointF();

    public MatrixBounds() {
    }

    public MatrixBounds(MatrixBounds bounds) {
        set(bounds.LT.x, bounds.LT.y, bounds.RT.x, bounds.RT.y,
                bounds.RB.x, bounds.RB.y, bounds.LB.x, bounds.LB.y);
    }

    public MatrixBounds(float ltX, float ltY, float rtX, float rtY,
                        float rbX, float rbY, float lbX, float lbY) {
        set(ltX, ltY, rtX, rtY, rbX, rbY, lbX, lbY);
    }

    public void set(float ltX, float ltY, float rtX, float rtY, float rbX,
                    float rbY, float lbX, float lbY) {
        LT.x = ltX;
        LT.y = ltY;
        RT.x = rtX;
        RT.y = rtY;
        RB.x = rbX;
        RB.y = rbY;
        LB.x = lbX;
        LB.y = lbY;
    }

    // public float[] getCenter(float angle) {
    // return new float[]{ getUserSelectWidth(angle)/2, getHeight(angle)/2 };
    // }
    public float[] getCenter() {
        return new float[]{((getRight() - getLeft()) / 2),
                ((getBottom() - getTop()) / 2)};
    }

    public float getWidth() {

        // float width = RT.x - LT.x;

        // return (float) (width / Math.cos(angle));
        return getRight() - getLeft();
    }

    public float getHeight() {

        // float height = RB.y - RT.y;
        //
        // return (float) (height / Math.cos(angle));

        return getBottom() - getTop();
    }

    public float getLeft() {
        return LB.x;
    }

    public float getTop() {
        return LT.y;
    }

    public float getRight() {
        return RT.x;
    }

    public float getBottom() {
        return RB.y;
    }
}
