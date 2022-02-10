package com.snaps.mobile.tutorial.new_tooltip_tutorial;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.view.Window;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.custom_tutorial.CustomTutorialView;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_PHOTOBOOK_FIND_COVER;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_SNSBOOK_FIND_COVER;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_KEYRING_TOUCH_UPLOAD_PNG_FILE;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_STAND_TOUCH_UPLOAD_PNG_FILE;

/**
 * Created by kimduckwon on 2017. 9. 15..
 */

public class SnapsTutorialUtil {
    private static final String TAG = SnapsTutorialUtil.class.getSimpleName();
    public static final int TERM_OF_TUTORIAL_NO_SHOW_10_DAYS = 10;//한번 보여주면 이 기간동안 안 보여줌.
    public static final int TERM_OF_TUTORIAL_NO_SHOW_ONLY_ONE_DAYS = 1;//한번 보여주면 이 기간동안 안 보여줌.

    private static final String EDIT_PRINT_VIEW_TUTORIAL = "editPrintViewTutorial";
    private static final String EDIT_VIEW_TUTORIAL = "editViewTutorial";
    private static final String PHOTO_BOOK_COVER = "photobookCover";
    private static final String SNS_BOOK_COVER = "snsbookCover";
    private static final int DELAY_TIME = 60 * 3;
    private static final int TOOLTIP_SHOW_DELAY_TIME = 300;
    private static Timer threeMinTimer = null;
    private static Handler threeMinHandler = null;
    private static int time = 0;
    private static ArrayList<CreateTooltipView> tooltipList = null;

    //튜토리얼이 보여지는 조건이 만족하는지
    public static boolean isShowConditionSatisfaction(Activity activity, ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType, int term) {
        if (tutorialType == null) return false;

        String lastShownDate = ImageSelectUtils.getShownDatePhoneFragmentTutorial(activity, tutorialType);
        if (StringUtil.isEmpty(lastShownDate)) return true;

        try {
            long lLastShownDate = Long.parseLong(lastShownDate);

            Calendar calendarCurrent = Calendar.getInstance();

            Calendar calendarLastShownDate = Calendar.getInstance();
            calendarLastShownDate.setTimeInMillis(lLastShownDate);
            calendarLastShownDate.add(Calendar.DAY_OF_MONTH, term); //마지막으로 보여지고 나서 일정 기간 동안 보여지지 않는다.

            if (calendarCurrent.before(calendarLastShownDate)) {
                ImageSelectUtils.setShownDatePhoneFragmentTutorial(activity, tutorialType); //마지막으로 본 날짜 연장
                return false;
            }

        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        return true;
    }

    public static void showTooltip(final @NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute) {
        if (SmartSnapsManager.isFirstSmartAreaSearching()) return;
        try {
            if(attribute.getTargetView() == null) return;
                if (checkTimeTooltip(attribute)) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createTooltipView(activity, attribute);
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    }, TOOLTIP_SHOW_DELAY_TIME);
                }
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private static void createTooltipView(Activity activity, SnapsTutorialAttribute attribute) throws Exception {
        if(tooltipList == null) {
            tooltipList = new ArrayList<CreateTooltipView>();
        }
        tooltipList.add(CreateTooltipView.createTooltipView(activity,activity,attribute));
    }

