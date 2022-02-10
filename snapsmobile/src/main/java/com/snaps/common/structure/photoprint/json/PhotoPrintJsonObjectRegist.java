package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectRegist {
    @SerializedName("@name")
    private String name;
    @SerializedName("@value")
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
