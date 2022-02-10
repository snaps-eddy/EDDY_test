package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForCalendar implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        // 추가.
        Config.setPROD_CODE(prodKey);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        Config.setPAPER_CODE(urlData.get(Const_EKEY.WEB_PAPER_CODE) == null ? "" : urlData.get(Const_EKEY.WEB_PAPER_CODE));
        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));

        String year = urlData.get(Const_EKEY.WEB_CALENDAR_YEAR_KEY);
        String month = urlData.get(Const_EKEY.WEB_CALENDAR_MONTH_KEY);
        int nYear;
        int nMonth;
        if (year != null && month != null && !year.isEmpty() && !month.isEmpty()) {
            nYear = Integer.parseInt(year);
            nMonth = Integer.parseInt(month);
        } else {
            nYear = 2015;
            nMonth = 10;
        }

        GetTemplateXMLHandler.setStartYear(nYear);
        GetTemplateXMLHandler.setStartMonth(nMonth);

        int selectType = Config.isWoodBlockCalendar() ? Config.SELECT_WOOD_BLOCK_CALENDAR : Config.SELECT_CALENDAR;

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);
        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(selectType)
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setHomeSelectKind("").create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        activity.startActivity(intent);
        return true;
    }
}
