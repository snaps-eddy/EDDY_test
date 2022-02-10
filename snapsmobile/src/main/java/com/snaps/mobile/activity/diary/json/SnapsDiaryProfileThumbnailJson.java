package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryProfileThumbnailJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_DIARY_PROFILE_PATH")
    private String thumbnailPath;

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
