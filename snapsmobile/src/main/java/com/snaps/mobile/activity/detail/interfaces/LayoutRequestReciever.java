package com.snaps.mobile.activity.detail.interfaces;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.EditText;

import com.snaps.mobile.activity.detail.layouts.LinkedLayout;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

/**
 * Created by songhw on 2016. 10. 19..
 */
public interface LayoutRequestReciever {
    int createNextLayout( ViewGroup parent, LinkedLayout layout, Object data, int headViewId );
    int createNextLayout( ViewGroup parent, String type, Object data, int headViewId );
    void removeLayout( int id );
    void itemSelected( String attribute, String value, boolean required );
    int itemSelected( SnapsProductOptionCommonValue value, ViewGroup parent, int headViewId );
    String getSelectedValue( String attribute );
    void openUrl( String url, boolean fullScreen );
    void openZoomUrl( String url );
    void onEditTextFocused( EditText v );
    Activity getActivity();
}
