package com.snaps.common.utils.ui;

import android.content.Context;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;

/**
 * Created by ysjeong on 2017. 8. 16..
 */

public class SnapsAppVersionUtil {
    public static void checkAppVersion(Context context) {
        // 기본 설정
        // /////////////////////////////////////////////////////////////////////////////////
        String preVersion = Setting.getString(context, Const_VALUE.APPVERSION);
        String appVersionString = SystemUtil.getAppVersion(context);
        boolean isVerDifferent = !appVersionString.equals(preVersion);
        // 다르면 저장..
        if (isVerDifferent) {
            Setting.set(context, Const_VALUE.APPVERSION, appVersionString);
        }

        Config.initAppVersion(appVersionString);
    }
}
