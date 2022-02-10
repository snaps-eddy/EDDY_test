package com.snaps.mobile.activity.diary.recoder;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

import java.util.ArrayList;

/**
 * Created by ysjeong on 16. 3. 22..
 */
public class SnapsDiaryPublishItem {
    private String date;
    private String contents;

    private SnapsDiaryConstants.eWeather weather;
    private SnapsDiaryConstants.eFeeling feels;

    private SnapsTemplate template;

    private String thumbnailUrl = null;

    private String filePath = null;
    private String diaryNo = null;

    public String osType = null;

    private ArrayList<MyPhotoSelectImageData> arrPhotoImageDatas = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        if(contents == null) return "";
        return contents;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public void setTemplate( SnapsTemplate template ) { this.template = template; }
    public SnapsTemplate getTemplate() { return this.template; }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public SnapsDiaryConstants.eWeather getWeather() {
        return weather;
    }

    public void setWeather(SnapsDiaryConstants.eWeather weather) {
        this.weather = weather;
    }

    public SnapsDiaryConstants.eFeeling getFeels() {
        return feels;
    }

    public void setFeels(SnapsDiaryConstants.eFeeling feels) {
        this.feels = feels;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public ArrayList<MyPhotoSelectImageData> getArrPhotoImageDatas() {
        return arrPhotoImageDatas;
    }

    public void setArrPhotoImageDatas(ArrayList<MyPhotoSelectImageData> arrPhotoImageDatas) {
        this.arrPhotoImageDatas = arrPhotoImageDatas;
    }

    public void setWeather(String weather) {
        if(weather == null) return;
        this.weather = SnapsDiaryConstants.eWeather.converterStrToEnum(weather);
    }


    public void setFeels(String feels) {
        if(feels == null) return;
        this.feels = SnapsDiaryConstants.eFeeling.converterStrToEnum(feels);
    }
}
