package com.snaps.mobile.utils.select_product_junction.interfaces;

import android.app.Activity;

import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public interface ISnapsProductLauncher {
    boolean startMakeProduct(Activity activity, SnapsProductAttribute attribute);
}
