package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectSource {
    @SerializedName("@type")
    private String type;
    @SerializedName("@bgcolor")
    private String bgcolor;

    public String getType() {
        return type;
    }

    public String getBgcolor() {
        return bgcolor;
    }
}
