package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectAlbumHandler;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectLoadPhotosListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectSNSPhotoStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectSNSPhotoAdapter;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public abstract class ImageSelectSNSPhotoBase implements IImageSelectSNSPhotoStrategy, IImageSelectAlbumHandler {

    protected ImageSelectSNSPhotoAdapter adapter = null;

    protected IImageSelectLoadPhotosListener listener = null;

    protected ImageSelectUIPhotoFilter photoFilterInfo = null;

    protected ImageSelectNetworkPhotoAttribute attribute;

    protected int loadedCount = 0;

    protected boolean isFirstLoding = false;

    protected boolean isSuspended = false;

    protected ImageSelectActivityV2 activity = null;

    public ImageSelectSNSPhotoBase(ImageSelectActivityV2 activity) {
        this.activity = activity;
    }

    @Override
    public void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener) {
        this.listener = listener;
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {}

    @Override
    public int getTitleResId() {
        return R.string.facebook_photo;
    }

    @Override
    public void suspended() {
        isSuspended = true;
    }

    @Override
    public void setAdapter(ImageSelectSNSPhotoAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void setPhotoFilterInfo(ImageSelectUIPhotoFilter photoFilterInfo) {
        this.photoFilterInfo = photoFilterInfo;
    }

    @Override
    public IAlbumData getCurrentAlbumCursor() {
        if (attribute != null) {
            return attribute.getAlbumCursorInfo();
        }
        return null;
    }

    protected boolean isAliveNetwork() {
        if (activity == null) return false;
        CNetStatus netStatus = CNetStatus.getInstance();
        return netStatus.isAliveNetwork(activity);
    }
}
