package com.snaps.mobile.activity.google_style_image_selector.datas;


import com.google.android.gms.common.images.Size;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysjeong on 2017. 1. 18..
 */

public class GooglePhotoStyleThumbnailSizeInfo {

    private Map<String, Size> mapThumbnailSizes = null;

    public GooglePhotoStyleThumbnailSizeInfo() {
        mapThumbnailSizes = new HashMap<>();
    }

    public Size getAnimationHolderDefaultSizeByUIDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth,
                                                       ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType, int baseWidth, int baseHeight) {
        if (uiDepth == null || holderType == null || mapThumbnailSizes == null) return null;

        String key = uiDepth.toString() + "_" + holderType.toString();

        if (mapThumbnailSizes.containsKey(key)) {
            return mapThumbnailSizes.get(key);
        } else {
            Size sizeInfo = new Size(baseWidth, baseHeight);
            mapThumbnailSizes.put(key, sizeInfo);
            return sizeInfo;
        }
    }
}
