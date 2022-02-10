package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;
import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;

import java.util.HashMap;

public class SnapsSelectProductJunctionForBudsCase implements ISnapsProductLauncher {
    @Override
    public boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute) {
        if (activity == null || attribute == null) return false;

        String productCode = attribute.getProdKey();
        HashMap<String, String> urlData = attribute.getUrlData();
        if (urlData == null) return false;

        Config.setPROD_CODE(productCode);
        Config.setTMPL_CODE(urlData.get(Const_EKEY.WEB_TEMPLE_KEY));
        Config.setPAPER_CODE(urlData.get(Const_EKEY.WEB_PAPER_CODE));

        Intent intent = new Intent(activity, SnapsEditActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.BUDS_CASE.ordinal());

        intent.putExtras(bundle);
        intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_ALL_PARAM_MAP, urlData);
        activity.startActivity(intent);
        return true;
    }
}
