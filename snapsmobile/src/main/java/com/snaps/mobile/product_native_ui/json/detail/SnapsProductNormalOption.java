package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductNormalOption extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = -4324781683914847947L;
    @SerializedName("title")
    private String title;

    @SerializedName("items")
    private List<SnapsProductNormalOptionItem> items;

    public List<SnapsProductNormalOptionItem> getItems() { return items; }

    public void deleteNullObjects() {
        if( items == null ) return;

        ArrayList<SnapsProductNormalOptionItem> deleteList = new ArrayList<SnapsProductNormalOptionItem>();
        for( SnapsProductNormalOptionItem item : items ) {
            if( item.isEmpty() )
                deleteList.add( item );
        }

        if( deleteList.size() > 0 )
            items.removeAll( deleteList );


        if( items.isEmpty() )
            items = null;
    }

    public void setItems(List<SnapsProductNormalOptionItem> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
