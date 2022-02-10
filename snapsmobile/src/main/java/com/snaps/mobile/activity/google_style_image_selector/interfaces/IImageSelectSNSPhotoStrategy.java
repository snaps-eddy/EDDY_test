package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectSNSPhotoAdapter;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public interface IImageSelectSNSPhotoStrategy {

    void initialize(ImageSelectSNSData snsData, IImageSelectLoadPhotosListener listener);

    void suspended(); //중간에 액티비타가 종료 되거나...

    void loadImage(ImageSelectNetworkPhotoAttribute attribute); //이미지 로딩

    int getTitleResId(); //타이틀에 표시할

    void setAdapter(ImageSelectSNSPhotoAdapter adapter);

    void setPhotoFilterInfo(ImageSelectUIPhotoFilter photoFilterInfo);

    String getMapKey(ImageSelectSNSImageData imageData);

    String getObjectId(String url);

    int getSNSTypeCode();
}
