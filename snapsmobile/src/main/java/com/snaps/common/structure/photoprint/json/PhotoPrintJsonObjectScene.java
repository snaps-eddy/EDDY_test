package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectScene {
    @SerializedName("@type")
    private String type;
    @SerializedName("@width")
    private String width;
    @SerializedName("@height")
    private String height;
    @SerializedName("layer")
    private PhotoPrintJsonObjectLayer[] layer;

    public String getType() {
        return type;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public PhotoPrintJsonObjectLayer[] getLayer() {
        return layer;
    }
}
