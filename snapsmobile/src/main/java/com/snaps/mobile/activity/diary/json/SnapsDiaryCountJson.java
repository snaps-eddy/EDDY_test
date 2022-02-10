package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryCountJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("COUNT")
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
