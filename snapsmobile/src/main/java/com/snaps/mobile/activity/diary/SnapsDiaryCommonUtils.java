package com.snaps.mobile.activity.diary;

import android.content.Context;
import android.graphics.Color;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ysjeong on 16. 4. 7..
 */
public class SnapsDiaryCommonUtils {
    private static final String TAG = SnapsDiaryCommonUtils.class.getSimpleName();
    public static final String TAG_DATE_PICKER_NAME = "Datepickerdialog";

    public static void showCalendar(FragmentActivity activity, DatePickerDialog.OnDateSetListener listener, DatePickerDialog picker) {
        if(activity == null || listener == null || (picker != null && picker.isVisible())) return;

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if (writeInfo.getDate() == null || writeInfo.getDate().length() < 1) {
            Calendar now = Calendar.getInstance();
            picker = DatePickerDialog.newInstance(
                    listener,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            picker = DatePickerDialog.newInstance(
                    listener,
                    writeInfo.getYear(),
                    writeInfo.getMonth() - 1,
                    writeInfo.getDay()
            );
        }

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(Calendar.YEAR, 2016);
        minCalendar.set(Calendar.MONTH, 0);
        minCalendar.set(Calendar.DAY_OF_MONTH, 1);

        picker.setMinDate(minCalendar);
        picker.setAccentColor(Color.parseColor("#555555"));
        picker.setOnDateSetListener(listener);
        activity.getFragmentManager().beginTransaction().add(picker, TAG_DATE_PICKER_NAME).commitAllowingStateLoss();
    }

    /**
     * 미래는 선택 못하도록..(단말기 시간 변경하면 막을 수가 없다..)
     */
    public static boolean isAllowDiaryRegisterDate(int y, int m, int d) {
        Calendar nowCalendar = Calendar.getInstance();
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(y, m, d);
        return !selectedCalendar.after(nowCalendar);
    }

    public static String convertDateForDiary(String date) {
        if (date == null || date.length() != 8) return date;

        if(date.length() == 8) {
            SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            try {
                Date originDate = originformat.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(originDate);
                return String.format("%d년 %d월 %d일 (%s)", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), getDayOfWeekString(cal.get(Calendar.DAY_OF_WEEK), false));
            } catch (ParseException e) {
                Dlog.e(TAG, e);
            }
        }
        return date;
    }

    public static String convertRegisteredDateForDiary(String date) {
        if (date == null) return "";

        //20160707 형태라면..
        if (date.length() == 8)
            date = date + " 00:00";

        //20160707 09:54
        if (date.length() == 14) {
            SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.getDefault());
            try {
                Date originDate = originformat.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(originDate);
                return String.format("등록일 %02d/%02d %02d:%02d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            } catch (ParseException e) {
                Dlog.e(TAG, e);
            }
        }

        return "";
    }

    public static String getDayOfWeekString(int dayOfWeek, boolean isEnglish) {

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return isEnglish ? "MONDAY" : "월";
            case Calendar.TUESDAY:
                return isEnglish ? "TUESDAY" : "화";
            case Calendar.WEDNESDAY:
                return isEnglish ? "WEDNESDAY" : "수";
            case Calendar.THURSDAY:
                return isEnglish ? "THURSDAY" : "목";
            case Calendar.FRIDAY:
                return isEnglish ? "FRIDAY" : "금";
            case Calendar.SATURDAY:
                return isEnglish ? "SATURDAY" : "토";
            case Calendar.SUNDAY:
                return isEnglish ? "SUNDAY" : "일";

            default:
                break;
        }
        return "";
    }

    public static String getUserProfileCacheFilePath(Context context) {
        if(context == null) return null;
        return Config.getExternalCacheDir(context) + "/snaps/effect/";
    }

    public static String getUserProfileCacheFileName(Context context, boolean isFullPath) {
        try {
            if(isFullPath)
                return Config.getExternalCacheDir(context) + "/snaps/effect/profile_"  + SnapsLoginManager.getUUserNo(context) + ".jpg";
            else
                return "profile_" + SnapsLoginManager.getUUserNo(context) + ".jpg";
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return "";
        }
    }
}
