package com.snaps.mobile.activity.home.model;

import android.os.Build;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.home.SharedPreferenceRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;

import errorhandle.logger.Logg;

public class SnapsWebAPI {
    private static final String TAG = SnapsWebAPI.class.getSimpleName();
    public String getFirstPageURL(SharedPreferenceRepository spRepository, String deviceUUID) {

        String firstPageStr = getWebViewDomainForLanguage(spRepository, false) + getFirstPageParameters(spRepository, deviceUUID);

        boolean isUserChangeLanguage = spRepository.getBoolean(Const_VALUE.KEY_IS_USER_CHANGE_LANGUAGE, false);
        boolean hasSeenTutorial = spRepository.getBoolean(Const_VALUE.HAS_SEEN_WEB_TUTORIAL, false);

        String userId = spRepository.getString(Const_VALUE.KEY_SNAPS_USER_ID, "");
        String userPWD = spRepository.getString(Const_VALUE.KEY_SNAPS_USER_PWD, "");

        boolean hasUerInfo = userId != null && userId.length() > 0 && userPWD != null && userPWD.length() > 0;

        if (isUserChangeLanguage) {
            if (hasUerInfo) {
                firstPageStr += getReloginParameters(spRepository);

            } else {
                //Logout 했다고 가정.
                firstPageStr = getWebViewDomainForLanguage(spRepository, false) + "/callback/kick-out" + getFirstPageParameters(spRepository, deviceUUID);
            }

        } else {
            if (!hasSeenTutorial) {
                if (hasUerInfo) {
                    firstPageStr += getReloginParameters(spRepository);
                }

                firstPageStr += "&isFirst=Y";
                spRepository.set(Const_VALUE.HAS_SEEN_WEB_TUTORIAL, true);
            }
        }

        spRepository.set(Const_VALUE.KEY_IS_USER_CHANGE_LANGUAGE, false);
        Dlog.d("getFirstPageURL() first page:" + firstPageStr);

        return firstPageStr;
    }

    public String getFirstPageParameters(SharedPreferenceRepository spRepository, String deviceUUID) {
        String regId = spRepository.getString(Const_VALUE.KEY_GCM_REGID);
        return "?channel=ANDROID&version=" + Config.getAPP_VERSION() + "&osVersion=" + Build.VERSION.RELEASE + "&device=" + Build.MODEL + "&deviceToken=" + regId + "&deviceUUID=" + deviceUUID;
    }

    private String getReloginParameters(SharedPreferenceRepository spRepository) {
        String userId = spRepository.getString(Const_VALUE.KEY_SNAPS_USER_ID, "");
        String userPWD = spRepository.getString(Const_VALUE.KEY_SNAPS_USER_PWD, "");
        return "&userId=" + StringUtil.getURLEncode(userId, "UTF-8") + "&userPassword=" + StringUtil.getURLEncode(userPWD, "UTF-8");
    }

    public String getWebViewDomainForLanguage(SharedPreferenceRepository spRepository, boolean isCart) {
        String currentLang = spRepository.getString(Const_VALUE.KEY_APPLIED_LANGUAGE);

        if (StringUtil.isEmpty(currentLang)) {
            currentLang = Locale.getDefault().getLanguage();
        }

        return SnapsAPI.FRONTEND_DOMAIN(currentLang, isCart);
    }

    public String makeUrlFromJson(JSONObject jsonObject, boolean bPreview) {
        StringBuilder snapsUrl = new StringBuilder(bPreview ? "snapsapp://preview?dumy=&" : "snapsapp://selectProduct?");
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {

                Object value = jsonObject.get(key);
                snapsUrl.append(key).append("=").append((value == null || value.toString().equalsIgnoreCase("null")) ? "" : value.toString()).append("&");

            } catch (JSONException e) {
                Dlog.e(TAG, e);
            }
        }
        return snapsUrl.toString();
    }
}
