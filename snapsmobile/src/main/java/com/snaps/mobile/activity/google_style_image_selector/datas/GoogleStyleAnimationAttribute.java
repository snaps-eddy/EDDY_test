package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;

/**
 * Created by ysjeong on 2017. 1. 20..
 */

public class GoogleStyleAnimationAttribute {
    private Rect startViewRect = null;    //현재 UI에서 각 아이템의 offset 및 크기 정보
    private Rect targetViewRect = null;    //변할 UI에서의 offset 및 크기 정보
    private View startHolderView = null;    //adapter item
    private View targetHolderView = null;    //adapter item
    private ImageView startImageView = null;
    private ImageView targetImageView = null;

    private String startImagePath = "";
    private String targetImagePath = "";

    private int startImageSize = 0; //이미지 로더에서 override할 사이즈 (속도와 화질 측면에서 컨트롤할 필요가 있다.)
    private int targetImageSize = 0;

    private boolean isDummyStartView = false; //현재 UI의 아이템 갯수가 변할 UI의 갯수보다 적어서 더미로 만들어낸 뷰인지(애니메이션 처리에 예외 처리가 필요하다.)
    private boolean isDummyTargetView = false; //반대로..
    private boolean isSelected = false;

    private ISnapsImageSelectConstants.eANIMATION_OBJECT_TYPE animationObjectType;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isDummyStartView() {
        return isDummyStartView;
    }

    public GoogleStyleAnimationAttribute setDummyStartView(boolean dummyStartView) {
        isDummyStartView = dummyStartView;
        return this;
    }

    public boolean isDummyTargetView() {
        return isDummyTargetView;
    }

    public void setDummyTargetView(boolean dummyTargetView) {
        isDummyTargetView = dummyTargetView;
    }

    public ISnapsImageSelectConstants.eANIMATION_OBJECT_TYPE getAnimationObjectType() {
        return animationObjectType;
    }

    public void setAnimationObjectType(ISnapsImageSelectConstants.eANIMATION_OBJECT_TYPE animationObjectType) {
        this.animationObjectType = animationObjectType;
    }

    public int getStartWidth() {
        return startViewRect != null ? startViewRect.width() : 0;
    }

    public int getStartHeight() {
        return startViewRect != null ? startViewRect.height() : 0;
    }

    public int getTargetWidth() {
        return targetViewRect != null ? targetViewRect.width() : 0;
    }

    public int getTargetHeight() {
        return targetViewRect != null ? targetViewRect.height() : 0;
    }

    public Rect getStartViewRect() {
        return startViewRect;
    }

    public void setStartViewRect(Rect startViewRect) {
        this.startViewRect = startViewRect;
    }

    public Rect getTargetViewRect() {
        return targetViewRect;
    }

    public void setTargetViewRect(Rect targetViewRect) {
        this.targetViewRect = targetViewRect;
    }

    public View getStartHolderView() {
        return startHolderView;
    }

    public void setStartHolderView(View startHolderView) {
        this.startHolderView = startHolderView;
    }

    public View getTargetHolderView() {
        return targetHolderView;
    }

    public void setTargetHolderView(View targetHolderView) {
        this.targetHolderView = targetHolderView;
    }

    public ImageView getStartImageView() {
        return startImageView;
    }

    public void setStartImageView(ImageView startImageView) {
        this.startImageView = startImageView;
    }

    public ImageView getTargetImageView() {
        return targetImageView;
    }

    public void setTargetImageView(ImageView targetImageView) {
        this.targetImageView = targetImageView;
    }

    public String getStartImagePath() {
        return startImagePath;
    }

    public void setStartImagePath(String startImagePath) {
        this.startImagePath = startImagePath;
    }

    public String getTargetImagePath() {
        return targetImagePath;
    }

    public void setTargetImagePath(String targetImagePath) {
        this.targetImagePath = targetImagePath;
    }

    public int getStartImageSize() {
        return startImageSize;
    }

    public void setStartImageSize(int startImageSize) {
        this.startImageSize = startImageSize;
    }

    public int getTargetImageSize() {
        return targetImageSize;
    }

    public void setTargetImageSize(int targetImageSize) {
        this.targetImageSize = targetImageSize;
    }
}
