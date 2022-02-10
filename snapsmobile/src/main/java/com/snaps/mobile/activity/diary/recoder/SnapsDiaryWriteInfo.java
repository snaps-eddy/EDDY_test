package com.snaps.mobile.activity.diary.recoder;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.SnapsDiaryCommonUtils;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ysjeong on 16. 3. 16..
 */
public class SnapsDiaryWriteInfo {
    private static final String TAG = SnapsDiaryWriteInfo.class.getSimpleName();
    private int year;
    private int month;
    private int day;

    private String date;
    private String contents; //UI에 뿌려줄 텍스트

    private boolean isForceMoreText = false;

    private int templateGridPosition = -1;

    private SnapsDiaryConstants.eWeather weather = SnapsDiaryConstants.eWeather.NONE;
    private SnapsDiaryConstants.eFeeling feels = SnapsDiaryConstants.eFeeling.NONE;

    private ArrayList<MyPhotoSelectImageData> arrPhotoImageDatas = null;

    public ArrayList<MyPhotoSelectImageData> getPhotoImageDataList() {
        return arrPhotoImageDatas;
    }

    public void setPhotoImageDataList(ArrayList<MyPhotoSelectImageData> list) {
        if(arrPhotoImageDatas == null)
            arrPhotoImageDatas = new ArrayList<MyPhotoSelectImageData>();

        if(!arrPhotoImageDatas.isEmpty())
            arrPhotoImageDatas.clear();

        if(list != null && !list.isEmpty()) {
            for(MyPhotoSelectImageData imgData : list) {
                MyPhotoSelectImageData copyData = new MyPhotoSelectImageData();
                copyData.set(imgData);
                arrPhotoImageDatas.add(copyData);
            }
        }
    }

    public int getTemplateGridPosition() {
        return templateGridPosition;
    }

    public void setTemplateGridPosition(int templateGridPosition) {
        this.templateGridPosition = templateGridPosition;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDateNumber() {
        if (year == 0) {
            Calendar now = Calendar.getInstance();
            year = now.get(Calendar.YEAR);
            month = now.get(Calendar.MONTH) + 1;
            day = now.get(Calendar.DAY_OF_MONTH);
            setYMDToDateStr();
        }

        return String.format("%d%02d%02d", year, month, day);
    }

    public String getDateFormatted() {
        return SnapsDiaryCommonUtils.convertDateForDiary(getDate());
    }

    public void setYMDToDateStr(String date) {
        if(date == null || date.length() != 8) return;

        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date originDate = originformat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(originDate);
            setYear(cal.get(Calendar.YEAR));
            setMonth(cal.get(Calendar.MONTH) + 1);
            setDay(cal.get(Calendar.DAY_OF_MONTH));
            setYMDToDateStr();
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * year, month, day를 인터페이스 포멧에 맞게..
     */
    public void setYMDToDateStr() {
        this.date = String.format("%04d%02d%02d", year, month, day);
    }

    public String getDate() {
        return this.date;
    }

    public SnapsDiaryConstants.eWeather getWeather() {
        return weather;
    }

    public void setWeather(SnapsDiaryConstants.eWeather weather) {
        this.weather = weather;
    }

    public void setWeather(String weather) {
        if(weather == null) return;
        this.weather = SnapsDiaryConstants.eWeather.converterStrToEnum(weather);
    }

    public SnapsDiaryConstants.eFeeling getFeels() {
        return feels;
    }

    public void setFeels(SnapsDiaryConstants.eFeeling feels) {
        this.feels = feels;
    }

    public void setFeels(String feels) {
        if(feels == null) return;
        this.feels = SnapsDiaryConstants.eFeeling.converterStrToEnum(feels);
    }

    public String getContents() {
        return contents;
    }

    public boolean isForceMoreText() {
        return isForceMoreText;
    }

    public void setIsForceMoreText(boolean isForceMoreText) {
        this.isForceMoreText = isForceMoreText;
    }

    public String getPostContents() {
        if(contents == null) return "";

        String[] lines = contents.split("\n");
        if(lines.length > 2)
            return lines[0] + "\n" + lines[1] + "\n" + lines[2];

        if(contents.length() > 120)
            return contents.substring(0, 120);

        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void clearImageList() {
        if(arrPhotoImageDatas != null && !arrPhotoImageDatas.isEmpty()) {
            arrPhotoImageDatas.clear();
            arrPhotoImageDatas = null;
        }
    }
}
