package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventForKakaoHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventForKakaoHandler.class.getSimpleName();

    public SnapsWebEventForKakaoHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        return forKakao(urlData);
    }

    /***
     * 카카오 친구 추가..
     *
     * @param urlData
     * @return
     */
    boolean forKakao(final HashMap<String, String> urlData) {
        if (Config.isSnapsBitween())
            return false;

        String text = urlData.get("text");
        String openurl = urlData.get("url");
        String urlText = urlData.get("urlText");
        String imgUrl = urlData.get("imageurl");
        String imgWidth = urlData.get("width");
        String imgHeight = urlData.get("height");
        String isRunapp = urlData.get("isrunapp");
        String eventcode = urlData.get("eventcode");
        String excuteParam = urlData.get("executeParam");
        if (openurl != null) {
            try {
                if (openurl != null)
                    openurl = URLDecoder.decode(openurl, "utf-8");
                if (text != null)
                    text = URLDecoder.decode(text, "utf-8");
                if (urlText != null)
                    urlText = URLDecoder.decode(urlText, "utf-8");
                if (imgUrl != null)
                    imgUrl = URLDecoder.decode(imgUrl, "utf-8");
                if (isRunapp != null)
                    isRunapp = URLDecoder.decode(isRunapp, "utf-8");
                if (eventcode != null)
                    eventcode = URLDecoder.decode(eventcode, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
            }
        }

        if (!Config.isSnapsBitween()) {
            String errMsg = "";
            kakao = SnsFactory.getInstance().queryIntefaceKakao();

            if (kakao != null) {
                if ((errMsg = kakao.createKakaoInstance(activity)) != null) {
                    alert(errMsg);
                    return true;
                }

            }
        }

        if(excuteParam != null) {
            kakao.sendInviteMessage(activity, text, openurl, urlText, imgUrl, imgWidth, imgHeight, isRunapp, excuteParam);
            return true;
        }

        // 카카오 친구 초대...
        if (isRunapp == null)
            kakao.sendInviteMessage(activity, text, openurl, urlText, imgUrl, imgWidth, imgHeight, isRunapp);
        else {
            //카카오친구초대 개선
            String parameters = getKakaoEventParamsFromUrlData(urlData, isRunapp);
            kakao.sendInviteFriend( activity, text, urlText, imgUrl, imgWidth, imgHeight, parameters );
        }

        return true;
    }

    private String getKakaoEventParamsFromUrlData(HashMap<String, String> urlData, String isRunapp) {
        if (urlData == null || isRunapp == null) return null;

        //카카오친구초대 개선
        String userNo = urlData.get("params");
        if (userNo == null) {
            // userNo가 없으면 직접 가져와서 넘긴다 sendNo:34823226||code:B0022303||eventCode:314013
            String userno = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NO);
            userNo = String.format("sendNo:%s||code:B0022303||eventCode:314013",userno);
        }

        String parameters = isRunapp.equals("true") ? String.format("sendno=%s", userNo) : "";
        try {
            if( parameters.length() > 0 ) {
                parameters += "&";
                parameters = URLEncoder.encode(parameters, "utf-8");
            }
            parameters += urlData.get( "campaign" );
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
        }

        return parameters;
    }

    void alert(String message) {
        MessageUtil.alert(activity, activity.getString(R.string.app_name), message);
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
