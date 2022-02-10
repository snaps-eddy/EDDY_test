package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
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

public class SnapsSelectProductJunctionForFrameProduct implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        // 추가.
        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        if(urlData.containsKey(Const_EKEY.WEB_FRAME_TYPE_KEY)) {
            Config.setFRAME_TYPE(urlData.get(Const_EKEY.WEB_FRAME_TYPE_KEY));
        } else if(urlData.containsKey("frameType")) {
            Config.setFRAME_TYPE(urlData.get("frameType"));
        } else {
            Config.setFRAME_TYPE("");
        }
        Config.setFRAME_ID(urlData.containsKey("frameid") ? urlData.get("frameid") : "");
        Config.setBACK_TYPE(urlData.containsKey("backType") ? urlData.get("backType") : "");
        // 행잉액자때문에 추가
        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));
        Config.setPAPER_CODE(urlData.get("paperCode") == null ? "" : urlData.get("paperCode"));

        String cnt = urlData.containsKey("usePicCnts") ? urlData.get("usePicCnts") : "1";

        if (Integer.parseInt(cnt) > 0) {
            Intent intent = new Intent(activity, ImageSelectActivityV2.class);
            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setHomeSelectProduct(Config.SELECT_FRAME)
                    .setHomeSelectProductCode(Config.getPROD_CODE())
                    .setHomeSelectKind("").create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            intent.putExtras(bundle);

            activity.startActivity(intent);
        } else {
            //템플릿 파싱을 편집화면에서 해야 되므로 클리어.
            SnapsTemplateManager.getInstance().cleanInstance();
            Intent intent = new Intent(activity, SnapsEditActivity.class);
            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
            activity.startActivity(intent);
        }

        return true;
    }
}
