package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryUserMissionInfoJson extends SnapsDiaryBaseResultJson {
    private static final String TAG = SnapsDiaryUserMissionInfoJson.class.getSimpleName();
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_DISPLAY_MISSION_EDATE")
    private String dispMissionEndDate;

    @SerializedName("F_DISPLAY_MISSION_SDATE")
    private String dispMissionStartDate;

    @SerializedName("F_MISSION_NO")
    private String missionNo;

    @SerializedName("F_MISSION_STAT")
    private String missionStat;

    @SerializedName("F_REMAIN_DATE")
    private String remainDateCount;

    @SerializedName("F_MISSION_EDATE")
    private String missionEndDate;

    @SerializedName("F_MISSION_SDATE")
    private String missionStartDate;

    @SerializedName("F_INK_CNTS")
    private String inkCnts;

    @SerializedName("F_MORE_INK_CNTS")
    private String needInkCnts;

    public String getDispMissionEndDate() {
        return dispMissionEndDate;
    }

    public void setDispMissionEndDate(String dispMissionEndDate) {
        this.dispMissionEndDate = dispMissionEndDate;
    }

    public String getDispMissionStartDate() {
        return dispMissionStartDate;
    }

    public void setDispMissionStartDate(String dispMissionStartDate) {
        this.dispMissionStartDate = dispMissionStartDate;
    }

    public String getMissionNo() {
        return missionNo;
    }

    public void setMissionNo(String missionNo) {
        this.missionNo = missionNo;
    }

    public String getRemainDateCount() {
        return remainDateCount;
    }

    public void setRemainDateCount(String remainDateCount) {
        this.remainDateCount = remainDateCount;
    }

    public String getMissionEndDate() {
        return missionEndDate;
    }

    public void setMissionEndDate(String missionEndDate) {
        this.missionEndDate = missionEndDate;
    }

    public String getMissionStartDate() {
        return missionStartDate;
    }

    public void setMissionStartDate(String missionStartDate) {
        this.missionStartDate = missionStartDate;
    }

    public String getInkCntsStr() {
        return inkCnts;
    }

    public int getInkCnts() {
        if(inkCnts == null || inkCnts.length() < 1) return 0;
        try {
           return Integer.parseInt(inkCnts);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    public int getNeedInkCnts() {
        if(needInkCnts == null || needInkCnts.length() < 1) return SnapsDiaryConstants.INVALID_INK_CNT;
        try {
           return Integer.parseInt(needInkCnts);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
        return SnapsDiaryConstants.INVALID_INK_CNT;
    }

    public void setInkCnts(String inkCnts) {
        this.inkCnts = inkCnts;
    }

    public String getMoreInkCntsStr() {
        return needInkCnts;
    }

    public void setNeedInkCnts(String needInkCnts) {
        this.needInkCnts = needInkCnts;
    }

    public int getTotalInkCnt() {
        return getNeedInkCnts() + getInkCnts();
    }

    public String getMissionStat() {
        return missionStat;
    }

    public void setMissionStat(String missionStat) {
        this.missionStat = missionStat;
    }
}
