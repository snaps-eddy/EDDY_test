package com.snaps.common.utils.ui;

import android.graphics.Point;
import android.graphics.Rect;

public class HelperInfo {

    private Point leftMatchPoint;
    private Point rightMatchPoint;
    private Point lowestCenterPoint;

    public HelperInfo(Point leftMatchPoint, Point rightMatchPoint, Point lowestCenterPoint) {
        this.leftMatchPoint = leftMatchPoint;
        this.rightMatchPoint = rightMatchPoint;
        this.lowestCenterPoint = lowestCenterPoint;
    }

    public Point getLeftMatchPoint() {
        return leftMatchPoint;
    }

    public Point getRightMatchPoint() {
        return rightMatchPoint;
    }

    public Point getLowestCenterPoint() {
        return lowestCenterPoint;
    }

    public Rect getStick(int stickWidth, int stickHeight) {
        return new Rect(lowestCenterPoint.x - stickWidth / 2, lowestCenterPoint.y + stickHeight, lowestCenterPoint.x + stickWidth / 2, lowestCenterPoint.y);
    }

    public int getHelperWidth() {
        return rightMatchPoint.x - leftMatchPoint.x;
    }
}
