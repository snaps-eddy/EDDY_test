package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectTemplate {
    @SerializedName("@type")
    private String type;
    @SerializedName("scene")
    private PhotoPrintJsonObjectScene scene;

    public String getType() {
        return type;
    }

    public PhotoPrintJsonObjectScene getScene() {
        return scene;
    }
}
