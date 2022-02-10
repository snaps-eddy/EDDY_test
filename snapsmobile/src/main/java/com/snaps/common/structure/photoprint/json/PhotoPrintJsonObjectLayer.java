package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectLayer {
    @SerializedName("@name")
    private String name;
    @SerializedName("image")
    private PhotoPrintJsonObjectImage image;
    @SerializedName("textlist")
    private PhotoPrintJsonObjectTextList textlist;

    public String getName() {
        return name;
    }

    public PhotoPrintJsonObjectTextList getTextlist() {
        return textlist;
    }
}
