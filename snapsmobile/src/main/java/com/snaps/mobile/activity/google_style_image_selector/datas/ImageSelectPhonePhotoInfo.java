package com.snaps.mobile.activity.google_style_image_selector.datas;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StoryBookStringUtil;
import com.snaps.common.utils.ui.StringUtil;

import java.io.File;
import java.util.Calendar;

/**
 * Created by ysjeong on 2017. 1. 3..
 */

public class ImageSelectPhonePhotoInfo {
	private String orgImgPath = null;
	private String thumbnailPath = null;
	private long takenTime = 0l;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	private String dayOfWeek = "";

	public long getTakenTime() {
		return takenTime;
	}

	public void setTakenTime(long takenTime) {
		this.takenTime = takenTime;
	}

	public String getOrgImgPath() {
		return orgImgPath;
	}

	public void setOrgImgPath(String orgImgPath) {
		this.orgImgPath = orgImgPath;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
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

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public void setDateByTakenTime() {
		Calendar calendarTaken = Calendar.getInstance();
		calendarTaken.setTimeInMillis(getTakenTime());

		Calendar currentCalendar = Calendar.getInstance();

		if (calendarTaken.get(Calendar.YEAR) <= 1970 || ((calendarTaken.get(Calendar.YEAR) > currentCalendar.get(Calendar.YEAR)))) { //TAKEN TIME이 없으면 파일 저장된 날짜
			if (!StringUtil.isEmpty(getOrgImgPath())) {
				File photoFile = new File(getOrgImgPath());
				if (photoFile.exists()) {
					calendarTaken.setTimeInMillis(photoFile.lastModified());
				}
			}
		}

		setYear(calendarTaken.get(Calendar.YEAR));
		setMonth(calendarTaken.get(Calendar.MONTH) + 1);
		setDay(calendarTaken.get(Calendar.DAY_OF_MONTH));

		setDayOfWeek(StoryBookStringUtil.getDayOfWeekString(calendarTaken.get(Calendar.DAY_OF_WEEK), !Config.useKorean()));
	}
}
