package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForDIYSticker implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
//        if (activity == null || attribute == null) return false;
//
//        String prodKey = attribute.getProdKey();
//        HashMap<String, String> urlData = attribute.getUrlData();
//        if (urlData == null) return false;
//        int currentHome = Config.SELECT_STICKER;
//        Config.setPROD_CODE(prodKey);
//        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
//        Config.setFRAME_ID(urlData.get(Const_EKEY.WEB_FRAME_KEY));
//        Config.setPAPER_CODE(urlData.get("paperCode") == null ? "" : urlData.get("paperCode"));
//        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));
//        Intent intent = new Intent(activity, SnapsEditActivity.class);
//        intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.STICKER.ordinal());
//        activity.startActivity(intent);
        performMakeProduct(activity,attribute);
        return true;
    }

    private boolean performMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));

        String paperCode = null;
        if (urlData.containsKey(Const_EKEY.WEB_PAPER_CODE))
            paperCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);
        if (paperCode != null) {
            Config.setPAPER_CODE(paperCode);
        }
        Config.setFRAME_TYPE(urlData.containsKey(Const_EKEY.WEB_FRAME_TYPE_KEY) ? urlData.get(Const_EKEY.WEB_FRAME_TYPE_KEY) : "");

        String count = urlData.get("projectCount");
        Config.setCARD_QUANTITY(TextUtils.isEmpty(count) ? "1" : count);

        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));

        Config.setPROJ_NAME("");

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_DIY_STICKER)
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setHomeSelectKind("").create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.DIY_STICKER.ordinal());
        intent.putExtras(bundle);

        activity.startActivity(intent);
        return true;
    }
}
