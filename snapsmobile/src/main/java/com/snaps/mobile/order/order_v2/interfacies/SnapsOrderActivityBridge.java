package com.snaps.mobile.order.order_v2.interfacies;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 4. 5..
 */

public interface SnapsOrderActivityBridge {
    ArrayList<MyPhotoSelectImageData> getUploadImageList();
    SnapsOrderAttribute getSnapsOrderAttribute();
    SnapsTemplate getTemplate();

    void requestMakeMainPageThumbnailFile(ISnapsCaptureListener captureListener);

    void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener);

    void onUploadFailedOrgImgWhenSaveToBasket() throws Exception;

    void onDisconnectNetwork();

    Activity getActivity();
}
