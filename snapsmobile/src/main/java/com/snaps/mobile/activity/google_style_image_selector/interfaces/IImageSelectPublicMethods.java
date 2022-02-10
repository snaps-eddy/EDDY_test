package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;

/**
 * Created by ysjeong on 2016. 12. 7..
 */

public interface IImageSelectPublicMethods {
    enum ePHOTO_LIST_ERR_TYPE {
        NONE,
        TEMPLATE_DOWNLOAD_ERROR,
        PHOTO_LIST_EMPTY,
        PHOTO_LIST_NETWORK_ERR
    }

    void onClickedTrayAllView(); //트레이 전체 보기 버튼을 클릭했을 때
    void onClickedTrayAddBtn(); //트레이 Plus 버튼을 클릭 했을 때
    void updateTitle(int id);
    String getTitleText();
    void showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType);
    void setMaxImageCount();
    void setMaxImageCount(int count);

    void onTemplateDownloadErrorOccur(ePHOTO_LIST_ERR_TYPE errType);

    void onChangedRecyclerViewScroll();

    void putSelectedImageData(String key, MyPhotoSelectImageData imgData);

    void onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE controlType, String mapKey);

    void removeSelectedImageData(String key);

    void registerListUpdateListener(IImageSelectListUpdateListener listUpdateListener);

    void unRegisterListUpdateListener(IImageSelectListUpdateListener listUpdateListener);

    int getHomeSelectProdKind();

    boolean isLandScapeMode();
    boolean isAddableImage();
    boolean isSingleChooseType();
    boolean isMultiChooseType();

    ImageSelectUIProcessor getUIProcessor();

    ImageSelectIntentData getIntentData();

    ImageSelectSNSData getSNSData();

    ImageSelectUIPhotoFilter getPhotoFilterInfo();
}
