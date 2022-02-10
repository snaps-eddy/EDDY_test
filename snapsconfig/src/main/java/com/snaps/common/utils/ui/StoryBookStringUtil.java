package com.snaps.common.utils.ui;

import android.util.Log;

import com.snaps.common.R;
import com.snaps.common.utils.log.Dlog;

import java.util.Calendar;
import java.util.Locale;

public class StoryBookStringUtil {
	private static final String TAG = StoryBookStringUtil.class.getSimpleName();
	/**
	 * 카카오스토리 날짜는 포멧에 맞게 형식을 바꿔주는 함수,
	 *
	 */
	static public String covertKakaoDate(String format, Calendar sCal, Calendar eCal) {
		if (format.equals("YYYY.MM.DD - YYYY.MM.DD")) {
			return String.format(Locale.getDefault(), "%04d.%02d.%02d - %04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					eCal.get(Calendar.YEAR), eCal.get(Calendar.MONTH) + 1, eCal.get(Calendar.DAY_OF_MONTH));
		} else if (format.equals("YYYY")) {
			return String.format(Locale.getDefault(), "%04d", sCal.get(Calendar.YEAR));
		} else if (format.equals("Month - Month")) {
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), false), getMonthString(eCal.get(Calendar.MONTH), false));
		} else if (format.equals("YYYY/MM/DD, XDAY, TT h:mm")) {
			return String.format(Locale.getDefault(), "%04d/%02d/%02d,%s,%s %d:%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true), getAM_PMString(sCal.get(Calendar.AM_PM), true), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("Month")) {
			return String.format(Locale.getDefault(), "%s", getMonthString(sCal.get(Calendar.MONTH), false));
		} else if (format.equals("MONTH - MONTH")) {
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true));
		} else if (format.equals("YYYY M DD KOR")) {
			return String.format(Locale.getDefault(), "%04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH));
		} else if (format.equals("tt hh:mm")) {
			return String.format(Locale.getDefault(), "%s %d:%02d", getAM_PMString(sCal.get(Calendar.AM_PM), false), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("YYYY/MM/DD TT hh:mm")) {
			return String.format(Locale.getDefault(), "%04d/%02d/%02d %s %02d:%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					getAM_PMString(sCal.get(Calendar.AM_PM), true), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("MONTH - MONTH YYYY")) {
			return String.format(Locale.getDefault(), "%s - %s %04d", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
		} else if (format.equals("M")) {
			return String.format(Locale.getDefault(), "%02d", sCal.get(Calendar.MONTH) + 1);
		} else if (format.equals("YYYY MONTH - YYYY MONTH")) {
			return String.format(Locale.getDefault(), "%04d %s - %04d %s", sCal.get(Calendar.YEAR), getMonthString(sCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR),
					getMonthString(eCal.get(Calendar.MONTH), true));
		} else if (format.equals("YYYY.MM.DD XDAY-KOR TT hh:MM")) {
			return String.format(Locale.getDefault(), "%04d.%02d.%02d %s %s %02d:%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH),
					getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), false), getAM_PMString(sCal.get(Calendar.AM_PM), true), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("MONTH -")) {
			return String.format(Locale.getDefault(), "%s -", getMonthString(sCal.get(Calendar.MONTH), true));
		} else if (format.equals("MONTH")) {
			return String.format(Locale.getDefault(), "%s", getMonthString(sCal.get(Calendar.MONTH), true));
		} else if (format.equals("YYYY.MM.DD")) {
			return String.format(Locale.getDefault(), "%04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH));
		} else if (format.equals("XDAY-KOR TT hh:mm")) {
			return String.format(Locale.getDefault(), "%s %s %02d:%02d", getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), false), getAM_PMString(sCal.get(Calendar.AM_PM), true),
					sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("YYYY.MM")) {
			return String.format(Locale.getDefault(), "%04d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1);
		} else if (format.equals("XDAY, TT hh:mm")) {
			return String.format(Locale.getDefault(), "%s %s %02d:%02d", getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true), getAM_PMString(sCal.get(Calendar.AM_PM), true),
					sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("XDAY / TT hh:mm")) {
			return String.format(Locale.getDefault(), "%s / %s %02d:%02d", getDayOfWeekString(sCal.get(Calendar.DAY_OF_WEEK), true), getAM_PMString(sCal.get(Calendar.AM_PM), true),
					sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (format.equals("MONTH - MONTH, YYYY")) {
			return String.format(Locale.getDefault(), "%s - %s, %04d", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
		} else if (format.equals("MONTH, YYYY - MONTH, YYYY") || format.equals("Month, YYYY - Month, YYYY")) {
			if (sCal.get(Calendar.YEAR) == eCal.get(Calendar.YEAR))
				return String.format(Locale.getDefault(), "%s - %s, %04d", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
			else
				return String.format(Locale.getDefault(), "%s, %04d - %s, %04d", getMonthString(sCal.get(Calendar.MONTH), true), sCal.get(Calendar.YEAR), getMonthString(eCal.get(Calendar.MONTH), true), eCal.get(Calendar.YEAR));
		} else {
			Dlog.d("covertKakaoDate() stringutil format : " + format);
		}

		Dlog.d("covertKakaoDate() stringutil 날짜 포멧 오류 = " + format);
		return "날짜 포멧 오류";
	}

	static public String covertKakaoDate(String format, String startDate, String endDate) {

		Calendar sCal = (startDate != null) ? StringUtil.getCalendarWidthString(startDate) : null;
		Calendar eCal = (endDate != null) ? StringUtil.getCalendarWidthString(endDate) : null;

		return covertKakaoDate(format, sCal, eCal);
	}

	static public String getMonthByKakaoDate(String date) {
		Calendar sCal = StringUtil.getCalendarWidthString(date);
		// return new int[]{sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH)};
		return String.format(Locale.getDefault(), "%04d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1);
	}

	/***
	 * snsProperty를 가지고 포멧에 맞게 텍스트를 가져오는 함수.
	 *
	 * @param snsProperty
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	static public String covertKakaoDateBySnsProperty(String snsProperty, String startDate, String endDate) {
		Calendar sCal = (startDate != null) ? StringUtil.getCalendarWidthString(startDate) : null;
		Calendar eCal = (endDate != null) ? StringUtil.getCalendarWidthString(endDate) : null;
		if (snsProperty.equals("story_date")) {
			return String.format(Locale.getDefault(), "%04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH));
		} else if (snsProperty.equals("story_time")) {
			return String.format(Locale.getDefault(), "%s %02d:%02d", getAM_PMString(sCal.get(Calendar.AM_PM), false), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (snsProperty.equals("month_pagenum") || snsProperty.equals("index_month")) {
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), false), getMonthString(eCal.get(Calendar.MONTH), false));
		} else if (snsProperty.equals("MONTH_PAGENUM") || snsProperty.equals("index_month")) {
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true));
		}
		return "";
	}

	static public String covertKakaoDateBySnsProperty(String snsProperty, Calendar sCal, Calendar eCal) {
		if (snsProperty.equals("story_date")) {
			return String.format(Locale.getDefault(), "%04d.%02d.%02d", sCal.get(Calendar.YEAR), sCal.get(Calendar.MONTH) + 1, sCal.get(Calendar.DAY_OF_MONTH));
		} else if (snsProperty.equals("story_time")) {
			return String.format(Locale.getDefault(), "%s %02d:%02d", getAM_PMString(sCal.get(Calendar.AM_PM), false), sCal.get(Calendar.HOUR), sCal.get(Calendar.MINUTE));
		} else if (snsProperty.equals("month_pagenum") || snsProperty.equals("index_month")) {
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), false), getMonthString(eCal.get(Calendar.MONTH), false));
		} else if (snsProperty.equals("MONTH_PAGENUM") || snsProperty.equals("index_month")) {
			return String.format(Locale.getDefault(), "%s - %s", getMonthString(sCal.get(Calendar.MONTH), true), getMonthString(eCal.get(Calendar.MONTH), true));
		}
		return "";
	}

	/**
	 * 영문 월을 구하는 함수. 예 August
	 * @return
	 */
	static public String getMonthString(int month, boolean isUpper) {
		String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

		return isUpper ? months[month].toUpperCase(Locale.getDefault()) : months[month];
	}

	/**
	 * 요일을 구하는 함수..
	 *
	 * @param isEnglish
	 * @return
	 */
	static public String getDayOfWeekString(int dayOfWeek, boolean isEnglish) {

		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return isEnglish ? "MONDAY" : ContextUtil.getString(R.string.monday, "월요일");//"월요일";
			case Calendar.TUESDAY:
				return isEnglish ? "TUESDAY" : ContextUtil.getString(R.string.tuesday, "화요일");//"화요일";
			case Calendar.WEDNESDAY:
				return isEnglish ? "WEDNESDAY" : ContextUtil.getString(R.string.wednesday, "수요일");//"수요일";
			case Calendar.THURSDAY:
				return isEnglish ? "THURSDAY" : ContextUtil.getString(R.string.thursday, "목요일");//"목요일";
			case Calendar.FRIDAY:
				return isEnglish ? "FRIDAY" : ContextUtil.getString(R.string.friday, "금요일");//"금요일";
			case Calendar.SATURDAY:
				return isEnglish ? "SATURDAY" : ContextUtil.getString(R.string.saturday, "토요일");//"토요일";
			case Calendar.SUNDAY:
				return isEnglish ? "SUNDAY" : ContextUtil.getString(R.string.sunday, "일요일");//"일요일";

			default:
				break;
		}
		return "";
	}

	/**
	 * @param am_pm
	 * @param isUpper
	 * @return
	 */
	static String getAM_PMString(int am_pm, boolean isUpper) {
		String result = "am";

		if (am_pm == Calendar.PM)
			result = "pm";

		return isUpper ? result.toUpperCase(Locale.getDefault()) : result;
	}

	public static int getYear(String date) {
		Calendar sCal = StringUtil.getCalendarWidthString(date);
		return sCal.get(Calendar.YEAR);
	}

	public static int getDifferentMonth(Calendar sCal, Calendar eCal) {
		int retMonth = 1;

		//시작일 큰경우 0
		if (sCal.compareTo(eCal) >= 0) {

		} else {
			//두날짜의 차이가 나는 개월수를 확인하기 위해 1월씩 더한다
			while (eCal.get(Calendar.YEAR) != sCal.get(Calendar.YEAR) || eCal.get(Calendar.MONTH) != sCal.get(Calendar.MONTH)) {
				sCal.add(Calendar.MONTH, 1);
				retMonth++;

			}
		}
		return retMonth;


	}
}
