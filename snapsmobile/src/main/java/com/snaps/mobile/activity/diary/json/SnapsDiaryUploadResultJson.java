package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryUploadResultJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_IS_SAVE_INK")
    private String isIssuedInk; //잉크를 발급 받았는 지 여부

    @SerializedName("MISSION_INFO")
    private SnapsDiaryUserMissionInfoJson missionInfo;

    public boolean isIssuedInk() {
        return BaseResponse.parseBool(getIsIssuedInk());
    }

    public String getIsIssuedInk() {
        return isIssuedInk;
    }

    public void setIsIssuedInk(String isIssuedInk) {
        this.isIssuedInk = isIssuedInk;
    }

    public SnapsDiaryUserMissionInfoJson getMissionInfo() {
        return missionInfo;
    }

    public void setMissionInfo(SnapsDiaryUserMissionInfoJson missionInfo) {
        this.missionInfo = missionInfo;
    }
}
