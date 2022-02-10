package com.snaps.mobile.activity.google_style_image_selector.datas;

import android.content.Context;
import android.graphics.Point;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.mobile.activity.photoprint.PhotoPrintProductInfo;

/**
 * Created by ysjeong on 2016. 12. 15..
 */

public class ImageSelectUIPhotoFilter {

    public static final String PRODUCT_DATA = "product_data";

    private Point photoFilterPoint = null;    //인화 불가 체크를 위한..

    public ImageSelectUIPhotoFilter() {
        this.photoFilterPoint = new Point(-1, -1);
    }

    public Point getPhotoFilterPoint() {
        return photoFilterPoint;
    }

    public void initPhotoFilterInfo(Context context, ImageSelectIntentData intentData) {
        if (intentData == null || getPhotoFilterPoint() == null) return;

        int recommendWidth = intentData.getRecommendWidth();
        int recommendHeight = intentData.getRecommendHeight();

        Point photoFilter = getPhotoFilterPoint();

        //전달 받은 권장 해상도
        if (recommendWidth > 0 && recommendHeight > 0) {
            photoFilter.set(recommendWidth, recommendHeight);
        } else {
            if (Config.isSnapsPhotoPrint()) {
                PhotoPrintProductInfo photoPrintProductInfo = (PhotoPrintProductInfo) FileUtil.readInnerFile(context, PRODUCT_DATA);
                if (photoPrintProductInfo != null) {
                    photoFilter.x = (int) photoPrintProductInfo.productLimitWidth;
                    photoFilter.y = (int) photoPrintProductInfo.productLimitHeigth;
                }
            } else if (intentData.getHomeSelectProduct() == Config.SELECT_SINGLE_CHOOSE_TYPE
                    || intentData.getHomeSelectProduct() == Config.SELECT_MULTI_CHOOSE_TYPE) {
                if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(Config.getPROD_CODE())) {
                    photoFilter.x = 960;
                    photoFilter.y = 720;
                } else if (Config.PRODUCT_THEMEBOOK_A6.equalsIgnoreCase(Config.getPROD_CODE())) {
                    photoFilter.x = 720;
                    photoFilter.y = 640;
                }
            }
        }
    }

}
