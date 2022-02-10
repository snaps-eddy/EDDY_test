package com.snaps.mobile.activity.google_style_image_selector.interfaces;

/**
 * Created by ysjeong on 2016. 12. 7..
 */

public interface IImageSelectLoadPhotosListener {

    enum eIMAGE_LOAD_RESULT_TYPE {
        NETWORK_ERROR,
        EMPTY,
        NO_MORE,
        REQUEST_MORE_LOAD,
        FIRST_LOAD_COMPLATED,
        MORE_LOAD_COMPLATE
    }

    void onLoadPhotoPreprare();
    void onFinishedLoadPhoto(eIMAGE_LOAD_RESULT_TYPE resultType);
}
