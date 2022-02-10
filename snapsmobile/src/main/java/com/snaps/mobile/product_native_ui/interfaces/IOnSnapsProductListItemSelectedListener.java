package com.snaps.mobile.product_native_ui.interfaces;

import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;

/**
 * Created by ysjeong on 16. 3. 14..
 */
public interface IOnSnapsProductListItemSelectedListener {
    void onProductListItemSelected(final int position, final SnapsBaseProductListItem item);
}
