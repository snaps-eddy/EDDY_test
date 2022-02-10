package com.snaps.facebook.utils;

import android.content.Context;
import android.util.TypedValue;

import com.snaps.common.utils.ui.StringUtil;

import java.util.Calendar;
import java.util.Locale;

public class FBCommonUtil {
	public static Calendar getCalFromFBTime( Long timestamp ) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis( timestamp * 1000 );
		return cal;
	}
	
	public static int getRandomInt( int start, int end ) {
		return (int)( (float)start + Math.random() * (float)(end - start) );
	}
	
	public static int convertDPtoPX(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	public static int convertPXtoDP(Context context, int px) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
	}

	public static float convertPixelsToSp(Context context, Float px) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px / scaledDensity;
	}

	public static float convertSpToPixels(Context context, Float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scaledDensity;
	}
	
	/**
	 * 날짜 포멧에 맞게 형식을 바꿔주는 함수, 카스북에서 복사하여 수정하여 사용.
	 * 
	 * @return
	 */
	public static String convertDateString(String format, Calendar sCal, Calendar eCal) {
		if (format.equals("YYYY.MM.DD - YYYY.MM.DD"))
			return String.format(Locale.getDefault(), "%04d.%02d.%02d - %04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					eCal.get(Calendar.YEAR), eCal.get(Calendar.MONTH) + 1, eCal.get(Calendar.DAY_OF_MONTH));
		else if (format.equals("YYYY"))
			return String.format(Locale.getDefault(), "%04d", sCal.get(Calendar.YEAR));
		else if (format.equals("Month - Month"))
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), false), getMonthString(eCal.get(Calendar.MONTH), false));
		else if (format.equals("YYYY/MM/DD, XDAY, TT h:mm"))
			return String.format(Locale.getDefault(), "%04d/%02d/%02d, %s, %s %d:%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true), getAM_PMString(sCal.get(Calendar.AM_PM), true), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("Month"))
			return String.format(Locale.getDefault(), "%s", getMonthString(sCal.get(Calendar.MONTH), false));
		else if (format.equals("MONTH - MONTH"))
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true));
		else if (format.equals("YYYY M DD KOR"))
			return String.format(Locale.getDefault(), "%04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH));
		else if (format.equals("tt hh:mm"))
			return String.format(Locale.getDefault(), "%s %d:%02d", getAM_PMString(sCal.get(Calendar.AM_PM), false), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("YYYY/MM/DD TT hh:mm"))
			return String.format(Locale.getDefault(), "%04d/%02d/%02d %s %02d:%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					getAM_PMString(sCal.get(Calendar.AM_PM), true), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("MONTH - MONTH YYYY"))
			return String.format(Locale.getDefault(), "%s - %s %04d", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
		else if (format.equals("M"))
			return String.format(Locale.getDefault(), "%02d", sCal.get(Calendar.MONTH) + 1);
		else if (format.equals("YYYY MONTH - YYYY MONTH"))
			return String.format(Locale.getDefault(), "%04d %s - %04d %s", sCal.get(Calendar.YEAR), getMonthString(sCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR),
					getMonthString(eCal.get(Calendar.MONTH), true));
		else if (format.equals("YYYY.MM.DD XDAY-KOR TT hh:MM"))
			return String.format(Locale.getDefault(), "%04d.%02d.%02d %s %s %02d:%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), false), getAM_PMString(sCal.get(Calendar.AM_PM), true), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("MONTH -"))
			return String.format(Locale.getDefault(), "%s -", getMonthString(sCal.get(Calendar.MONTH), true));
		else if (format.equals("MONTH"))
			return String.format(Locale.getDefault(), "%s", getMonthString(sCal.get(Calendar.MONTH), true));
		else if (format.equals("YYYY.MM.DD"))
			return String.format(Locale.getDefault(), "%04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH));
		else if (format.equals("XDAY-KOR TT hh:mm"))
			return String.format(Locale.getDefault(), "%s %s %02d:%02d", getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), false), getAM_PMString(sCal.get(Calendar.AM_PM), true),
					sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("YYYY.MM"))
			return String.format(Locale.getDefault(), "%04d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1);
		else if (format.equals("XDAY, TT hh:mm"))
			return String.format(Locale.getDefault(), "%s %s %02d:%02d", getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true), getAM_PMString(sCal.get(Calendar.AM_PM), true),
					sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("XDAY / TT hh:mm"))
			return String.format(Locale.getDefault(), "%s / %s %02d:%02d", getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true), getAM_PMString(sCal.get(Calendar.AM_PM), true),
					sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		else if (format.equals("MONTH - MONTH, YYYY"))
			return String.format(Locale.getDefault(), "%s - %s, %04d", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
		else if (format.equals("MONTH, YYYY - MONTH, YYYY") || format.equals("Month, YYYY - Month, YYYY")) {
			if(sCal.get(Calendar.YEAR) == eCal.get(Calendar.YEAR))
				return String.format(Locale.getDefault(), "%s - %s, %04d", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
			else
				return String.format(Locale.getDefault(), "%s, %04d - %s, %04d", getMonthString(sCal.get(Calendar.MONTH), true), sCal.get(Calendar.YEAR), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
		}
		else if (format.equals("XDAY"))
			return getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true);

		return "날짜 포멧 오류";
	}
	
	/**
	 * 영문 월을 구하는 함수. 예 August
	 */
	public static String getMonthString(int month, boolean isUpper) {
		String months[] = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

		return isUpper ? months[month].toUpperCase(Locale.getDefault()) : months[month];
	}

	/**
	 * 요일을 구하는 함수..
	 * 
	 * @param isEnglish
	 * @return
	 */
	public static String getDayOfWeekString(int dayOfWeek, boolean isEnglish) {

		switch (dayOfWeek) {
		case 2:
			return isEnglish ? "MONDAY" : "월요일";
		case 3:
			return isEnglish ? "TUESDAY" : "화요일";
		case 4:
			return isEnglish ? "WEDNESDAY" : "수요일";
		case 5:
			return isEnglish ? "THURSDAY" : "목요일";
		case 6:
			return isEnglish ? "FRIDAY" : "금요일";
		case 7:
			return isEnglish ? "SATURDAY" : "토요일";
		case 1:
			return isEnglish ? "SUNDAY" : "일요일";

		default:
			break;
		}
		return "";
	}

	/**
	 * 
	 * @param am_pm
	 * @param isUpper
	 * @return
	 */
	public static String getAM_PMString(int am_pm, boolean isUpper) {
		String result = "am";

		if (am_pm == Calendar.PM)
			result = "pm";

		return isUpper ? result.toUpperCase(Locale.getDefault()) : result;
	}

	public static int getYear(String date) {
		Calendar sCal = StringUtil.getCalendarWidthString(date);
		return sCal.get(Calendar.YEAR);
	}
	
	public static String getSharedOriginWriteDate( Calendar cal ) {
		return String.format(Locale.getDefault(), "%04d.%02d.%02d %s %s %02d:%02d",
				cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), getDayOfWeekString(cal.get(Calendar.DAY_OF_WEEK), false), getAM_PMString(cal.get(Calendar.AM_PM), false), cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE) );
	}
}
