package com.snaps.mobile.activity.diary.recoder;

import android.graphics.Bitmap;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.json.SnapsDiaryUserMissionInfoJson;

/**
 * Created by ysjeong on 16. 3. 14..
 */
public class SnapsDiaryUserInfo {
    private static final String TAG = SnapsDiaryUserInfo.class.getSimpleName();
    private String missionNo = null;
    private String remainDays = null;
    private String missionStat = null;
    private int maxInkCount = 0;

    private int needInkCount = 0;
    private int currentInkCount = 0;

    private boolean isMissionValildCheckResult = true;

    private Bitmap thumbnailCache = null;
    private String thumbnailPath = null;

    public void set(SnapsDiaryUserMissionInfoJson missionResult) {
        setCurrentInkCount(missionResult.getInkCnts());
        setMaxInkCount(missionResult.getInkCnts() + missionResult.getNeedInkCnts());
        setMissionNo(missionResult.getMissionNo());
        setNeedInkCount(missionResult.getNeedInkCnts());
        setRemainDays(missionResult.getRemainDateCount());
        setMissionStat(missionResult.getMissionStat());
    }

    public void releaseCache() {
        if (thumbnailCache != null && !thumbnailCache.isRecycled()) {
            thumbnailCache.recycle();
            thumbnailCache = null;
        }
    }

    public boolean isMissionValildCheckResult() {
        return isMissionValildCheckResult;
    }

    public void setIsMissionValildCheckResult(boolean isMissionValildCheckResult) {
        this.isMissionValildCheckResult = isMissionValildCheckResult;
    }

    public String getMissionStat() {
        return missionStat;
    }

    public void setMissionStat(String missionStat) {
        this.missionStat = missionStat;
    }

    public int getMaxInkCount() {
        return maxInkCount;
    }

    public void setMaxInkCount(int maxInkCount) {
        this.maxInkCount = maxInkCount;
    }

    public int getCurrentInkCount() {
        return currentInkCount;
    }

    public void setCurrentInkCount(int currentInkCount) {
        this.currentInkCount = currentInkCount;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public Bitmap getThumbnailCache() {
        return thumbnailCache;
    }

    public void setThumbnailCache(Bitmap thumbnailCache) {
        this.thumbnailCache = thumbnailCache;
    }

    public boolean isBeforeMissionStart() {
        return getMissionNo() == null || getMissionNo().length() < 1;
    }

    public boolean isSuccessMission() {
        if (getMissionStat() == null) return false;
        else if (!isBeforeMissionStart() && getMissionStat().equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_SUCCESS)) return true;
        return false;
    }

    public boolean isValidPeriodOfMission() {
        return getMissionStateEnum().equals(SnapsDiaryConstants.eMissionState.ING);
    }

    public boolean isFailedMission() {
        if (getMissionStat() == null) return false;
        else if (!isBeforeMissionStart() && getMissionStat().equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_FAILED)) return true;
        return false;
    }

    public boolean checkPassedMissionPeriod() {
        if(SnapsDiaryConstants.IS_QA_VERSION || getMissionStat() == null || !getMissionStat().equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_ING) || getRemainDays() == null) return false;

        int remainDay;
        try {
            remainDay = Integer.parseInt(getRemainDays());
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
            return false;
        }

        return !isBeforeMissionStart()
                && (getNeedInkCount() > 0)
                && (getNeedInkCount() > (remainDay + 1));
    }

    public boolean checkAlreadyMissionCompleted() {
        if(SnapsDiaryConstants.IS_QA_VERSION || getMissionStat() == null || !getMissionStat().equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_ING) || getRemainDays() == null) return false;

        int remainDay;
        try {
            remainDay = Integer.parseInt(getRemainDays());
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
            return false;
        }

        return !isBeforeMissionStart()
                && getNeedInkCount() <= 0
                && remainDay >= 0;
    }

    public String getMissionNo() {
        return missionNo;
    }

    public void setMissionNo(String missionNo) {
        this.missionNo = missionNo;
    }

    public String getRemainDays() {
        return remainDays;
    }

    public int getRemainDaysInt() {
        if (remainDays == null || remainDays.length() < 1) return 0;
        try {
            return Integer.parseInt(remainDays);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    public void setRemainDays(String remainDays) {
        this.remainDays = remainDays;
    }

    public int getNeedInkCount() {
        return needInkCount;
    }

    public void setNeedInkCount(int needInkCount) {
        this.needInkCount = needInkCount;
    }

    public SnapsDiaryConstants.eMissionState getMissionStateEnum() {
        if (isBeforeMissionStart()) {
            return SnapsDiaryConstants.eMissionState.PREV;
        } else if(isSuccessMission()) {
            return SnapsDiaryConstants.eMissionState.SUCCESS;
        } else if(isFailedMission()) {
            return SnapsDiaryConstants.eMissionState.FAILED;
        } else {
            return SnapsDiaryConstants.eMissionState.ING;
        }
    }
}
