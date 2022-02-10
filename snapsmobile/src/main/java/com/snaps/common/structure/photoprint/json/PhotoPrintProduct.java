package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintProduct {
    @SerializedName("template")
    private PhotoPrintJsonObjectTemplate template;
    @SerializedName("TmplInfo")
    private PhotoPrintJsonObjectTmplnfo TmplInfo;

    public PhotoPrintJsonObjectTemplate getTemplate() {
        return template;
    }

    public PhotoPrintJsonObjectTmplnfo getTmplInfo() {
        return TmplInfo;
    }
}
