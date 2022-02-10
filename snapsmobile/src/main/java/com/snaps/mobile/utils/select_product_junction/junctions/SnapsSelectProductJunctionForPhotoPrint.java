package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.photoprint.PhotoPrintProductInfo;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForPhotoPrint implements ISnapsProductLauncher {

    private final String PRODUCT_DATA = "product_data";

    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        if (attribute.getPhotoPrintDataList() == null) {
            attribute.setPhotoPrintDataList(PhotoPrintProductInfo.getPhotoPrintTemplate(activity));
        }

        ArrayList<PhotoPrintProductInfo> photoPrintProductInfos = attribute.getPhotoPrintDataList();
        if (photoPrintProductInfos != null) {
            for (PhotoPrintProductInfo item : photoPrintProductInfos) {
                if (prodKey.equals(item.productCode)) {
                    Config.setPROD_CODE(Config.PRODUCT_PHOTOPRINT);
                    FileUtil.saveInnerFile(activity, item, PRODUCT_DATA);
                    break;
                }
            }
        }

        Config.setPROD_CODE(Config.PRODUCT_PHOTOPRINT);

        String paperCode = "";
        if (urlData.containsKey(Const_EKEY.WEB_PAPER_CODE))
            paperCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);

        if (paperCode != null) {
            Config.setPAPER_CODE(paperCode);
        }

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_PHOTO_PRINT)
                .setHomeSelectProductCode(prodKey)
                .setHomeSelectPaperType(paperCode).create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        activity.startActivity(intent);
        return true;
    }
}
