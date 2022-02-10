package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectModifyContent {
    @SerializedName("@move")
    private String move;
    @SerializedName("@border")
    private String border;
    @SerializedName("@resize")
    private String resize;
    @SerializedName("@rotate")
    private String rotate;
    @SerializedName("@flip")
    private String flip;
    @SerializedName("@alpha")
    private String alpha;

    public String getMove() {
        return move;
    }

    public String getBorder() {
        return border;
    }

    public String getResize() {
        return resize;
    }

    public String getRotate() {
        return rotate;
    }

    public String getFlip() {
        return flip;
    }

    public String getAlpha() {
        return alpha;
    }
}
