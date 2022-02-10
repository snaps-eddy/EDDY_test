package com.snaps.mobile.kr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_UpdateInfo;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnapsLanguageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.instagram.utils.instagram.InstagramSession;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.interfaces.OnDataLoadListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static com.snaps.common.utils.constant.Const_VALUE.SMART_SNAPS_DAY_CHECK;

/**
 * Created by ysjeong on 2017. 8. 16..
 */

public class SnapsIntroHandler {
    private static final String TAG = SnapsIntroHandler.class.getSimpleName();
    private SnapsHandler splashHandler = null;
    private Activity activity = null;
    private HttpUtil.DownloadProgressListener fontDownloadProgressListener = null;
    private Xml_UpdateInfo xmlUpdateinfo;

    public static SnapsIntroHandler createInstanceWithSplashHandler(Activity activity, SnapsHandler splashHandler) {
        return new SnapsIntroHandler(activity, splashHandler);
    }

    private SnapsIntroHandler(Activity activity, SnapsHandler splashHandler) {
        this.activity = activity;
        this.splashHandler = splashHandler;
    }

    public void downloadUpdateInfo() {
        if (splashHandler == null) {
            return;
        }

        ATask.executeVoidWithThreadPool(new ATask.OnTask() {

            boolean isStoppedIntroByUpdateInfo = false;
            boolean isSuccessIntro = false;

            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                //최소 저장 공간 체크
                if (!checkEnoughDeviceStorageSpace()) {
                    return;
                }

                if(!SnapsLanguageUtil.isAppliedServiceableLanguage(activity)){
                    UIUtil.applyLanguage(activity, Locale.ENGLISH.getLanguage(), true);
                }

                if (requestGetUpdateInfo()) {
                    if (!checkConditionByUpdateInfo()) {
                        handleUpdateInfo();
                    }
                }
            }

            private boolean checkEnoughDeviceStorageSpace() {
                if (!SystemUtil.isEnoughStorageSpace()) {
                    splashHandler.sendEmptyMessage(SplashActivity.HANDLE_NOT_ENOUGH_STORAGE_SPACE);
                    isStoppedIntroByUpdateInfo = true;
                    return false;
                }
                return true;
            }

            private boolean requestGetUpdateInfo() {
                xmlUpdateinfo = GetParsedXml.getUpdateInfoForMobile(SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                return xmlUpdateinfo != null;
            }

            private boolean checkConditionByUpdateInfo() {
                return !checkEmergencyNoticePopup() || !checkAppVersionByUpdateInfo();
            }

            private void handleUpdateInfo() {
                if (xmlUpdateinfo == null) {
                    return;
                }

                try {
                    setPreferencesByUpdateInfo();

//                    setAdbrixByUpdateInfo();

                    setAppsFlyerByUpdateInfo();

                    boolean shouldDownloadFont = !FontUtil.checkAllFontExist();
                    if (shouldDownloadFont) {
                        FontUtil fontUtil = FontUtil.getInstance();
                        isSuccessIntro = fontUtil.downloadFont(activity, fontDownloadProgressListener);
                    } else {
                        isSuccessIntro = true;
                    }
                } catch (Throwable t) {
                    //갤럭시 s10 일부 단말에서 문제가 발생하고 있는데 원인 파악이 안되서 로그 추가
                    Dlog.e(TAG, t);
                }
            }

            private void setPreferencesByUpdateInfo() {
                // 카카오 이벤트 여부 설정..
                Setting.set(activity, Const_VALUE.KAKAO_EVENT_OPEN, xmlUpdateinfo.isDoingFriendEvent() ? "true" : "false");

                // 구글 어날리틱스 사용 설정.
                Setting.set(activity, Const_VALUE.KEY_SEND_GOOGLE_ANALYTICS_DATA, xmlUpdateinfo.isEnableGoogleAnalytics());

                // TODO 이미지 캐쉬 skip 옵션. 임시니까 const value로 안만듬. false면 skip. true면 사용.
                Setting.set(activity, "do_not_use_imageCache", xmlUpdateinfo.isImageCache());

                Setting.set(activity, Const_VALUE.USE_USER_CERTIFICATION, xmlUpdateinfo.isUsePhoneCertification());
                if (null != xmlUpdateinfo.getInstargramLogin()) {
                    InstagramSession mSession = new InstagramSession(activity);
                    mSession.setLoginPath(xmlUpdateinfo.getInstargramLogin());
                }
            }

//            private void setAdbrixByUpdateInfo() {
//                // adbrix 시작
//                SnapsAdbrix.setEnable(xmlUpdateinfo.isEnableAdbrix()); // enableAdbrix값을 적용
//                SnapsAdbrix.startAdbrix(activity);
//                SnapsAdbrix.setDeferredLinkListener(activity);
//                String intentStr = Setting.getString(activity, Const_VALUE.KEY_ADBRIX_INTENT);
//                if (!StringUtil.isEmpty(intentStr)) { // 저장된 intent값이 있는지 확인
//                    try {
//                        SnapsAdbrix.sendInstallInfo(activity, Intent.parseUri(intentStr, 0));
//                    } catch (URISyntaxException e) {
//                        Dlog.e(TAG, e);
//                    }
//                    Setting.set(activity, Const_VALUE.KEY_ADBRIX_INTENT, ""); // 저장값 삭제
//                }
//            }

            private void setAppsFlyerByUpdateInfo() {
                SnapsAppsFlyer.setEnable(xmlUpdateinfo.isEnableAppsFlyer());
                if (xmlUpdateinfo.isEnableAppsFlyer()) {
                    splashHandler.sendEmptyMessage(SplashActivity.HANDLE_INIT_APPS_FLYER);
                }
            }

            private boolean checkAppVersionByUpdateInfo() {
                if (Config.isDevelopVersion()) {
                    return true;
                }

                // - 앱버전 체크 : 업데이트 버전이 더 높을 경우 마켓으로 이동
                // if (SystemUtil.getAppVersion(activity).compareTo(xmlUpdateinfo.android_version) < 0) {
                // 앱 버젼 체크 두자리이사으로 변경..
                if (Config.isRealServer()) {
                    int verCheck = StringUtil.compareVersion(SystemUtil.getAppVersion(activity), xmlUpdateinfo.getAppVersion());
                    if (verCheck < 0) {
                        splashHandler.sendEmptyMessage(SplashActivity.HANDLE_MOVE_MARKET);
                        isStoppedIntroByUpdateInfo = true;
                        return false;
                    } else if (verCheck == 99) {
                        splashHandler.sendEmptyMessage(SplashActivity.HANDLE_VERSION_WARNNING);
                        isStoppedIntroByUpdateInfo = true;
                        return false;
                    }
                }
                return true;
            }

            private boolean checkEmergencyNoticePopup() {
                // - 긴급 공지사항 체크 - 기존코드 재활용
                // 버전이 현재 앱 버전과 같거나 0이면 실행. 메세지는 필수.
                if (("0".equals(xmlUpdateinfo.getNoticeVersion()) || SystemUtil.getAppVersion(activity).equals(xmlUpdateinfo.getNoticeVersion())) && !StringUtil.isEmpty(xmlUpdateinfo.getNoticeMsg())) {
                    splashHandler.sendEmptyMessage(SplashActivity.HANDLE_SHOW_NOTICE);
                    isStoppedIntroByUpdateInfo = true;
                    return false;
                }
                return true;
            }

            @Override
            public void onPost() {
                //UpdateInfo에 Notice나 버전 업데이트 저장 공간 무족 등의 사유로 앱 실행을 진행 할 수 없는 상태
                if (isStoppedIntroByUpdateInfo) {
                    return;
                }

                if (isSuccessIntro) {// 정상적인 실행이면 다음으로 진행
                    initMainUI();
                } else {// 정상적인 실행이 아니면 어플을 실행할 수 없는 상태이므로 종료함.
                    int msgId = -1;
                    if (xmlUpdateinfo == null)// updateinfo 수신 실패 시
                    {
                        msgId = R.string.network_status_check;
                    } else
                    // 기타 intro process 실패 시
                    {
                        msgId = R.string.intro_fail;
                    }

                    MessageUtil.alert(activity, msgId, false, new ICustomDialogListener() {

                        @Override
                        public void onClick(byte clickedOk) {
                            requestFinishApp();
                        }
                    });
                }
            }
        });
    }

