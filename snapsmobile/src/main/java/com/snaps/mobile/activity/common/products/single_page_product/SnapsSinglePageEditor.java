package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;

import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorCommonImplement;

/**
 * Created by ysjeong on 2017. 10. 19..
 */

public abstract class SnapsSinglePageEditor extends SnapsProductBaseEditorCommonImplement {
    private static final String TAG = SnapsSinglePageEditor.class.getSimpleName();

    protected SnapsSinglePageEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
}
