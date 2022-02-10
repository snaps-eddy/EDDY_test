package com.snaps.mobile.activity.book;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;

import com.snaps.common.storybook.IOnStoryDataLoadListener;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookRecorder.ControlFixInfo;
import com.snaps.mobile.utils.ui.CalcViewRectUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class StoryBookCommonUtil {
    private static final String TAG = StoryBookCommonUtil.class.getSimpleName();

    public static void setTextControlAtrr(Context context, Object obj, ArrayList<SnapsControl> arControls, int pos, ControlFixInfo fixInfo) {

        if (obj == null || arControls == null || arControls.size() <= pos)
            return;
        SnapsControl c = arControls.get(pos);

        if (!(c instanceof SnapsTextControl))
            return;

        SnapsTextControl textControl = ((SnapsTextControl) c);

        if (obj instanceof Integer) {
            int number = (Integer) obj;
            textControl.text = String.valueOf(number);
        } else if (obj instanceof String) {
            String str = (String) obj;
            textControl.text = str;
        }

        if (fixInfo != null) {
            if (fixInfo.isTextHalfPositionUp)
                textControl.setY(String.valueOf(textControl.getIntY() - (Float.parseFloat(textControl.format.fontSize) / 2)));

            if (fixInfo.fixPosY != 0)
                textControl.setY(String.valueOf(textControl.getIntY() + fixInfo.fixPosY));

            if (fixInfo.fixPosX != 0)
                textControl.setX(String.valueOf(textControl.getIntX() + fixInfo.fixPosX));

            if (fixInfo.fixTextSize != 0)
                textControl.format.fontSize = String.valueOf(Float.parseFloat(textControl.format.fontSize) + fixInfo.fixTextSize);
        }
    }

    // 텍스트 사이즈에 따라 영역 크기를 변경하는 함수..
    public static void setResizeTextControlAtrr(Context context, SnapsTextControl textControl) {
        setResizeTextControlAtrr(context, textControl, true);
    }

    // 텍스트 사이즈에 따라 영역 크기를 변경하는 함수..
    public static void setResizeTextControlAtrr(Context context, SnapsTextControl textControl, boolean isChangeXpos) {
        Rect rect = CalcViewRectUtil.getTextControlRect2(context, textControl.text, textControl.format.fontSize, 200, textControl.format.fontFace, 1.f);
        int w = textControl.getIntWidth();
        textControl.width = rect.width() + "";
        if (isChangeXpos)
            textControl.x = (textControl.getIntX() + w - rect.width()) + "";
    }

    // 스티커를 텍스트의 왼쪽에 위치하도록 하기 위해 offset을 구한다.
    public static String getStikerPosXOfTextLeft(Context context, SnapsTextControl textControl, SnapsClipartControl stiker, int marginDp) {
        if (textControl == null || stiker == null)
            return "";

        int fontSize = (int) UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize));
        int offset = (int) ((Float.parseFloat(textControl.x) + Float.parseFloat(textControl.width)) - (textControl.text.length() * fontSize));
        int margin = UIUtil.convertPXtoDP(context, marginDp);
        return String.valueOf(offset - (Integer.parseInt(stiker.width) + margin));
    }

    // 스티커를 텍스트의 오른쪽에 위치하도록 하기 위해 offset을 구한다.
    public static String getPosXOfTextRight(Context context, SnapsTextControl textControl, int marginDp) {
        if (textControl == null)
            return "";

        int offset = 0;
        int margin = UIUtil.convertPXtoDP(context, marginDp);

        int fontSize = (int) UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize));
        offset = (int) (Float.parseFloat(textControl.x) + (textControl.text.length() * fontSize));

        return String.valueOf(offset + margin);
    }

    public static String getPosXOfControlRight(Context context, SnapsControl control, int marginDp) {
        if (control == null)
            return "";

        int offset = 0;
        int margin = UIUtil.convertPXtoDP(context, marginDp);

        offset = (int) (Float.parseFloat(control.x) + Float.parseFloat(control.width));

        return String.valueOf(offset + margin);
    }

    public static int getPosXOfTextRightUnitPt(SnapsControl targetTextControl, SnapsTextControl offsetTextControl) {
        if (offsetTextControl == null || targetTextControl == null)
            return 0;

        int fontSize = (int) Float.parseFloat(offsetTextControl.format.fontSize);
        int offset = (int) (Float.parseFloat(offsetTextControl.x) + (offsetTextControl.text.length() * (fontSize * .75f)));

        return -targetTextControl.getIntX() + offset;
    }

    // 스티커를 텍스트의 오른쪽에 위치하도록 하기 위해 offset을 구한다.
    public static float getTextControlWidth(SnapsTextControl textControl) {
        if (textControl == null)
            return 0;

        int fontSize = (int) Float.parseFloat(textControl.format.fontSize);
        return (textControl.text.length() * fontSize);
    }

    public static void fixTextControlAtrr(SnapsTextControl textControl, ControlFixInfo fixInfo) {
        if (fixInfo == null)
            return;

        if (fixInfo.isTextHalfPositionUp)
            textControl.setY(String.valueOf(textControl.getIntY() - (Float.parseFloat(textControl.format.fontSize) / 2)));
        if (fixInfo.fixPosY != 0)
            textControl.setY(String.valueOf(textControl.getIntY() + fixInfo.fixPosY));
        if (fixInfo.fixPosX != 0)
            textControl.setX(String.valueOf(textControl.getX() + fixInfo.fixPosX));
        if (fixInfo.fixTextSize != 0)
            textControl.format.fontSize = String.valueOf(Float.parseFloat(textControl.format.fontSize) + fixInfo.fixTextSize);
    }

    public static void fixControlAtrr(SnapsControl control, ControlFixInfo fixInfo) {

        if (fixInfo == null)
            return;

        if (fixInfo.isTextHalfPositionUp)
            if (control instanceof SnapsTextControl)
                control.setY(String.valueOf(control.getIntY() - (Float.parseFloat(((SnapsTextControl) control).format.fontSize) / 2)));
        if (fixInfo.fixPosY != 0)
            control.setY(String.valueOf(control.getIntY() + fixInfo.fixPosY));
        if (fixInfo.fixPosX != 0)
            control.setX(String.valueOf(control.getX() + fixInfo.fixPosX));
        if (fixInfo.fixTextSize != 0) {
            if (control instanceof SnapsTextControl) {
                ((SnapsTextControl) control).format.fontSize = String.valueOf(Float.parseFloat(((SnapsTextControl) control).format.fontSize) + fixInfo.fixTextSize);
            }
        }
    }

    public static void fixTextControlAtrr(Object obj, ArrayList<SnapsControl> arControls, int pos, ControlFixInfo fixInfo) {
        if (obj == null || arControls == null || arControls.size() <= pos)
            return;

        SnapsControl c = arControls.get(pos);
        if (!(c instanceof SnapsTextControl))
            return;

        SnapsTextControl textControl = ((SnapsTextControl) c);

        if (obj instanceof Integer) {
            int number = (Integer) obj;
            textControl.text = String.valueOf(number);
        } else if (obj instanceof String) {
            String str = (String) obj;
            textControl.text = str;
        }

        fixTextControlAtrr(textControl, fixInfo);
    }

    public static String convertTwoStylePeriod(String period) {

        if (period == null || period.length() < 1 || !period.contains("-"))
            return null;

        StringBuffer sbResult = new StringBuffer();
        try {
            String[] arPeriod = period.split("-");

            if (arPeriod != null & arPeriod.length >= 2) {
                String[] arStart = arPeriod[0].split("\\.");
                String[] arEnd = arPeriod[1].split("\\.");

                sbResult.append(arStart[0]).append(" ").append(convertMonthStr(arStart[1]));
                sbResult.append(" - ");
                sbResult.append(arEnd[0]).append(" ").append(convertMonthStr(arEnd[1]));
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return sbResult.toString();
    }

    public static String convertMonthStr(String month) {
        if (month == null)
            return "";

        try {
            int iMonth = Integer.parseInt(month);
            switch (iMonth) {
                case 1:
                    return "JANUARY";
                case 2:
                    return "FEBRUARY";
                case 3:
                    return "MARCH";
                case 4:
                    return "APRIL";
                case 5:
                    return "MAY";
                case 6:
                    return "JUNE";
                case 7:
                    return "JULY";
                case 8:
                    return "AUGUST";
                case 9:
                    return "SEPTEMBER";
                case 10:
                    return "OCTOBER";
                case 11:
                    return "NOVEMBER";
                case 12:
                    return "DECEMBER";
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return month;
    }

    public static String convertAM_PMStr(int am_pm) {
        switch (am_pm) {
            case Calendar.AM:
                return "AM";
            case Calendar.PM:
                return "PM";
        }
        return "AM";
    }

    public static String getObjToStr(Object obj) {
        if (obj == null)
            return "";

        if (obj instanceof String)
            return (String) obj;

        return "";
    }

    public static ArrayList<Integer> sortByValue(final HashMap<Integer, Integer> map) {
        List<Integer> list = new ArrayList<Integer>();
        list.addAll(map.keySet());

        Collections.sort(list, new Comparator<Integer>() {

            @Override
            public int compare(Integer lhs, Integer rhs) {
                Integer v1 = map.get(lhs);
                Integer v2 = map.get(rhs);
                return v1.compareTo(v2);
            }

        });
        Collections.reverse(list); // 주석시 오름차순
        ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.addAll(list);
        return arr;
    }

    public static void setTextControlAuraFontSizeAtrr(SnapsTextControl control, float fontSize) {
        control.format.auraOrderFontSize = fontSize + "";
    }

    public static void showErrMsg(Activity act, Context con, int errorCode) {
        switch (errorCode) {
            case IOnStoryDataLoadListener.ERR_CODE_INVALID_PERIOD:
                MessageUtil.toast(con, con.getString(R.string.kakao_book_make_err_is_not_exist_page));
                act.finish();
                break;
            case IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_USER_PROFILE:
                MessageUtil.toast(con, con.getString(R.string.kakao_book_make_err_failed_get_user_profile));
                act.finish();
                break;
            case IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_LIST:
                MessageUtil.toast(con, con.getString(R.string.kakao_book_make_err_failed_get_list));
                act.finish();
                break;
            case IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_DETAIL:
                MessageUtil.toast(con, con.getString(R.string.kakao_book_make_err_failed_get_detail));
                act.finish();
                break;

            default:
                MessageUtil.toast(con, con.getString(R.string.kakao_data_loading_fail));
                break;
        }
    }
}
