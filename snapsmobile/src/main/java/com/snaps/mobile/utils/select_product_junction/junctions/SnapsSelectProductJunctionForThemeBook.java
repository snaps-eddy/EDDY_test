package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.themebook.ThemeTitleActivity;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionForThemeBook implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String prodKey = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();

        if (urlData == null) return false;

        Config.setGLOSSY_TYPE(urlData.get("glossytype") == null ? "" : urlData.get("glossytype"));

        Intent intent = new Intent(activity, ThemeTitleActivity.class);
        intent.putExtra("whereis", "web");
        intent.putExtra(Const_EKEY.THEME_SELECT_TEMPLE, urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        intent.putExtra(Const_EKEY.HOME_SELECT_PRODUCT_CODE, prodKey);
        activity.startActivity(intent);
        return true;
    }
}
