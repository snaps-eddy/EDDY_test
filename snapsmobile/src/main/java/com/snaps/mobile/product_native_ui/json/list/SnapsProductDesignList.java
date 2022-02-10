package com.snaps.mobile.product_native_ui.json.list;

import com.google.gson.annotations.SerializedName;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ifunbae on 2016. 10. 14..
 */

public class SnapsProductDesignList extends SnapsProductNativeUIBaseResultJson {
    private static final long serialVersionUID = -835241438191749334L;
    @SerializedName("list")
    private List<SnapsProductDesignCategory> productList;

    public List<SnapsProductDesignCategory> getProductList() {
        return productList;
    }

    public Map<String, SnapsProductDesignCategory> getProductMap() {
        Map<String, SnapsProductDesignCategory> map = new HashMap<String, SnapsProductDesignCategory>();
        if( productList != null && !productList.isEmpty() ) {
            for( SnapsProductDesignCategory category : productList )
                map.put( category.getCATEGORY_NAME(), category );
        }
        return map;
    }
}
