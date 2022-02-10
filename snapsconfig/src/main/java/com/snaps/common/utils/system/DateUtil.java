package com.snaps.common.utils.system;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ListView;

import com.snaps.common.R;
import com.snaps.common.data.interfaces.DateMonthPickerSelectListener;
import com.snaps.common.data.interfaces.DatePickerSelectListener;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by ysjeong on 2017. 8. 2..
 */

public class DateUtil {
    private static final String TAG = DateUtil.class.getSimpleName();

    public static final int CALENDAR_MONTH_DEFAULT_INDEX = -1;

    public static String[] createDateRangeItem(Context context) throws Exception {
        String[] items = new String[25];

        LinkedList<String> inputItems = new LinkedList<>();
        inputItems = appendPrevYearsItems(inputItems);
        inputItems = appendCurrentYearsItems(inputItems);
        inputItems = appendNextYearsItems(inputItems);

        int index = 0;
        while (!inputItems.isEmpty()) {
            if (items.length <= index) {
                break;
            }
            items[index++] = inputItems.poll();
        }

        return items;
    }

    public static AlertDialog showDateMonthPickerDialog(Context context,
                                                 int defaultSelect,
                                                 final DateMonthPickerSelectListener dateMonthPickerSelectListener) {
        try {
            final String[] items = createDateRangeItem(context);

            AlertDialog.Builder builder = new AlertDialog.Builder( context );

            if (defaultSelect == CALENDAR_MONTH_DEFAULT_INDEX) {
                defaultSelect = getCurrentYearMonthIndexOnItem(items);
            }

            builder.setSingleChoiceItems(items, defaultSelect, new DialogInterface.OnClickListener() {
                @Override
                public void onClick (DialogInterface dialog, int which) {
                    try {
                        if (dateMonthPickerSelectListener != null) {
                            dateMonthPickerSelectListener.onDateMonthSelected(which, items[which]);
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                    dialog.dismiss();
                }
            } );
            builder.setTitle(R.string.selectMonthStart);
            builder.setCancelable( true );
            AlertDialog dialog = builder.create();
            dialog.show();

            try {
                setSelectedItemCenterPosition(context, dialog, defaultSelect);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
            return dialog;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    private static int getCurrentYearMonthIndexOnItem(String[] items) {
        if (items == null) return 0;
        String currentYearMonthStr = getCurrentYearMonthStr();
        if (StringUtil.isEmpty(currentYearMonthStr)) return 0;
        for (int ii = 0; ii < items.length; ii++) {
            String item = items[ii];
            if (currentYearMonthStr.equalsIgnoreCase(item)) return ii;
        }
        return 0;
    }

    private static LinkedList<String> appendPrevYearsItems(LinkedList<String> inputItems) throws Exception {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get( Calendar.YEAR )-1;
        int month = calendar.get( Calendar.MONTH ) + 1;

        for (int ii = month;  ii <= 12; ii++) {
            inputItems.add(String.format("%d-%s", year, (ii > 9 ? String.valueOf(ii) : ("0" + ii))));
        }

        return inputItems;
    }

    private static LinkedList<String> appendCurrentYearsItems(LinkedList<String> inputItems) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR );

        for (int ii = 1; ii <= 12; ii++) {
            inputItems.add(String.format("%d-%s", year, (ii > 9 ? String.valueOf(ii) : ("0" + ii))));
        }
        return inputItems;
    }

    private static String getCurrentYearMonthStr() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        return String.format("%d-%s", year, (month > 9 ? String.valueOf(month) : ("0" + month)));
    }

    private static LinkedList<String> appendNextYearsItems(LinkedList<String> inputItems) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR ) + 1;
        int month = calendar.get( Calendar.MONTH ) + 1;

        for (int ii = 1; ii <= month; ii++) {
            inputItems.add(String.format("%d-%s", year, (ii > 9 ? String.valueOf(ii) : ("0" + ii))));
        }
        return inputItems;
    }

