package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;

import java.util.HashMap;
import java.util.Set;

import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerFactory {
    private static final String GOTO_TARGET_CODE_P0002 = "P0002";
    private static final String GOTO_TARGET_CODE_P0003 = "P0003";
    private static final String GOTO_TARGET_CODE_P0004 = "P0004";
    private static final String GOTO_TARGET_CODE_P0005 = "P0005";
    private static final String GOTO_TARGET_CODE_P0006 = "P0006";
    private static final String GOTO_TARGET_CODE_P0007 = "P0007";
    private static final String GOTO_TARGET_CODE_P0008 = "P0008";
    private static final String GOTO_TARGET_CODE_P0010 = "P0010";
    private static final String GOTO_TARGET_CODE_P0012 = "P0012";
    private static final String GOTO_TARGET_CODE_P0013 = "P0013";
    private static final String GOTO_TARGET_CODE_P0014 = "P0014";


    public static HomeIntentDataImp createHomeIntentDataImpWithIntent(Activity activity, HomeIntentHandleData intentHandleData) throws Exception {
        String gotoo = getGoTooCodeFromIntent(intentHandleData.getIntent());
        if (gotoo == null) return null;

        switch (gotoo) {
            case GOTO_TARGET_CODE_P0002 : //홈
                return new HomeIntentDataHandlerP0002(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0003 : //장바구니
                return new HomeIntentDataHandlerP0003(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0004 : //주문배송
                return new HomeIntentDataHandlerP0004(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0005 : //쿠폰
                return new HomeIntentDataHandlerP0005(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0006 : //공지사항
                return new HomeIntentDataHandlerP0006(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0007 : //이벤트 화면
                return new HomeIntentDataHandlerP0007(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0008 : // 카카오 이벤트 화면으로 이동...
                return new HomeIntentDataHandlerP0008(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0010 : // 일기 메인 화면으로 이동...
                return new HomeIntentDataHandlerP0010(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0012 : //회원가입 및 인증
                return new HomeIntentDataHandlerP0012(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0013 : //장바구니 상품 로딩
                return new HomeIntentDataHandlerP0013(activity, intentHandleData);
            case GOTO_TARGET_CODE_P0014 : //(다이렉트 웹 팝업 호출)openApp popup
                return new HomeIntentDataHandlerP0014(activity, intentHandleData);
        }

        return null;
    }

    private static String getGoTooCodeFromIntent(Intent intent) throws Exception {
        String dataString = intent.getDataString();
        SnapsLogger.appendTextLog("funcGoto : ", dataString);

        // 카카오톡에서 executeParam으로 보내 줬을때 처리를 위함.
        if (dataString != null && (dataString.contains("kakao") || dataString.contains("adbrix"))) {
            HashMap<String, String> parsed = StringUtil.parseUrl(dataString);
            if (parsed != null && !parsed.isEmpty()) {
                Set<String> keySet = parsed.keySet();
                for (String key : keySet) {
                    intent.putExtra(key, parsed.get(key));
                }
            }
        }

        if (intent.getExtras() == null)
            return null;

        return intent.getExtras().getString("gototarget");
    }
}
