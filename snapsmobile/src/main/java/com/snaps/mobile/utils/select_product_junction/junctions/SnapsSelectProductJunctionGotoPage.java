package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;

import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class SnapsSelectProductJunctionGotoPage implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;
        WebViewCmdGotoPage.gotoPage(activity, attribute.getHandleDatas());
        return true;
    }
}
