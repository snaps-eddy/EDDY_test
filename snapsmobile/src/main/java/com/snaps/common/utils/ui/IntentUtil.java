package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import errorhandle.logger.Logg;

import static com.snaps.common.utils.constant.ISnapsConfigConstants.SNAPS_EXEC_GOTO_HOST;

public class IntentUtil {
    private static final String TAG = IntentUtil.class.getSimpleName();

    /**
     * 카카오스토리 설치를 위한 마켓으로 이동
     *
     * @param context
     */
    public static void moveMarketforKakaoStory(Context context) {
        Uri appUri = Uri.parse("market://details?id=" + "com.kakao.story");
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, appUri));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * Url로 이동(웹브라우저, 마켓 등)
     *
     * @param context
     * @param url
     */
    public static void moveUrl(Context context, String url) {
        if (url == null || "".equals(url))
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Dlog.d("moveUrl() url:" + url);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * Activity를 최상위, 싱글탑으로 연다.
     *
     * @param context
     * @param intent
     */
    public static void startActivity(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    /**
     * Gmail로 직접 이메일 보내기
     */
    public static void sendToGMail(Context context, String address) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            final PackageManager pm = context.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            context.startActivity(emailIntent);
        } catch (Exception e) {
            Dlog.e(TAG, e);
//            context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Const_VALUE.QNA_EMAIL())));
            context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:cs@snaps.com")));
        }
    }

    /**
     * 주문 배송 조회 하
     */
    public static void sendToDeliveryActivity(Context context) {
        try {
            SnapsMenuManager.gotoOrderDelivery(context);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void badgeUpdate(int badgeCount, Activity activity) {
        if (activity == null)
            return;
        String pkgName = activity.getComponentName().getPackageName();// 메인 메뉴에 나타나는 어플의
        // 패키지 명
        String className = activity.getComponentName().getPackageName() + ".SplashActivity";
        Dlog.d("badgeUpdate() pkgName:" + pkgName + ", className:" +  className);

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", badgeCount);
        intent.putExtra("badge_count_package_name", pkgName);
        intent.putExtra("badge_count_class_name", className);
        activity.sendBroadcast(intent);
    }

//    public static void putExtraDataIfHostIsExecGoto(Intent intent) {
//        if (intent == null) {
//            return;
//        }
//        Uri uri = intent.getData();
//        if (uri == null) {
//            return;
//        }
//
//        String host = uri.getHost();
//        if (host == null || !host.equalsIgnoreCase(SNAPS_EXEC_GOTO_HOST)) {
//            return;
//        }
//
//        String dataString = uri.getQuery();
//
//        HashMap<String, String> parsed = StringUtil.parseUrl(dataString);
//        if (parsed != null && !parsed.isEmpty()) {
//            Set<String> keySet = parsed.keySet();
//            intent.putExtra("fullurl", dataString);
//            for (String key : keySet) {
//                intent.putExtra(key, parsed.get(key));
//            }
//        }
//    }
}
