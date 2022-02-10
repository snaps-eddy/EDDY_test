package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 16. 8. 12..
 */

/**
 * 웹뷰에서 클릭 했을 때, 일반적인 URL타입의 처리 Handler.
 * (snapsApp://으로 시작하는 url이 아닌 경우...)
 */
public class SnapsWebEventThirdPartySchemaHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventThirdPartySchemaHandler.class.getSimpleName();
    private static final Map<String, String> thirdPartySchemas;
    static {
        thirdPartySchemas = new HashMap<>();
        thirdPartySchemas.put("shilladfs", "com.shilladutyfree");
    }

    public static String getThirdPartySchemaWithUrl(String url) {
        if (StringUtil.isEmpty(url) || !url.contains(":") || SnapsWebEventThirdPartySchemaHandler.thirdPartySchemas == null) return null;

        String targetSchema = url.substring(0, url.indexOf(":"));
        Map<String, String> schemas = SnapsWebEventThirdPartySchemaHandler.thirdPartySchemas;
        if (schemas.containsKey(targetSchema)) return schemas.get(targetSchema);
        return null;
    }

    public SnapsWebEventThirdPartySchemaHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {
        if (handleDatas == null) return false;

//        snapsapp://gotoPage?pageNum=P0008&optionUrl=/mw/v3/event/201803/shilladfs.jsp&title=EVENT
//        http://m.shilladfs.com/estore/kr/ko/deeplink?url=http://www.shilladfs.com/estore/kr/ko/event/eventView?affl_id=900812
//        shilladfs://deepLink?redirectUrl=http://www.shilladfs.com/estore/kr/ko/event/eventView?affl_id=900812&amp;uiel=Mobile

        try {
            String thirdPartySchema = handleDatas.getThirdPartySchema();
            boolean isInstalledApp = searchAppPackage(activity, thirdPartySchema);
            if (isInstalledApp) {
                executeLocalAppPackage(activity, thirdPartySchema);
            } else {
                executeStoreAppPackage(activity, thirdPartySchema);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return false;
        }

        return true;
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class Name:" + getClass().getName());
    }

    /** 특정 패키지명의 앱 설치 여부 체크업 **/
    private boolean searchAppPackage(Context context, String packageName){
        boolean bExist = false;

        /** 패키지 정보 리스트 추출 **/
        PackageManager pkgMgr = context.getPackageManager();
        List<ResolveInfo> mAppList;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mAppList = pkgMgr.queryIntentActivities(mainIntent, 0);

        /** 패키지 리스트 순회하면서 특정 패키지명 검색 **/
        try{
            for(int i=0;i<mAppList.size();i++){
                Dlog.d("searchAppPackage() app name : " + mAppList.get(i).activityInfo.packageName);
                if(mAppList.get(i).activityInfo.packageName.startsWith(packageName)){
                    bExist = true;
                    break;
                }
            }
        }catch(Exception e){
            bExist = false;
        }
        return bExist;
    }

    /** 특정 패키지명의 앱 실행(설치여부 확인후 실행필요) **/
    private void executeLocalAppPackage(Context context, String packageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /** 특정 패키지명의 앱의 설치경로(PlayStore) 이동처리 **/
    private void executeStoreAppPackage(Context context, String packageName){
        String url = "market://details?id=" + packageName;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(i);
    }
}
