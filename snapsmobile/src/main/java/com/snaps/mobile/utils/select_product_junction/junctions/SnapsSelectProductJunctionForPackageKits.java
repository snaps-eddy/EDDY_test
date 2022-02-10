package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForPackageKits implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        String paperCode = null;
        if (urlData.containsKey(Const_EKEY.WEB_PAPER_CODE))
            paperCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);

        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));
        if (paperCode != null) {
            Config.setPAPER_CODE(paperCode);
        }

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_PACKAGE_KIT)
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setHomeSelectPaperType(paperCode)
                .setHomeSelectKind("").create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);
        activity.startActivity(intent);

        SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
        if (menuManager != null) {
            PopupWebviewActivity popupWebviewActivity = menuManager.getPopupWebViewActivity();
            if (popupWebviewActivity != null && !popupWebviewActivity.isFinished()) {
                popupWebviewActivity.finish();
            }
        }
        return true;
    }
}
