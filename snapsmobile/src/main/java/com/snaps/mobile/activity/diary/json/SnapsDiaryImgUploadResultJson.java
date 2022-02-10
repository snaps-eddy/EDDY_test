package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryImgUploadResultJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_IMG_YEAR")
    private String imgYear;

    @SerializedName("F_DIARY_NO")
    private String diaryNo;

    @SerializedName("F_EXIF_DATE")
    private String exifDate;

    @SerializedName("F_REAL_FILE_PATH")
    private String realFilePath;

    @SerializedName("F_ORG_FILE_PATH")
    private String orgFilePath;

    @SerializedName("F_MID_FILE_PATH")
    private String midFilePath;

    @SerializedName("F_IMG_HEIGHT")
    private String imgHeight;

    @SerializedName("F_IMG_SQNC")
    private String imgSqnc;

    @SerializedName("F_FILE_SAVE_NAME")
    private String fileSaveName;

    @SerializedName("F_TINY_FILE_PATH")
    private String tinyFilePath;

    @SerializedName("F_IMG_WIDTH")
    private String imgWidth;

    public String getImgYear() {
        return imgYear;
    }

    public void setImgYear(String imgYear) {
        this.imgYear = imgYear;
    }

    public String getDiaryNo() {
        return diaryNo;
    }

    public void setDiaryNo(String diaryNo) {
        this.diaryNo = diaryNo;
    }

    public String getExifDate() {
        return exifDate;
    }

    public void setExifDate(String exifDate) {
        this.exifDate = exifDate;
    }

    public String getRealFilePath() {
        return realFilePath;
    }

    public void setRealFilePath(String realFilePath) {
        this.realFilePath = realFilePath;
    }

    public String getOrgFilePath() {
        return orgFilePath;
    }

    public void setOrgFilePath(String orgFilePath) {
        this.orgFilePath = orgFilePath;
    }

    public String getMidFilePath() {
        return midFilePath;
    }

    public void setMidFilePath(String midFilePath) {
        this.midFilePath = midFilePath;
    }

    public String getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(String imgHeight) {
        this.imgHeight = imgHeight;
    }

    public String getImgSqnc() {
        return imgSqnc;
    }

    public void setImgSqnc(String imgSqnc) {
        this.imgSqnc = imgSqnc;
    }

    public String getFileSaveName() {
        return fileSaveName;
    }

    public void setFileSaveName(String fileSaveName) {
        this.fileSaveName = fileSaveName;
    }

    public String getTinyFilePath() {
        return tinyFilePath;
    }

    public void setTinyFilePath(String tinyFilePath) {
        this.tinyFilePath = tinyFilePath;
    }

    public String getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(String imgWidth) {
        this.imgWidth = imgWidth;
    }
}
