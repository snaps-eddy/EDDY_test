package com.snaps.mobile.activity.diary.recoder;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.mobile.activity.diary.SnapsDiaryCommonUtils;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

import java.io.Serializable;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public class SnapsDiaryListItem implements Parcelable, Serializable {

    private static final long serialVersionUID = -2226916461945986668L;

    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_ITEM = 1;
    public static final int ITEM_TYPE_DUMMY = 2;

    private int itemType = ITEM_TYPE_ITEM;

    private String key = null;
    private String date = null;
    private String registeredDate = null;
    private String wheater = null;
    private String feels = null;
    private String thumbnail = null;
    private String contents = null;
    private String filePath = null;
    private String diaryNo = null;
//    private String regDate = null;
    private String osType = null;
    private boolean isForceMoreText = false;

    public void set(SnapsDiaryListItem item) {
        if(item == null) return;
        key = item.key;
        registeredDate = item.registeredDate;
        date = item.date;
        wheater = item.wheater;
        feels = item.feels;
        thumbnail = item.thumbnail;
        contents = item.contents;
        filePath = item.filePath;
        diaryNo = item.diaryNo;
//        regDate = item.regDate;
        osType = item.osType;
        isForceMoreText = item.isForceMoreText;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

//    public String getRegDate() {
//        return regDate;
//    }
//
//    public void setRegDate(String regDate) {
//        this.regDate = regDate;
//    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFormattedDate() {
        return SnapsDiaryCommonUtils.convertDateForDiary(getDate());
    }

    public String getFormattedRegisteredDate() {
        String registeredDate = getRegisteredDate();
        if (registeredDate == null || registeredDate.length() < 1) {
            registeredDate = getDate();
        }

        return SnapsDiaryCommonUtils.convertRegisteredDateForDiary(registeredDate);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public SnapsDiaryConstants.eWeather getWeatherEnum() {
        return SnapsDiaryConstants.eWeather.converterStrToEnum(getWeather());
    }

    public SnapsDiaryConstants.eFeeling getFeelsEnum() {
        return SnapsDiaryConstants.eFeeling.converterStrToEnum(getFeels());
    }

    public String getWeather() {
        return wheater;
    }

    public void setWeather(String wheater) {
        this.wheater = wheater;
    }

    public String getFeels() {
        return feels;
    }

    public void setFeels(String feels) {
        this.feels = feels;
    }

    public String getThumbnail() { return thumbnail; }

    public String getThumbnailUrl() { return SnapsAPI.DOMAIN() + thumbnail; }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
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

    public boolean isHeader() {
        return itemType == ITEM_TYPE_HEADER;
    }

    public boolean isDummyItem() {
        return itemType == ITEM_TYPE_DUMMY;
    }

    public boolean isForceMoreText() {
        return isForceMoreText;
    }

    public void setIsForceMoreText(boolean isForceMoreText) {
        this.isForceMoreText = isForceMoreText;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는
        dest.writeString(key);
        dest.writeString(date);
        dest.writeString(registeredDate);
        dest.writeString(wheater);
        dest.writeString(feels);
        dest.writeString(thumbnail);
        dest.writeString(contents);
        dest.writeString(filePath);
        dest.writeString(diaryNo);
//        dest.writeString(regDate);
        dest.writeString(osType);

        boolean[] arrBool = { isForceMoreText };
        dest.writeBooleanArray(arrBool);
    }

    private void readFromParcel(Parcel in) {
        key = in.readString();
        date = in.readString();
        registeredDate = in.readString();
        wheater = in.readString();
        feels = in.readString();
        thumbnail = in.readString();
        contents = in.readString();
        filePath = in.readString();
        diaryNo = in.readString();
//        regDate = in.readString();
        osType = in.readString();

        boolean[] arrBool = new boolean[1];
        in.readBooleanArray(arrBool);
        isForceMoreText = arrBool[0];
    }

    public SnapsDiaryListItem() {
        this.itemType = ITEM_TYPE_ITEM;
    }

    public SnapsDiaryListItem(int type) {
        this.itemType = type;
    }

    public SnapsDiaryListItem(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsDiaryListItem createFromParcel(Parcel in) {
            return new SnapsDiaryListItem(in);
        }

        @Override
        public SnapsDiaryListItem[] newArray(int size) {
            return new SnapsDiaryListItem[size];
        }
    };
}
