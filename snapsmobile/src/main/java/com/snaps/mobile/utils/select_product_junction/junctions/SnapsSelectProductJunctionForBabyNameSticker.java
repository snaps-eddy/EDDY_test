package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.name_sticker.NameStickerWriteActivity;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForBabyNameSticker implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;
        int currentHome = Config.SELECT_STICKER;
        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        Config.setFRAME_ID(urlData.get(Const_EKEY.WEB_FRAME_KEY));
        Config.setPAPER_CODE(urlData.get("paperCode") == null ? "" : urlData.get("paperCode"));
        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));
        Intent intent = new Intent(activity, NameStickerWriteActivity.class);
        activity.startActivity(intent);
        return true;
    }
}
