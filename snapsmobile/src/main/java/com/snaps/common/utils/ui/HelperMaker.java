package com.snaps.common.utils.ui;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * 아크릴 등신대 헬퍼 생성 및 유틸
 */
public class HelperMaker {

    private HelperInfo lastHelperInfo;

    private static final int PIXEL_RANGE_FIND_MATCH_POINT = 5;

    public HelperInfo makeHelper(ImageEdge imageEdge, int bitmapWidth, int helperMinWidth) {
        List<Integer> outlines = imageEdge.getOutlinesOnly();
        Rect fitImageRect = imageEdge.getFitImageRect();

        int lowestCenterIndex = getLowestCenterPixelIndex(imageEdge, bitmapWidth);
        int lowestCenterX = lowestCenterIndex % bitmapWidth;
        int lowestCenterY = lowestCenterIndex / bitmapWidth;

        int leftPixelIndex = 0;
        int rightPixelIndex = 0;

        int leftPixelX = bitmapWidth;
        int rightPixelX = 0;

        for (int pixelIndex : outlines) {
            int x = pixelIndex % bitmapWidth;
            int y = pixelIndex / bitmapWidth;

            if (lowestCenterY - PIXEL_RANGE_FIND_MATCH_POINT < y && y <= lowestCenterY) {
                if (x < leftPixelX) {
                    leftPixelX = x;
                    leftPixelIndex = pixelIndex;
                }

                if (x > rightPixelX) {
                    rightPixelX = x;
                    rightPixelIndex = pixelIndex;
                }
            }
        }

        if (rightPixelX - leftPixelX >= helperMinWidth) {
            return makeHelperExtension(leftPixelIndex, rightPixelIndex, bitmapWidth, lowestCenterY, fitImageRect);

        } else {
            return makeHelperNormal(lowestCenterX, lowestCenterY, fitImageRect, outlines, bitmapWidth, helperMinWidth);
        }
    }

    public HelperInfo moveHelper(ImageEdge imageEdge, int centerX, int centerY, int bitmapWidth, int helperSize) {
        Rect fitImageRect = imageEdge.getFitImageRect();
        List<Integer> outlines = imageEdge.getOutlinesOnly();
        return makeHelperNormal(centerX, centerY, fitImageRect, outlines, bitmapWidth, helperSize);
    }

    public HelperInfo restoreHelper(ImageEdge imageEdge, int leftX, int centerY, int bitmapWidth, int helperSize) {
        List<Integer> onlyOutline = imageEdge.getOutlinesOnly();
        int rightX = leftX + helperSize;
        int centerX = leftX + Math.round((float) helperSize / 2);

        int matchedLeftY = 0;
        int matchedRightY = 0;

        for (int pixelIndex : onlyOutline) {
            int x = pixelIndex % bitmapWidth;
            int y = pixelIndex / bitmapWidth;

            if (leftX == x && y > matchedLeftY) {
                matchedLeftY = y;
            }

            if (rightX == x && y > matchedRightY) {
                matchedRightY = y;
            }
        }
        lastHelperInfo = new HelperInfo(new Point(leftX, matchedLeftY), new Point(rightX, matchedRightY), new Point(centerX, centerY));
        return lastHelperInfo;
    }

    private int getLowestCenterPixelIndex(ImageEdge imageEdge, int bitmapWidth) {
        List<Integer> onlyOutline = imageEdge.getOutlinesOnly();
        Rect fitImageRect = imageEdge.getEdgeRect();

        ArrayList<Integer> lowPixels = new ArrayList<>();
        for (int pixelIndex : onlyOutline) {
            int y = pixelIndex / bitmapWidth;
            if (y == fitImageRect.bottom) {
                lowPixels.add(pixelIndex);
            }
        }
        return lowPixels.get(Math.abs(lowPixels.size() / 2));
    }

    private HelperInfo makeHelperExtension(int leftPixelIndex, int rightPixelIndex, int bitmapWidth, int lowestCenterY, Rect fitImageRect) {
        int matchLeftY = leftPixelIndex / bitmapWidth;
        int matchRightY = rightPixelIndex / bitmapWidth;

        int matchLeftX = leftPixelIndex % bitmapWidth;
        int matchRightX = rightPixelIndex % bitmapWidth;

        int correctLeft = Math.max(matchLeftX, fitImageRect.left);
        int correctRight = Math.min(fitImageRect.right, matchRightX);

        int lowestCenterX = Math.round((correctLeft + correctRight) / 2.0f);

        lastHelperInfo = new HelperInfo(new Point(correctLeft, matchRightY), new Point(correctRight, matchLeftY), new Point(lowestCenterX, lowestCenterY));
        return lastHelperInfo;
    }

    private HelperInfo makeHelperNormal(int lowestCenterX, int lowestCenterY, Rect fitImageRect, List<Integer> onlyOutline, int bitmapWidth, int helperWidth) {

        int leftStartX = lowestCenterX - helperWidth / 2;
        int rightStartX = lowestCenterX + helperWidth / 2;

        if (leftStartX < fitImageRect.left) {
            leftStartX = fitImageRect.left;
            rightStartX = leftStartX + helperWidth;
            lowestCenterX = leftStartX + (rightStartX - leftStartX + 1) / 2;


        } else if (rightStartX > fitImageRect.right) {
            rightStartX = fitImageRect.right;
            leftStartX = rightStartX - helperWidth;
            lowestCenterX = leftStartX + (rightStartX - leftStartX + 1) / 2;
        }

        int matchedLeftY = 0;
        int matchedRightY = 0;

        for (int pixelIndex : onlyOutline) {
            int x = pixelIndex % bitmapWidth;
            int y = pixelIndex / bitmapWidth;

            if (leftStartX == x && y > matchedLeftY) {
                matchedLeftY = y;
            }

            if (rightStartX == x && y > matchedRightY) {
                matchedRightY = y;
            }
        }

        lastHelperInfo = new HelperInfo(new Point(leftStartX, matchedLeftY), new Point(rightStartX, matchedRightY), new Point(lowestCenterX, lowestCenterY));
        return lastHelperInfo;
    }

}
