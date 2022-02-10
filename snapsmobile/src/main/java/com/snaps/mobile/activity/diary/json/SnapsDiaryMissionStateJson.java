package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryMissionStateJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_MISSION_NO")
    private String missionNo;

    public String getMissionNo() {
        return missionNo;
    }

    public void setMissionNo(String missionNo) {
        this.missionNo = missionNo;
    }
}
