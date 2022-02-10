package com.snaps.mobile.kr;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.AccessAppDialog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;

/**
 * Created by ysjeong on 2017. 8. 16..
 */

public class SplashPopupHandler {

    private Activity activity;
    private SnapsHandler splashHandler;

    public static SplashPopupHandler createInstanceWithSplashHandler(Activity activity, SnapsHandler splashHandler) {
        return new SplashPopupHandler(activity, splashHandler);
    }

    private SplashPopupHandler(Activity activity, SnapsHandler splashHandler) {
        this.activity = activity;
        this.splashHandler = splashHandler;
    }

    // 팝업을 보여줄지 말지
    public boolean shouldShowPushAgreePopup() {
        // 1번이라도 보여줬다면 false(로그인 화면 등에서 수신 동의 팝업을 보았다면 false)
        if (Setting.getBoolean(activity, Const_VALUE.KEY_SAW_PUSH_AGREE_POPUP) || Config.isBlockShowCurrentUserPushAgreePopup(activity)) {
            return false;
        }
        return true;
    }

    public boolean shouldShowAccessAppPopup() {
        return Config.useKorean() && !Setting.getBoolean(activity, Const_VALUE.KEY_SAW_ACCESS_APP_POPUP);
    }

    public void showPushAgreePopup() {
        MessageUtil.alert(activity, activity.getString(R.string.title_need_to_permission_accept_for_push), activity.getString(R.string.content_need_to_permission_accept_for_push), R.string.decline_accept_for_push, R.string.confirm_accept_for_push, false, clickedOk -> {
            if (clickedOk == ICustomDialogListener.OK) {
                MessageUtil.showPushAgreeInfo(activity, true, clickedOk1 -> splashHandler.sendEmptyMessage(SplashActivity.HANDLE_INIT_MAIN_UI));

            } else {
//                blockShowCurrentUserPushAgreePopup();
                Setting.set(activity, Const_VALUE.KEY_SAW_PUSH_AGREE_POPUP, true);
                splashHandler.sendEmptyMessage(SplashActivity.HANDLE_INIT_MAIN_UI);
            }
        });

    }

    public boolean shouldShowSystemPermissionPopup() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    || activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                return false;
            }

            int permissionReadStorage = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionWriteStorage = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return permissionReadStorage == PackageManager.PERMISSION_DENIED
                    || permissionWriteStorage == PackageManager.PERMISSION_DENIED;
        }
        else {
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    || activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                    || activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_MEDIA_LOCATION)) {
                return false;
            }

            int permissionReadStorage = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionWriteStorage = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int accessMediaLocation = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_MEDIA_LOCATION);
            return permissionReadStorage == PackageManager.PERMISSION_DENIED
                    || permissionWriteStorage == PackageManager.PERMISSION_DENIED
                    || accessMediaLocation == PackageManager.PERMISSION_DENIED;
        }
    }

    public void showSystemPermissionPopup() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            activity.requestPermissions(permissions, SplashActivity.REQUEST_STORAGE_PERMISSION_CODE);
        }
        else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION};
            activity.requestPermissions(permissions, SplashActivity.REQUEST_STORAGE_PERMISSION_CODE);
        }
    }


    public void showAccessAppPopup() {
        AccessAppDialog accessAppDialog = new AccessAppDialog(activity, new DialogInputNameFragment.IDialogInputNameClickListener() {
            @Override
            public void onClick(boolean isOk) {
                Setting.set(activity, Const_VALUE.KEY_SAW_ACCESS_APP_POPUP, true);

                if (shouldShowPushAgreePopup()) {
                    showPushAgreePopup();
                } else {
                    splashHandler.sendEmptyMessage(SplashActivity.HANDLE_INIT_MAIN_UI);
                }
            }

            @Override
            public void onCanceled() {
            }
        });
        accessAppDialog.setCancelable(false);
        accessAppDialog.show();
    }

    private void blockShowCurrentUserPushAgreePopup() {
        Setting.set(activity, Config.getBlockShowCurrentUserKey(activity), true);
    }
}
