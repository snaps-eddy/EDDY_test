package com.snaps.mobile.utils.sns.googlephoto.interfacies;

/**
 * Created by ysjeong on 2017. 5. 19..
 */

public interface GooglePhotoAPIListener {
    void onPrepare();
    void onGooglePhotoAPIResult(boolean isSuccess, GooglePhotoAPIResult resultObj);
}