    public static void showTooltipAlways(final @NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute) {
        try {
            if(attribute.getTargetView() == null) return;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tooltipList == null) {
                            tooltipList = new ArrayList<CreateTooltipView>();
                        }
                        tooltipList.add(CreateTooltipView.createTooltipView(activity, activity, attribute));
                    }
                }, TOOLTIP_SHOW_DELAY_TIME);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showTooltipDialog(final @NonNull Window window, final @NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute) {
        try {
            if(attribute.getTargetView() != null) return;
                if (checkTimeTooltip(attribute)) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CreateTooltipView.createTooltipView(window, activity, attribute);
                        }
                    }, TOOLTIP_SHOW_DELAY_TIME);
                }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showToast(final @NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute) {
        try {
            if(checkTimeToast(attribute)) {
                Handler handler = new Handler(activity.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MessageUtil.longToast(activity,attribute.getText());
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void clearTooltip() {
        if (tooltipList == null || tooltipList.size() == 0) return;

        for(CreateTooltipView item : tooltipList) {
            if (item == null) continue;
            item.clearTooltipView();
        }

        tooltipList.clear();
    }

    public static void showCustomTutorial(@NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute, final CustomTutorialView.CloseListener closeListener) {
        try {
            if(checkTimeForCustomTutorial(attribute)) {
                CustomTutorialView.createTutorialView(activity, attribute, closeListener).show();
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(closeListener != null){
                            closeListener.close();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showGifView(@NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute) {
        try {
            if(checkTimeGIF(attribute)) {
                GifTutorialView.createGifView(activity, attribute).show();
            }
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showGifView(@NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute, final GifTutorialView.CloseListener closeListener) {
        try {
            if(checkTimeGIF(attribute)) {
                GifTutorialView.createGifView(activity, attribute, closeListener).show();
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(closeListener != null){
                            closeListener.close();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void showGifViewAlways(@NonNull Activity activity, final @NonNull SnapsTutorialAttribute attribute, final GifTutorialView.CloseListener closeListener) {
        try {
                GifTutorialView.createGifView(activity, attribute,closeListener).show();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static boolean checkTimeGIF(SnapsTutorialAttribute attribute) {
        String msgKey = attribute.getGifType().toString();

        Context context = ContextUtil.getContext();
        if (context == null) return false;

        long lastShownTime = Setting.getLong(context, msgKey);
        Calendar lastShownCalendar = Calendar.getInstance();
        lastShownCalendar.setTimeInMillis(lastShownTime);

        switch (attribute.getGifType()) {
            case RECOMMEND_BOOK_MAIN_LIST_PINCH_ZOOM:
                if (lastShownTime == 0) {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                } else if(DateUtil.isToday(lastShownCalendar)) {
                    return  false;
                } else {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                }
            case KT_BOOK_EDITOR:
                //KT 북
                if (lastShownTime == 0) {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                } else if(DateUtil.isToday(lastShownCalendar)) {
                    return false;
                } else {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                }
            case ACRYLIC_KEYING_EDITOR:
                if (lastShownTime == 0) {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                } else if(DateUtil.isToday(lastShownCalendar)) {
                    return false;
                } else {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                }
            case ACRYLIC_STAND_EDITOR:
                if (lastShownTime == 0) {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                } else if(DateUtil.isToday(lastShownCalendar)) {
                    return false;
                } else {
                    Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
                    return true;
                }
            default:
                if (lastShownTime == 0 || DateUtil.isAfterTwoWeek(lastShownCalendar)) {
                    Setting.set(context, msgKey, System.currentTimeMillis());
                    return true;
                }
                break;
        }

        return false;
    }

    private static boolean isAlwaysShowTutorialType(SnapsTutorialAttribute.eCustomTutorialType tutorialType) {
        if (tutorialType == null) return false;

        switch (tutorialType) {
            case RECOMMEND_BOOK_IMAGE_SELECT:
                return true;
        }
        return false;
    }

    private static boolean checkTimeForCustomTutorial(SnapsTutorialAttribute attribute) {
        if (attribute == null) return false;

        if (isAlwaysShowTutorialType(attribute.getCustomTutorialType())) return true;

        String msgKey = attribute.getCustomTutorialType().toString();

        Context context = ContextUtil.getContext();
        long lastShownTime = context != null ? Setting.getLong(context, msgKey) : 0;

        Calendar lastShownCalendar = Calendar.getInstance();
        lastShownCalendar.setTimeInMillis(lastShownTime);
//        if (lastShownTime == 0 || DateUtil.isAfterTwoWeek(lastShownCalendar)) {
//            if (context != null)
//                Setting.set(context, msgKey, System.currentTimeMillis());
//            return true;
//        }else{
//            return false;
//        }
        if (lastShownTime == 0) {
            Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
            return true;
        }else if(DateUtil.isToday(lastShownCalendar)) {
            return  false;
        }else {
            Setting.set(ContextUtil.getContext(), msgKey, System.currentTimeMillis());
            return true;
        }
    }

    private static boolean checkTimeTooltip(SnapsTutorialAttribute attribute) {
        String msgKey = "";

        if(attribute.getTutorialId() != null && attribute.getTutorialId() == TUTORIAL_ID_PHOTOBOOK_FIND_COVER){
            msgKey = PHOTO_BOOK_COVER;
        }else if(attribute.getTutorialId() != null && attribute.getTutorialId() == TUTORIAL_ID_SNSBOOK_FIND_COVER){
            msgKey = SNS_BOOK_COVER;
        }else if(attribute.getTutorialId() != null && attribute.getTutorialId() == TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_KEYRING_TOUCH_UPLOAD_PNG_FILE){
            //Ben : 기존에 이딴식으로 만들어져 있어서 나도 그냥 숟가락 추가
            msgKey = "TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_KEYRING_TOUCH_UPLOAD_PNG_FILE";
        }else if(attribute.getTutorialId() != null && attribute.getTutorialId() == TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_STAND_TOUCH_UPLOAD_PNG_FILE){
            //Ben : 기존에 이딴식으로 만들어져 있어서 나도 그냥 숟가락 추가
            msgKey = "TUTORIAL_ID_TOOLTIP_NAME_ACRYLIC_STAND_TOUCH_UPLOAD_PNG_FILE";
        }else{
            msgKey =attribute.getTargetView().getId()+"";
        }

        Context context = ContextUtil.getContext();
        long lastShownTime = context != null ? Setting.getLong(context, msgKey) : 0;
        Calendar lastShownCalendar = Calendar.getInstance();
        lastShownCalendar.setTimeInMillis(lastShownTime);
        if (lastShownTime == 0) {
            if (context != null) {
                Setting.set(context, msgKey, System.currentTimeMillis());
            }
            return true;
        } else if( DateUtil.isToday(lastShownCalendar)) {
            return false;
        } else {
            if (context != null) {
                Setting.set(context, msgKey, System.currentTimeMillis());
            }
            return true;
        }
    }

    private static boolean checkTimeToast(SnapsTutorialAttribute attribute) {
        String msgKey = attribute.getTutorialId()+"";

        Context context = ContextUtil.getContext();
        long lastShownTime = context != null ? Setting.getLong(context, msgKey) : 0;
        Calendar lastShownCalendar = Calendar.getInstance();
        lastShownCalendar.setTimeInMillis(lastShownTime);
        if (lastShownTime == 0) {
            if (context != null) {
                Setting.set(context, msgKey, System.currentTimeMillis());
            }
            return true;
        }else if( DateUtil.isToday(lastShownCalendar)) {
            if (context != null) {
                Setting.set(context, msgKey, System.currentTimeMillis());
            }
            return false;
        }else{
            return true;
        }
    }

    public static void checkTimeThreeMinDelay(final OnThreeMinListener listener) {
        try {
                if (threeMinHandler != null) {
                    threeMinHandler = null;
                }

                threeMinHandler = new Handler();
                threeMinHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.threeMin();
                        threeMinHandler = null;
                    }
                },    3 * 60 * 1000);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public interface OnThreeMinListener {
        void threeMin();
    }
}
