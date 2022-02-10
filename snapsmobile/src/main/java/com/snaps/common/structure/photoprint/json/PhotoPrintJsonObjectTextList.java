package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectTextList {
    @SerializedName("@rc")
    private String rc;
    @SerializedName("@vertical")
    private String vertical;
    @SerializedName("@angle")
    private String angle;
    @SerializedName("htmlText")
    private String htmlText;

    public String getRc() {
        return rc;
    }

    public String getVertical() {
        return vertical;
    }

    public String getAngle() {
        return angle;
    }

    public String getHtmlText() {
        return htmlText;
    }
}
