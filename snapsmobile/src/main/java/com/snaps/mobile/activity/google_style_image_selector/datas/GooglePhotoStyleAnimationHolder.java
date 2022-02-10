package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

/**
 * Created by ysjeong on 2017. 1. 11..
 */

public class GooglePhotoStyleAnimationHolder {

    private RecyclerView.ViewHolder viewHolder;
    private Rect viewRect;
    private View parentView;
    private ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType;
    private ISnapsImageSelectConstants.eANIMATION_OBJECT_TYPE animationObjectType;
    private boolean isDummyStartView; //애니메이션을 시작하는 뷰가 더미로 만들어진 뷰다.
    private boolean isDummyTargetView;

    public GooglePhotoStyleAnimationHolder copyInstance() {
        GooglePhotoStyleAnimationHolder copiedHolder = new GooglePhotoStyleAnimationHolder();
        copiedHolder.setViewHolder(getViewHolder());
        if (getViewRect() != null) {
            Rect copyRect = new Rect(getViewRect());
            copiedHolder.setViewRect(copyRect);
        }
        copiedHolder.setHolderType(getHolderType());
        copiedHolder.setAnimationObjectType(getAnimationObjectType());

        return copiedHolder;
    }

    public boolean isDummyStartView() {
        return isDummyStartView;
    }

    public void setDummyStartView(boolean dummyStartView) {
        isDummyStartView = dummyStartView;
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

    public ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE getHolderType() {
        return holderType;
    }

    public void setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType) {
        this.holderType = holderType;
    }

    public View getParentView() {
        return parentView;
    }

    public GalleryCursorRecord.PhonePhotoFragmentItem getItem() {
        if (viewHolder != null && viewHolder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
            ImageSelectAdapterHolders.PhotoFragmentItemHolder holder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) viewHolder;
            return holder.getPhonePhotoItem();
        }
        return null;
    }

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public boolean isSectionHolder() {
        return holderType != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL;//viewHolder != null && viewHolder instanceof ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder;
    }

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public Rect getViewRect() {
        return viewRect;
    }

    public void setViewRect(Rect viewRect) {
        this.viewRect = viewRect;
    }
}
