package com.snaps.mobile.product_native_ui.json.detail;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductThumbnail extends SnapsProductNativeUIBaseResultJson {

    private static final long serialVersionUID = 8708552716310418439L;
    @SerializedName("thumbnails")
    private List<SnapsProductThumbnailItem> thumbnailItems;

    private HashMap<String, SnapsProductThumbnailItem> thumbnailMap;

    public List<SnapsProductThumbnailItem> getThumbnailItems() {
        return thumbnailItems;
    }

    public SnapsProductThumbnailItem getThumbnailItem( String type ) {
        if( thumbnailMap == null ) createMap();
        return thumbnailMap.get( type );
    }

    public void createMap() {
        if( thumbnailItems == null ) return;

        thumbnailMap = new HashMap<String, SnapsProductThumbnailItem>();
        for( SnapsProductThumbnailItem item : thumbnailItems ) {
            if( !StringUtil.isEmpty(item.getProdForm()) )
                thumbnailMap.put( item.getProdForm(), item );
        }
    }

    public void setThumbnailItems(List<SnapsProductThumbnailItem> thumbnailItems) {
        this.thumbnailItems = thumbnailItems;
    }
}
