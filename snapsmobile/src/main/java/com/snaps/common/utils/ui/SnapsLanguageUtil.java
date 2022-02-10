package com.snaps.common.utils.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.autosave.AutoSaveManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import errorhandle.CatchSyncker;
import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2017. 8. 16..
 */

public class SnapsLanguageUtil {

    public static void handleChangedConfiguration(Context context, Configuration newConfig) {
        String appliedLang = Setting.getString(context, Const_VALUE.KEY_APPLIED_LANGUAGE);
        String deviceSettingLanguage = Setting.getString(context, Const_VALUE.KEY_LANGUAGE);

        if (!Config.isDevelopVersion() && CatchSyncker.getInstance() != null && StringUtil.isEmpty(appliedLang) && !newConfig.locale.getLanguage().equalsIgnoreCase(deviceSettingLanguage)) { // 앱내 설정에서 변경했을 경우, 여기를 타지 않으므로, 항상 앱을 종료 한다. 디바이스 언어 설정을 변경하고 앱을 종료하지 않으면 기존 acitivity가 살아있다는 보장이 없음.
            String newLang = newConfig.locale.getLanguage();
            if (!newLang.equalsIgnoreCase("ko") || !newLang.equalsIgnoreCase("en") || !newLang.equalsIgnoreCase("ja")) {
//            if (!newLang.equalsIgnoreCase("ko") || !newLang.equalsIgnoreCase("ja")) {
                newLang = "ko";
            }

            CatchSyncker.getInstance().forceFinishApp();
        }
    }

    public static void checkLanguageState(Context context) {
        String language = Locale.getDefault().getLanguage();
        Setting.set(context, Const_VALUE.KEY_LANGUAGE, language);

        chooseTargetLanguage(context);
    }

    private static void chooseTargetLanguage(Context context) {
        String selectedLang = Setting.getString(context, Const_VALUE.KEY_SELECTED_LANGUAGE);
        String appliedLang = Setting.getString(context, Const_VALUE.KEY_APPLIED_LANGUAGE);
        String targetLanguage = !StringUtil.isEmpty(selectedLang) ? selectedLang : !StringUtil.isEmpty(appliedLang) ? appliedLang : Locale.getDefault().getLanguage();
        applyTargetLanguage(context, targetLanguage);
    }

    private static void applyTargetLanguage(Context context, String targetLanguage) {
        String currentLang = Setting.getString(context, Const_VALUE.KEY_APPLIED_LANGUAGE);
        if (StringUtil.isEmpty(currentLang)) {
            currentLang = Setting.getString(context, Const_VALUE.KEY_LANGUAGE);
        }

        if (!targetLanguage.equalsIgnoreCase(currentLang)) {
            initVersionData(context);
            deleteAutoSaveData();
            Setting.set(context, Const_VALUE.KEY_IS_USER_CHANGE_LANGUAGE, true);
        } else {
            Setting.set(context, Const_VALUE.KEY_IS_USER_CHANGE_LANGUAGE, false);
        }

        String selectedLang = Setting.getString(context, Const_VALUE.KEY_SELECTED_LANGUAGE);
        UIUtil.applyLanguage(context, targetLanguage, !StringUtil.isEmpty(selectedLang));
        Setting.set(context, Const_VALUE.KEY_SELECTED_LANGUAGE, "");
    }

    private static void deleteAutoSaveData() {
        AutoSaveManager manager = AutoSaveManager.getInstance(); // 언어가 바뀌면 자동저장된 파일 지움.
        if (manager != null) {
            manager.delete();
        }
    }

    private static void initVersionData(Context context) {
        MenuDataManager.getInstance().initVersionData(context);
    }

    public static String getConvertedOldSnapsLanguageCode(String lang) {// 한국어, 일본어, 영어를 제외한 언어는 한국어로 표시.

        if (lang != null && lang.length() != 0) {
            if (lang.equalsIgnoreCase(Locale.JAPANESE.toString())) {
                return Locale.JAPANESE.toString();
            }

            if (lang.equalsIgnoreCase(Locale.ENGLISH.toString())) {
                return Locale.ENGLISH.toString();
            }
        }
        return Locale.KOREAN.toString();
    }

    public static boolean isAppliedServiceableLanguage(Context context) {
        String appliedLanguage = Setting.getString(context, Const_VALUE.KEY_APPLIED_LANGUAGE, "");

        if (appliedLanguage.equals("")) { //최초 사용자일 경우 이 값이 없다.
            String deviceLanguage = Locale.getDefault().getLanguage();
            return isServiceableLanguage(deviceLanguage);

        } else { //한번이라도 앱을 실행한 사용자.
            return isServiceableLanguage(appliedLanguage);
        }
    }

    public static boolean isServiceableLanguage(String language) {
        if (StringUtil.isEmpty(language)) return false;

        List<String> serviceableLanguageList = new ArrayList<>();
        serviceableLanguageList.add(Locale.KOREAN.getLanguage());
        serviceableLanguageList.add(Locale.JAPANESE.getLanguage());
        serviceableLanguageList.add(Locale.ENGLISH.getLanguage());
        return serviceableLanguageList.contains(language);
    }

    public static String getCurrencyStr(float value) {
        if (Config.useKorean()) {
            return new DecimalFormat("###,###").format(value) + "원";
        }

        if (Config.useJapanese()) {
            return new DecimalFormat("###,###").format(value) + "円";
        }
        return new DecimalFormat("###,###.##").format(value) + "USD";
    }
}
