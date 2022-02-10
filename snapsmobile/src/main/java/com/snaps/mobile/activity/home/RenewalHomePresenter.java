package com.snaps.mobile.activity.home;

import android.Manifest;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snaps.common.deeplink.DeeplinkService;
import com.snaps.common.push.PushManager;
import com.snaps.common.trackers.SnapsAppsFlyer;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.AsyncTask;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.model.SnapsWebAPI;
import com.snaps.mobile.service.ai.DeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class RenewalHomePresenter implements RenewalHomeContract.Presenter, RenewalHomeContract.OnCalledJavascript {
    private static final String TAG = RenewalHomePresenter.class.getSimpleName();
    private RenewalHomeContract.View view;
    private SharedPreferenceRepository spRepository;
    private DeviceManager deviceManager;
    private SnapsWebAPI webUrlManager;
    private PushManager pushService;
    private String deviceUUID;

    public RenewalHomePresenter(SharedPreferenceRepository spRepository, DeviceManager deviceManager, PushManager pushService) {
        this.spRepository = spRepository;
        this.deviceManager = deviceManager;
        this.webUrlManager = new SnapsWebAPI();
        this.pushService = pushService;
    }

    private Context context;
    public void setContext_ForDebug(Context context) {
        if (Config.isDevelopVersion()) {
            this.context = context;
        }
    }

    @Override
    public void setView(RenewalHomeContract.View view) {
        this.view = view;
    }

    @Override
    public void onInitWebView(String deviceUUID) {
        this.deviceUUID = deviceUUID;

        boolean isUserChangeLanguage = spRepository.getBoolean(Const_VALUE.KEY_IS_USER_CHANGE_LANGUAGE, false);
        if (isUserChangeLanguage) {
            pushService.requestRegistPushDevice();
        }

        String firstPageStr = webUrlManager.getFirstPageURL(spRepository, deviceUUID);
        view.loadWebPage(firstPageStr);
    }

    @Override
    public void onPermissionDialogButtonClicked(String permissionName, boolean hasPermission) {
        String url = makeSetAppPermissionYN(permissionName, hasPermission);
        view.loadWebPage(url);
    }

    @Override
    public void onFileWritePermissionDialogButtonClicked(boolean hasPermission) {
        String url = makeCheckPermissionResult(hasPermission);
        view.loadWebPage(url);
    }

    @Override
    public void onMessageGoToCart(String currentUrl) {
        //아래와 같이 처리한 이유는 장바구니 주소가 쿼리 파라미터가 있는 경우와 없는 경우가 있다. 브라우저 히스토리를 안 남기려고 reload
        String cartUrl = webUrlManager.getWebViewDomainForLanguage(spRepository, true);
        String cartUrlWithParams = cartUrl + webUrlManager.getFirstPageParameters(spRepository, deviceUUID);
        if (currentUrl.equals(cartUrl) || currentUrl.equals(cartUrlWithParams)) {
            view.reloadWebPage();
        } else {
            view.loadWebPage(cartUrlWithParams);
        }
    }

    @Override
    public void onShowPushServiceConfirmDialog() {
        pushService.requestRegistPushDevice();
    }

    @Override
    public void onRestart() {
        view.loadWebPage(String.format("javascript:%s()", "refresh"), 1000L);
    }

    @Override
    public void onBackPressed(String originalURL, boolean canGoBack) {
        if ((webUrlManager.getWebViewDomainForLanguage(spRepository, false) + "/").equalsIgnoreCase(originalURL)) { //TODO::여기서 문제 생길..
            view.executeDoubleClickBackbutton();
        } else if (canGoBack) {
            view.executeWebViewGoBack();
        } else {
            view.executeDoubleClickBackbutton();
        }
    }

    /**
     * Javascript Interface implements
     */
    @Override
    public void onSelectProduct(String param, boolean isCartItem) {
        String snapsURL = null;
        try {

            JSONObject jsonObject = new JSONObject(param);
            snapsURL = webUrlManager.makeUrlFromJson(jsonObject, isCartItem);

            String quantity = jsonObject.optString("quantity");
            Config.setQUANTITY(quantity);

        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }

        if (isCartItem) {
            view.loadCartProductPage(snapsURL);

        } else {
            view.loadProductPage(snapsURL);
        }
    }

    @Override
    public void onGoToDailybook() {
        view.startDiaryListActivity();
    }

    @Override
    public void onGoToSetting() {
        view.startSettingActivity();
    }

    @Override
    public void onSetUserInfo(String param) {
        if (isNotValidJSONText(param)) {
            return;
        }
        view.putUserInfo(param);
        view.syncWebViewCookie();
    }

    @Override
    public void onRemoveUserInfo() {
        view.deleteUserInfo();
        view.syncWebViewCookie();
    }

    @Override
    public void onRecommandAI(String param) {
        if (isNotValidJSONText(param)) {
            return;
        }

        String productCode, recommendSeq, templateCode, projectName;
        try {
            JSONObject jsonObject = new JSONObject(param);
            productCode = jsonObject.getString("productCode");
            recommendSeq = jsonObject.getString("recommendSeq");
            templateCode = jsonObject.getString("templateCode");

            Config.cleanProductInfo();

            Config.setAI_IS_RECOMMENDAI(true);
            Config.setAI_RECOMMENDREQ(recommendSeq);
            Config.setPROD_CODE(productCode);
            Config.setTMPL_CODE(templateCode);

            projectName = jsonObject.getString("projectName");
            Config.setPROJ_NAME(projectName);
        } catch (JSONException je) {
            Dlog.e(TAG, je);
        }

        view.startSmartRecommendAIMakingActivity();
    }

    @Override
    public void onSelfAI(String param) {
        if (isNotValidJSONText(param)) {
            return;
        }

        String searchType, productCode, recommendSeq, templateCode, searchValue, searchDate;
        try {
            JSONObject jsonObject = new JSONObject(param);
            searchType = jsonObject.getString("searchType");
            productCode = jsonObject.getString("productCode");
            recommendSeq = jsonObject.getString("recommendSeq");
            templateCode = jsonObject.getString("templateCode");
            searchValue = jsonObject.getString("searchValue");

            Config.cleanProductInfo();

            Config.setAI_IS_SELFAI(true);
            Config.setAI_SEARCHTYPE(searchType);
            Config.setAI_SEARCHVALUE(searchValue);
            Config.setAI_RECOMMENDREQ(recommendSeq);
            Config.setPROD_CODE(productCode);
            Config.setTMPL_CODE(templateCode);

            searchDate = jsonObject.getString("searchDate");
            Config.setAI_SEARCHDATE(searchDate);

        } catch (JSONException je) {
            Dlog.e(TAG, je);
        }

        view.startSelfAIPhotoBook();
    }

    @Override
    public void onSetAISync() {
        /*
        spRepository.set(Const_VALUE.KEY_SNAPS_AI, true);
        spRepository.set(Const_VALUE.KEY_SNAPS_AI_TOS_AGREEMENT, true);

        String userNo = spRepository.getString(Const_VALUE.KEY_SNAPS_USER_NO);

        AsyncTask.execute(() -> {
            String result = HttpReq.requestPutAIUse(userNo, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            Dlog.d("onSetAISync() result:" + result);
        });
        */
    }

    @Override
    public void onSetAISyncWithLTE(String param) {
        /*
        final String keyIsWithLTE = "isWithLTE";
        try {
            JSONObject json = new JSONObject(param);
            if (!json.has(keyIsWithLTE)) {
                return;
            }

            boolean isWithLTE = json.getString(keyIsWithLTE).equalsIgnoreCase("Y");
            spRepository.set(Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, isWithLTE);
            String userNo = spRepository.getString(Const_VALUE.KEY_SNAPS_USER_NO);

            AsyncTask.execute(() -> {
                String result = HttpReq.requestPutAISyncWithLTE(userNo, isWithLTE, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                Dlog.d("onSetAISyncWithLTE() result:" + result);
            });

        } catch (JSONException je) {
            Dlog.e(TAG, je);
        }
        */
    }

    @Override
    public void onCheckDevicePermission(String param) {
        if (isNotValidJSONText(param)) {
            return;
        }

        try {
            JSONObject json = new JSONObject(param);
            String pemissionName = json.getString("permissionName");

            boolean hasPermission;

            if ("read_photo".equalsIgnoreCase(pemissionName)) {
                hasPermission = deviceManager.isGantedPermissionReadExternalStorage() || deviceManager.isGantedPermissionWriteExternalStorage();

            } else if ("push_message".equalsIgnoreCase(pemissionName)) {
                hasPermission = spRepository.getBoolean(Const_VALUE.KEY_GCM_PUSH_RECEIVE);

            } else if ("wifi".equalsIgnoreCase(pemissionName)) {
                hasPermission = deviceManager.isWiFiConnected();

            } else {
                hasPermission = false;
            }

            String url = makeSetAppPermissionYN(pemissionName, hasPermission);
            Dlog.d( "JAVASCRIPT makeSetAppPermissionYN pemissionName:"
                            + pemissionName + ", hasPermission:" + hasPermission + ", RETURN:" + url);
            view.loadWebPage(url);

        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onRequestDevicePermission(String param) {
        if (isNotValidJSONText(param)) {
            return;
        }

        try {
            JSONObject json = new JSONObject(param);
            String pemissionName = json.getString("permissionName");

            if ("read_photo".equalsIgnoreCase(pemissionName)) {
                view.showSetFilePermissionInfo(pemissionName);
                return;
            }

            if ("push_message".equalsIgnoreCase(pemissionName)) {
                view.showPushServiceConfirmDialog(pemissionName);
            }

        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onSetLogConversion(String param) {
        String debugMsg = "";
        if (Config.isDevelopVersion()) {
            try {
                JSONObject json = new JSONObject(param);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                debugMsg = "onSetLogConversion:" + "\n" + gson.toJson(json);
                String finalDebugMsg = debugMsg;
                new CountDownTimer(10000, 900) {
                    public void onTick(long millisUntilFinished) {
                        Toast toast = Toast.makeText(context, finalDebugMsg, Toast.LENGTH_SHORT);
                        ViewGroup group = (ViewGroup) toast.getView();
                        TextView messageTextView = (TextView) group.getChildAt(0);
                        messageTextView.setTextSize(14);
                        toast.show();
                    }
                    public void onFinish() {}
                }.start();
            } catch (Exception e) {
                Toast.makeText(context, "onSetLogConversion:" + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        if (isNotValidJSONText(param)) {
            return;
        }

        try {
            JSONObject json = new JSONObject(param);
            String type = json.getString("type");

            if ("JOIN".equalsIgnoreCase(type)) {
                SnapsAppsFlyer.setJoinComplete();
            } else if ("PAYMENT".equalsIgnoreCase(type)) {
                String totalPrice = json.getString("totalPrice");
                String orderCode = json.getString("orderCode");
                SnapsAppsFlyer.setInappEvent(totalPrice, orderCode);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (Config.isDevelopVersion()) {
                debugMsg += "\n" + e.getMessage();
                Toast.makeText(context, debugMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Javascript Interface implements
     */

    private String makeSetAppPermissionYN(String permissionName, boolean hasPermission) {
        return String.format("javascript:%s(\"%s\", '%s')", "setAppPermissionYN", permissionName.toUpperCase(), hasPermission ? "Y" : "N");
    }

    private String makeCheckPermissionResult(boolean hasPermission) {
        return String.format("javascript:%s('%s')", "checkPermissionResult", hasPermission);
    }

    private boolean isNotValidJSONText(String param) {
        return param == null || param.trim().length() < 1;
    }

    /**
     * @param externalURL    ex)https://kr.snaps.com/ai/intro
     * @param prmChannelCode 웹에서 받은 channel code
     */
    @Override
    public void onGetExternalURL(String externalURL, String prmChannelCode, String deviceChannelCode) {
        if (TextUtils.isEmpty(externalURL) || TextUtils.isEmpty(prmChannelCode) || TextUtils.isEmpty(deviceChannelCode)) {
            return;
        }

        DeeplinkService deeplinkService = new DeeplinkService();
        if (deeplinkService.isFitLanguageTarget(prmChannelCode, deviceChannelCode)) {
            if (externalURL.contains("?")) {
                externalURL += webUrlManager.getFirstPageParameters(spRepository, deviceUUID).replace("?", "&");
            } else {
                externalURL += webUrlManager.getFirstPageParameters(spRepository, deviceUUID);
            }
            view.loadWebPage(externalURL, 500L);

        } else {
            view.showAlertDialog(R.string.deeplink_error_language_different);
        }
    }


    //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
    @Override
    public void onCheckPermission(String param) {
        if (isNotValidJSONText(param)) {
            Dlog.e(TAG, "onCheckPermission() isNotValidJSONText");
            return;
        }

        try {
            JSONObject json = new JSONObject(param);
            String permission = json.getString("permission");

            if ("permission_photo_write".equalsIgnoreCase(permission)) {
                boolean hasPermission = deviceManager.isGantedPermissionReadExternalStorage() || deviceManager.isGantedPermissionWriteExternalStorage();
                if (hasPermission) {
                    String url = makeCheckPermissionResult(hasPermission);
                    Dlog.d("JAVASCRIPT checkPermissionResult " + hasPermission);
                    view.loadWebPage(url);
                }
                else {
                    view.showSetFileWritePermissionInfo();
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
    // {"imageData":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAu4AAAPoCAYAAACBM3tjAAAgAAASUVORK5CYII="}
    @Override
    public void onEraserFileSave(String param) {
        if (isNotValidJSONText(param)) {
            Dlog.e(TAG, "onEraserFileSave() isNotValidJSONText");
            return;
        }

        try {
            JSONObject json = new JSONObject(param);
            String imageData = json.getString("imageData");

            String base64EncodedString = imageData.substring(imageData.indexOf(",") + 1);
            byte[] bytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
            File file = createImageFile(bytes);
            if (file == null) {
                Dlog.e(TAG, "onEraserFileSave() createImageFile ERROR");
                return;
            }

            view.rescanMediaScanner(file);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private File createImageFile(byte[] bytes) {
        File outputDir = createDir();
        if (outputDir == null) {
            return null;
        }

        FileOutputStream fos = null;
        try {
            String fileName = FILE_NAME_DATE_FORMAT.format(new Date()) + ".png";
            File outputFile = new File(outputDir, fileName);
            outputFile.createNewFile();
            fos = new FileOutputStream(outputFile);
            fos.write(bytes);
            fos.flush();
            return outputFile;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }

        return null;
    }

    private static final String DIR_NAME_ERASER_FILE = "snaps";
    private File createDir() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return null;
        }

        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), DIR_NAME_ERASER_FILE);
        if (!outputDir.isDirectory()) {
            if (!outputDir.mkdirs()) {
                return null;
            }
        }
        return outputDir;
    }
}