    private void initMainUI() {
        splashHandler.sendEmptyMessage(SplashActivity.HANDLE_INIT_MAIN_UI);
    }

    private void requestFinishApp() {
        splashHandler.sendEmptyMessage(SplashActivity.HANDLE_REQUEST_APP_FINISH);
    }

    public void initMenuData() {
        final MenuDataManager menuDataManager = MenuDataManager.getInstance();
        if (menuDataManager != null) {
            menuDataManager.setDataLoadListener(new OnDataLoadListener() {
                @Override
                public void onProcessDone() {
                    registerAppLaunchAndInstallInfo();
                }

                @Override
                public void onGetSpineInfoFailed() {
                    menuDataManager.setDataLoadListener(null);
                    splashHandler.sendEmptyMessage(SplashActivity.HANDLE_MAXPAGE_WARNNING);
                }
            });
            menuDataManager.init(activity);
        }
    }

    public void registerAppLaunchAndInstallInfo() {
        /** 앱 설치나 초기화 후 한번만 실행 */
        if (!Setting.getBoolean(activity, Const_VALUE.FIRST_LAUNCH_PROCESS_DONE)) {
            Setting.set(activity, Const_VALUE.FIRST_LAUNCH_PROCESS_DONE, true);
            registerAppInstallInfo();
        }

        // 앱 접속 횟수 통계 정보 등록
        registerAppLaunchInfo();

        splashHandler.sendEmptyMessage(SplashActivity.HANDLE_COMPLETE_PROGRESS);
    }

