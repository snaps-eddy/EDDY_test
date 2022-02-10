package com.snaps.common.utils.ui;

import android.graphics.Rect;

import java.util.List;

public class ImageEdge {

    private int labelingId;
    private int[] edgePixels;
    private Rect edgeRect;
    private Rect fitImageRect;
    private List<Integer> outlines;

    ImageEdge(int labelingId, int[] edgePixels, Rect edgeRect, List<Integer> outlines, Rect fitImageRect) {
        this.labelingId = labelingId;
        this.edgePixels = edgePixels;
        this.edgeRect = edgeRect;
        this.outlines = outlines;
        this.fitImageRect = fitImageRect;
    }

    public int getLabelingId() {
        return labelingId;
    }

    public int[] getEdgePixels() {
        return edgePixels;
    }

    public Rect getEdgeRect() {
        return edgeRect;
    }

    public List<Integer> getOutlinesOnly() {
        return outlines;
    }

    public Rect getFitImageRect() {
        return fitImageRect;
    }
}
