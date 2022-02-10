package com.snaps.mobile.edit_activity_tools.adapter;

import android.graphics.Rect;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;

/**
 * Created by ysjeong on 2017. 7. 17..
 */

public class CardShapeThumbnailChildView {

    public enum eCARD_SHAPE_THUMBNAIL_PAGE_TYPE {
        CARD_SHAPE_THUMBNAIL_TYPE_PAGE_LEFT,
        CARD_SHAPE_THUMBNAIL_TYPE_PAGE_RIGHT
    }

    private RelativeLayout rootLayout;
    private RelativeLayout imgLayout;
    private RelativeLayout canvasParentLy;
    private SnapsPageCanvas canvas;
    private ImageView outline;
    private ImageView warnining;
    private TextView introindex;
    private TextView leftIndex;
    private TextView rightIndex;

    private ProgressBar progressBar;

    private Rect thumbnailRect;
    private SnapsPage page;

    public RelativeLayout getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(RelativeLayout rootLayout) {
        this.rootLayout = rootLayout;
    }

    public RelativeLayout getImgLayout() {
        return imgLayout;
    }

    public void setImgLayout(RelativeLayout imgLayout) {
        this.imgLayout = imgLayout;
    }

    public RelativeLayout getCanvasParentLy() {
        return canvasParentLy;
    }

    public void setCanvasParentLy(RelativeLayout canvasParentLy) {
        this.canvasParentLy = canvasParentLy;
    }

    public SnapsPageCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(SnapsPageCanvas canvas) {
        this.canvas = canvas;
    }

    public ImageView getOutline() {
        return outline;
    }

    public void setOutline(ImageView outline) {
        this.outline = outline;
    }

    public ImageView getWarnining() {
        return warnining;
    }

    public void setWarnining(ImageView warnining) {
        this.warnining = warnining;
    }

    public TextView getIntroindex() {
        return introindex;
    }

    public void setIntroindex(TextView introindex) {
        this.introindex = introindex;
    }

    public TextView getLeftIndex() {
        return leftIndex;
    }

    public void setLeftIndex(TextView leftIndex) {
        this.leftIndex = leftIndex;
    }

    public TextView getRightIndex() {
        return rightIndex;
    }

    public void setRightIndex(TextView rightIndex) {
        this.rightIndex = rightIndex;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Rect getThumbnailRect() {
        return thumbnailRect;
    }

    public void setThumbnailRect(Rect thumbnailRect) {
        this.thumbnailRect = thumbnailRect;
    }

    public SnapsPage getPage() {
        return page;
    }

    public void setPage(SnapsPage page) {
        this.page = page;
    }
}
