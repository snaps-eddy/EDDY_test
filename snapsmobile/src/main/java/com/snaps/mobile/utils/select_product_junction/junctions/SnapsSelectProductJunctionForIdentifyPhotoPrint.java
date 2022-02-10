package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForIdentifyPhotoPrint implements ISnapsProductLauncher {

    private final String PRODUCT_DATA = "product_data";

    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        String prodKey = attribute.getProdKey();
        if (StringUtil.isEmpty(prodKey)) prodKey = Config.PRODUCT_IDENTIFY_PHOTO;
        Config.setPROD_CODE(prodKey);

        Config.setTMPL_CODE( urlData.get(Const_EKEY.WEB_TEMPLE_KEY) );

        String paperCode = null;
        if (urlData.containsKey(Const_EKEY.WEB_PAPER_CODE))
            paperCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);
        if (paperCode != null) {
            Config.setPAPER_CODE(paperCode);
        }

        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SNAPS_IDENTIFY_PHOTO)
                .setHomeSelectProductCode(prodKey).create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        activity.startActivity(intent);
        return true;
    }
}
