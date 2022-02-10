package com.snaps.mobile.product_native_ui.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsProductNativeUIBaseResultJson extends BaseResponse {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("stat")
    private String state;

    public String getState() {
        return state;
    }

    public boolean isSuccess() {
        return getState() != null && getState().equalsIgnoreCase("ok");
    }
}
