package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.performs.BaseImageSelectPerformer;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForGiftProducts implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        Config.setPROD_CODE(prodKey);
//        Config.setPROD_CODE("00802700020014");
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
//        Config.setTMPL_CODE("045021013577");
        Config.setFRAME_ID(urlData.get(Const_EKEY.WEB_FRAME_KEY));
//        Config.setFRAME_ID("045014000228");

        Config.setNOTE_PAPER_CODE(urlData.get("papertype") == null ? "" : urlData.get("papertype"));
        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));
        Config.setPAPER_CODE(urlData.get(Const_EKEY.WEB_PAPER_CODE)); //FIXME 왜 디자인노트만 papertype으로 넘어오는 지 모르겠음.
        if (Config.getPAPER_CODE() == null || Config.getPAPER_CODE().length() < 1)
            Config.setPAPER_CODE(Config.getNOTE_PAPER_CODE());
        Intent intent = null;
//        if(Const_PRODUCT.isDesignNoteProduct()) {
//            intent = new Intent(activity, SnapsEditActivity.class);
//            intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
//        } else {
            intent = new Intent(activity, ImageSelectActivityV2.class);
            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setHomeSelectProduct(Config.SELECT_FRAME)
                    .setHomeSelectProductCode(Config.getPROD_CODE())
                    .setHomeSelectKind("").create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.FRAME.ordinal());
            intent.putExtras(bundle);
//        }
//        Intent intent = new Intent(activity, ImageSelectActivityV2.class);


        activity.startActivity(intent);

        return true;
    }
}
