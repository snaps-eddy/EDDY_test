package com.snaps.mobile.activity.home;

import java.io.File;

import androidx.annotation.StringRes;

public interface RenewalHomeContract {

    //일단 RenewalHomeActivity 와 이름을 갖게 하기 위해 Renewal을 붙힘.

    interface View {

        void reloadWebPage();

        void loadWebPage(String url);

        void loadWebPage(String url, long delay);

        void loadCartProductPage(String snapsURL);

        void loadProductPage(String snapsURL);

        void startDiaryListActivity();

        void startSettingActivity();

        void syncWebViewCookie();

        void startSmartRecommendAIMakingActivity();

        void startSelfAIPhotoBook();

        void putUserInfo(String param);

        void deleteUserInfo();

        void showToast(String message);

        void showAlertDialog(@StringRes int messageId);

        void executeDoubleClickBackbutton();

        void executeWebViewGoBack();

        void showSetFilePermissionInfo(String permissionName);

        void showPushServiceConfirmDialog(String permissionName);

        //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
        void showSetFileWritePermissionInfo();

        //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
        void rescanMediaScanner(File file);
    }

    interface Presenter {

        void setView(View view);

        void onInitWebView(String deviceUUID);

        void onPermissionDialogButtonClicked(String permissionName, boolean pemission);

        void onMessageGoToCart(String currentUrl);

        void onShowPushServiceConfirmDialog();

        void onRestart();

        void onBackPressed(String originalURL, boolean canGoBack);

        //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
        void onFileWritePermissionDialogButtonClicked(boolean permission);
    }

    interface OnCalledJavascript {

        void onSelectProduct(String param, boolean isCartItem);

        void onGoToDailybook();

        void onGoToSetting();

        void onSetUserInfo(String param);

        void onRemoveUserInfo();

        void onRecommandAI(String param);

        void onSelfAI(String param);

        void onSetAISync();

        void onCheckDevicePermission(String param);

        void onRequestDevicePermission(String param);

        void onSetAISyncWithLTE(String param);

        void onSetLogConversion(String param);

        void onGetExternalURL(String externalURL, String prmChannelCode, String deviceChannelCode);

        //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
        void onCheckPermission(String param);

        //2020.01.04 Ben 배경 제거 서비스 파일 저장 관련
        void onEraserFileSave(String param);
    }

}