    // 오류 통계 집계를 위해 접속 횟수를 서버로 전송한다.
    private void registerAppLaunchInfo() {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    String appVer = SystemUtil.getAppVersion(activity);
                    if (appVer != null && appVer.length() > 0) {
                        GetParsedXml.postAppLaunchCount(appVer, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }

    // 설치시 전송.
    private void registerAppInstallInfo() {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    String appVer = SystemUtil.getAppVersion(activity);
                    if (appVer != null && appVer.length() > 0) {
                        GetParsedXml.postAppInstallCount(appVer, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }

//    public void smartSnapsCRM(Activity context) {
//        if (!checkDay(context)) {
//            return;
//        }
//        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
//            @Override
//            public void onPre() {
//            }
//
//            @Override
//            public void onBG() {
//                try {
//
//                    if (Build.VERSION.SDK_INT > 22) {
//                        if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                            sendLog(context);
//                        }
//                    } else {
//                        sendLog(context);
//                    }
//                } catch (Exception e) {
//                    Dlog.e(TAG, e);
//                }
//            }
//
//            @Override
//            public void onPost() {
//            }
//        });
//    }

    private boolean checkDay(Context context) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);
        String saveTime = Setting.getString(context, SMART_SNAPS_DAY_CHECK, "");
        if (TextUtils.isEmpty(saveTime)) {
            return true;
        }
        if (getTime.equals(saveTime)) {
            return false;
        } else {
            return true;
        }
    }

//    private void sendLog(Activity activity) {
//
//        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list = getImageList(activity);
//        String jsonStr = imageListConvertJson(list);
//        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.v1_user_totalimg_exif)
//                .appendPayload(WebLogConstants.eWebLogPayloadType.IMAGE_EXIF, jsonStr)
//                .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_CNT, list.size() + ""));
//        Date date = new Date(System.currentTimeMillis());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String getTime = sdf.format(date);
//        Setting.set(activity, SMART_SNAPS_DAY_CHECK, getTime);
//
//    }
//
//    private ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> getImageList(final Activity activity) {
//        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list = new ArrayList<>();
//        ImageSelectPhonePhotoData phonePhotoUriData = new ImageSelectPhonePhotoData(activity);
//        phonePhotoUriData.createAlbumDatas();
//        phonePhotoUriData.createAllPhotoDataOfCellPhone(phonePhotoUriData.getArrCursor(), activity);
//        list = phonePhotoUriData.getPhotoListByAlbumId(String.valueOf(ISnapsImageSelectConstants.PHONE_ALL_PHOTO_CURSOR_ID));
//        return list;
//    }

    private static String imageListConvertJson(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list) {
        String srtJson = "";
        int count = list.size();

        try {

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < count; i++) {
                GalleryCursorRecord.PhonePhotoFragmentItem item = list.get(i);
                JSONObject jsonObjectImage = new JSONObject();
                jsonObjectImage.put("index", i);
                jsonObjectImage.put("imageKey", item.getPhotoInfo().getOrgImgPath());
                jsonObjectImage.put("oripqW", item.getImgOutWidth());
                jsonObjectImage.put("oripqH", item.getImgOutHeight());
                jsonObjectImage.put("sysDate", StringUtil.getSafeStrIfNotValidReturnSubStr(getImageSystemDateTime(item),
                        StringUtil.convertLongTimeToSmartAnalysisFormat(System.currentTimeMillis())));

                ExifUtil.SnapsExifInfo snapsExifInfo = ExifUtil.getExifInfoWithFilePath(item.getPhotoOrgPath());
                if (snapsExifInfo != null) {
                    jsonObjectImage.put("ot", Integer.parseInt(StringUtil.getSafeStrIfNotValidReturnSubStr(snapsExifInfo.getOrientationTag(), "0")));
                    jsonObjectImage.put("exifDate", StringUtil.getSafeStrIfNotValidReturnSubStr(snapsExifInfo.getDate(), ""));
                    jsonObjectImage.put("gps", StringUtil.getSafeStrIfNotValidReturnSubStr(snapsExifInfo.getLocationStr(), ""));

                } else {
                    jsonObjectImage.put("ot", 0);
                    jsonObjectImage.put("exifDate", "");
                    jsonObjectImage.put("gps", "");
                }
                jsonArray.put(jsonObjectImage);
            }
            srtJson = jsonArray.toString();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return srtJson;

    }

    private static String getImageSystemDateTime(GalleryCursorRecord.PhonePhotoFragmentItem item) {
        try {
            Date takenDate = new Date(item.getPhotoInfo().getTakenTime());
            long takenTime = takenDate.getTime();
            if (DateUtil.isValidSmartSnapsDate(takenTime)) {
                String convertedDate = StringUtil.convertLongTimeToSmartAnalysisFormat(takenDate.getTime());
                if (!StringUtil.isEmpty(convertedDate)) {
                    return convertedDate;
                }
            }

            //만약, 로컬 Media 날짜가 부정확하다면 lastmodified 날짜를 쓰도록 한다.
            File file = new File(item.getPhotoOrgPath());
            if (file.exists()) {
                Date lastModDate = new Date(file.lastModified());
                long dateTime = lastModDate.getTime();
                if (DateUtil.isValidSmartSnapsDate(dateTime)) {
                    String convertedDate = StringUtil.convertLongTimeToSmartAnalysisFormat(lastModDate.getTime());
                    if (!StringUtil.isEmpty(convertedDate)) {
                        return convertedDate;
                    }
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return StringUtil.convertLongTimeToSmartAnalysisFormat(Calendar.getInstance().getTimeInMillis());
    }

    public void setFontDownloadProgressListener(HttpUtil.DownloadProgressListener fontDownloadProgressListener) {
        this.fontDownloadProgressListener = fontDownloadProgressListener;
    }

    public Xml_UpdateInfo getXmlUpdateinfo() {
        return xmlUpdateinfo;
    }
}
