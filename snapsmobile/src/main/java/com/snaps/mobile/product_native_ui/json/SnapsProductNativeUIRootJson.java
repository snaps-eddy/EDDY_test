package com.snaps.mobile.product_native_ui.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsProductNativeUIRootJson<T extends SnapsProductNativeUIBaseResultJson> extends BaseResponse {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("rsp")
    private SnapsProductNativeUICommonResultJson result;

    public SnapsProductNativeUICommonResultJson getResult() {
        return result;
    }
}
