package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForDiaryBook implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;


        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        SnapsDiaryDataManager.getInstance().setIsWritingDiary(false);

        Setting.set(activity, "themekey", "");

        Config.setPROD_CODE( urlData.get(Const_EKEY.WEB_PRODCODE_KEY) );
        Config.setTMPL_CODE( urlData.get(Const_EKEY.WEB_TEMPLE_KEY) );

        String title = urlData.get(Const_EKEY.WEB_TITLE_KEY);

        try {
            title = URLDecoder.decode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            title = activity.getString(R.string.untitled);
        } catch (NullPointerException e ) {
            title = activity.getString(R.string.untitled);
        }

        String pageCode = urlData.get(Const_EKEY.WEB_PAPER_CODE);
        String startDate = urlData.get(Const_EKEY.WEB_START_DATE_KEY);
        String endDate = urlData.get(Const_EKEY.WEB_END_DATE_KEY);

        Intent intent = new Intent(activity, ImageSelectActivityV2.class);

        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SNAPS_REMOVE_DIARY)
                .setThemeSelectTemplate(Config.getTMPL_CODE())
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setWebTitleKey(title)
                .setWebPaperCode(pageCode)
                .setWebStartDate(startDate)
                .setWebEndDate(endDate)
                .create();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        intent.putExtras(bundle);

        activity.startActivity(intent);

        return true;
    }
}