    public static void showDatePickerDialog(Context context, final DatePickerSelectListener datePickerSelectListener) throws Exception {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (datePickerSelectListener != null) {
                    datePickerSelectListener.onDateSelected(year, monthOfYear, dayOfMonth);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) );
        dialog.show();
    }

    private static void setSelectedItemCenterPosition(Context context, AlertDialog alertDialog, int defaultSelect) throws Exception {
        ListView listView = alertDialog.getListView();
        int listHeight = getCurrentScreenHeight(context);
        int rowHeight = 0;
        try {
            rowHeight = calculateHeight(listView);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (rowHeight < 1)
            rowHeight = convertDPtoPX(context, 50);

        listView.setSelectionFromTop(defaultSelect, listHeight/2 - rowHeight);
    }

    public static int getCurrentScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    private static int calculateHeight(ListView list) throws Exception {
        View childView = list.getAdapter().getView(0, null, list);
        childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return childView.getMeasuredHeight();
    }

    private static int convertDPtoPX(Context context, int dp) {
        try {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context));
        } catch (NullPointerException e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null || context.getResources() == null) return null;

        if (Config.isWQHDResolutionDevice()) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            displayMetrics.density = 4.f;
            return displayMetrics;
        }

        return context.getResources().getDisplayMetrics();
    }

    public static boolean isValidSmartSnapsDate(long time) {
        if (time < 1) return false;

        try {
            Calendar current = Calendar.getInstance();
            int currentYear = current.get(Calendar.YEAR );

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new java.util.Date(time));
            int year = calendar.get( Calendar.YEAR );
            return year >= 1980 && year <= currentYear;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    public static boolean isAfterTwoWeek(Calendar cal) {
        return calDay(cal, Calendar.getInstance());

    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isTodayDateWithSavedDate(String savedDate) {
        String todayStr = getTodayDate();
        return !StringUtil.isEmpty(savedDate) && todayStr.equals(savedDate);
    }

    public static String getTodayDate() {
        // 현재 시간을 msec으로 구한다.
        long now = System.currentTimeMillis();

        // 현재 시간을 저장 한다.
        Date date = new Date(now);

        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

        String strCurYear = CurYearFormat.format(date);
        String strCurMonth = CurMonthFormat.format(date);
        String strCurDay = CurDayFormat.format(date);

        String dateToday = strCurYear + strCurMonth + strCurDay;
        return dateToday;
    }

    public static String getTodayDateWithFormat(String format) {
        long now = System.currentTimeMillis();
        Date currentDate = new Date(now);
        //YYYYMMDDHHMMSS.SSS
        SimpleDateFormat curYearFormat = new SimpleDateFormat(format);
        return curYearFormat.format(currentDate);
    }

    public static String getTodayFullDate() {
        // 현재 시간을 msec으로 구한다.
        long now = System.currentTimeMillis();

        // 현재 시간을 저장 한다.
        Date date = new Date(now);

        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat CurHoursFormat = new SimpleDateFormat("HH");
        SimpleDateFormat CurMinFormat = new SimpleDateFormat("mm");

        String strCurYear = CurYearFormat.format(date);
        String strCurMonth = CurMonthFormat.format(date);
        String strCurDay = CurDayFormat.format(date);
        String strCurHours = CurHoursFormat.format(date);
        String strCurMin = CurMinFormat.format(date);

        String dateToday = String.format("%s/%s/%s %s:%s", strCurYear, strCurMonth, strCurDay, strCurHours, strCurMin);
        return dateToday;
    }

    public static String getDurationBreakdown(long millis) {
        if(millis < 0) return "";

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return(sb.toString());
    }

    public static boolean calDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        long diff = cal2.getTimeInMillis() - cal2.getTimeInMillis();
        long diffDays = diff / (24 * 60 * 60 * 1000 );
        if(diffDays >=14){
            return true;
        }else{
            return false;
        }

    }
}
