package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImagePopup;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandlerForPhotoPrintUploadImageFailed extends SnapsBasePushHandler {
    private static final String TAG = SnapsPushHandlerForPhotoPrintUploadImageFailed.class.getSimpleName();
    public SnapsPushHandlerForPhotoPrintUploadImageFailed(Activity activity, SnapsPushHandleData pushHandleData) {
        super(activity, pushHandleData);
    }

    @Override
    public boolean performPushDataHandle() {
        try {
            return SnapsUploadFailedImagePopup.showOrgImgUploadFailPopupIfGetUploadFailIntent(getActivity(), getPushHandleData().getIntent(), getPushHandleData().getHomeUIHandler());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }
}
