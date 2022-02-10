package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryListItemJson extends BaseResponse {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_SAVE_DATE")
    private String saveDate;

    @SerializedName("F_DIARY_CONTENTS")
    private String diaryContents;

    @SerializedName("F_DIARY_DATE")
    private String date;

    @SerializedName("F_FILE_PATH")
    private String filePath;

    @SerializedName("F_DIARY_NO")
    private String diaryNo;

    @SerializedName("F_THUM_IMGS")
    private String thumbnailImg;

    @SerializedName("F_WEATHER_CODE")
    private String weatherCode;

    @SerializedName("F_FEELING_CODE")
    private String feelingCode;

    @SerializedName("F_MORE_YORN")
    private String forceMoreText;

    @SerializedName("F_OS_TYPE")
    private String osType;

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getDiaryContents() {
        if(diaryContents == null)
            diaryContents = "";
        return diaryContents;
    }

    public String getForceMoreText() {
        return forceMoreText;
    }

    public void setForceMoreText(String forceMoreText) {
        this.forceMoreText = forceMoreText;
    }

    public void setDiaryContents(String diaryContents) {
        this.diaryContents = diaryContents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDiaryNo() {
        return diaryNo;
    }

    public void setDiaryNo(String diaryNo) {
        this.diaryNo = diaryNo;
    }

    public String getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(String thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getFeelingCode() {
        return feelingCode;
    }

    public void setFeelingCode(String feelingCOde) {
        this.feelingCode = feelingCode;
    }

    public boolean isOSTypeAndroid() {
        return getOsType() != null && getOsType().equalsIgnoreCase(SnapsDiaryConstants.CODE_OS_TYPE_ANDROID);
    }

    public String getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(String saveDate) {
        this.saveDate = saveDate;
    }
}
