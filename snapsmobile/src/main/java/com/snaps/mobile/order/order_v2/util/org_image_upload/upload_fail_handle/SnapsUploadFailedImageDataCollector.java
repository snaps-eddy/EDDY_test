package com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle;

import androidx.annotation.NonNull;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImageData;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;

import java.util.HashMap;

import errorhandle.SnapsAssert;

/**
 * Created by ysjeong on 2017. 4. 24..
 */

public class SnapsUploadFailedImageDataCollector {
    private static final String TAG = SnapsUploadFailedImageDataCollector.class.getSimpleName();
    private static volatile SnapsUploadFailedImageDataCollector gInstance = null;

    /**
     * 굳이, 맵 형태로 실패 데이터를 관리하는 이유는 사진인화의 경우, 업로딩을 백그라운드에서 진행하고 있기 때문에
     * 사진이 많을 경우 다른 상품과 동시에 업로딩이 진행 될 가능성도 있기 때문이다.
     */
    private HashMap<String, SnapsUploadFailedImageData> uploadFailedImageDataMap = null;

    private boolean isShowingUploadFailPopup = false;

    private SnapsUploadFailedImageDataCollector() {}

    public static void createInstance() {
        if (gInstance ==  null) {
            synchronized (SnapsOrderManager.class) {
                if (gInstance ==  null) {
                    gInstance = new SnapsUploadFailedImageDataCollector();
                }
            }
        }
    }

    public static SnapsUploadFailedImageDataCollector getInstance() {
        if(gInstance ==  null)
            createInstance();
        return gInstance;
    }

    public static void finalizeInstance() {
        try {
            if (gInstance ==  null) return;

            getUploadFailedImageDataMap().clear();

            gInstance = null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 업로딩을 시작할 때, 반드시 clearHistory를 호출해 줄 것.
     */
    public static void clearHistory(String projCode) {
        try {
            SnapsUploadFailedImageData currentProjectData = getUploadFailedImageData(projCode);
            if (currentProjectData != null) currentProjectData.clear();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showUploadFailedOrgImageListPopup(@NonNull SnapsUploadFailedImagePopupAttribute attribute, @NonNull SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener popupListener) {
        try {
            SnapsUploadFailedImagePopup.showUploadFailedImageList(attribute, popupListener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(attribute.getActivity(), e);
        }
    }

    /**
     * 업로드 실패한 데이터를 수집한다.
     */
    public static void addUploadFailedImageData(String projCode, MyPhotoSelectImageData imageData) {
        try {
            SnapsUploadFailedImageData failedImageData = SnapsUploadFailedImageDataCollector.getUploadFailedImageData(projCode);
            if (failedImageData != null) {
                if (imageData != null) {
                    imageData.isUploadFailedOrgImage = true;
                    failedImageData.addUploadFailedImageData(imageData);
                    Dlog.d("addUploadFailedImageData() collected upload failed org image path:" + imageData.PATH);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static boolean isExistFailedImageData(String projCode) {
        try {
            if (!Config.isValidProjCodeWithStringCode(projCode)) return false;
            SnapsUploadFailedImageData failedImageData = SnapsUploadFailedImageDataCollector.getUploadFailedImageData(projCode);
            return failedImageData != null && failedImageData.getUploadFailedImageList() != null && failedImageData.getUploadFailedImageList().size() > 0;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    public static int getFailedImageDataCount(String projCode) {
        try {
            SnapsUploadFailedImageData failedImageData = SnapsUploadFailedImageDataCollector.getUploadFailedImageData(projCode);
            return failedImageData != null && failedImageData.getUploadFailedImageList() != null ? failedImageData.getUploadFailedImageList().size() : 0;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    /**
     * 업로드 실패한 이미지 데이터를 반환한다.
     */
    public static SnapsUploadFailedImageData getUploadFailedImageData(String projCode) throws Exception {
        if (StringUtil.isEmpty(projCode)) return null;

        if (!getUploadFailedImageDataMap().containsKey(projCode)) {
            getUploadFailedImageDataMap().put(projCode, SnapsUploadFailedImageData.newInstance());
        }

        return getUploadFailedImageDataMap().get(projCode);
    }

    private static HashMap<String, SnapsUploadFailedImageData> getUploadFailedImageDataMap() {
        SnapsUploadFailedImageDataCollector collector = getInstance();
        if (collector.uploadFailedImageDataMap == null)
            collector.uploadFailedImageDataMap = new HashMap<>();
        return collector.uploadFailedImageDataMap;
    }

    public static boolean isShowingUploadFailPopup() {
        return getInstance().isShowingUploadFailPopup;
    }

    public static void setShowingUploadFailPopup(boolean showingUploadFailPopup) {
        getInstance().isShowingUploadFailPopup = showingUploadFailPopup;
    }
}
