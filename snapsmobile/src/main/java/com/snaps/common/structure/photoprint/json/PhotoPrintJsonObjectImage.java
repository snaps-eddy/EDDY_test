package com.snaps.common.structure.photoprint.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by songhw on 2017. 3. 14..
 */

public class PhotoPrintJsonObjectImage {
    @SerializedName("@x")
    private int x;
    @SerializedName("@y")
    private int y;
    @SerializedName("@width")
    private int width;
    @SerializedName("@height")
    private int height;
    @SerializedName("source")
    private PhotoPrintJsonObjectSource source;
    @SerializedName("regist")
    private PhotoPrintJsonObjectRegist regist;
    @SerializedName("modify_content")
    private PhotoPrintJsonObjectModifyContent modifyContent;


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public PhotoPrintJsonObjectSource getSource() {
        return source;
    }

    public PhotoPrintJsonObjectRegist getRegist() {
        return regist;
    }

    public PhotoPrintJsonObjectModifyContent getModifyContent() {
        return modifyContent;
    }
}
