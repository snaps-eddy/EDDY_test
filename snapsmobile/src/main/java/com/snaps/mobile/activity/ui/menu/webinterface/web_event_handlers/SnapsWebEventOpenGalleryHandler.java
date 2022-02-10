package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.FileUtil;
import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.WebViewCmdGotoPage;

import java.util.HashMap;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsWebEventOpenGalleryHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsWebEventOpenGalleryHandler.class.getSimpleName();
    public SnapsWebEventOpenGalleryHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas) {
        super(activity, handleDatas);
    }

    @Override
    public boolean handleEvent() {

        boolean permissionGranted = true;
        if(Build.VERSION.SDK_INT > 22 ) {
            if(activity != null && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if( activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) )
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
                else activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION);
                permissionGranted = false;
            }
        }

        if (permissionGranted) {
            openGallery(urlData);
            WebViewCmdGotoPage.gotoPage(activity, handleDatas);
        }

        return true;
    }

    /**
     * 첨부파일 전송을 위해 겔러리를 열어 줌.
     * 선택한 파일은 HomeActivity.java 에서 전송함.
     */
    private void openGallery(HashMap<String, String> urlData) {
        if(activity == null) return;

        String msg = urlData.get("callback");
        Setting.set(activity, Const_VALUE.KEY_FILE_ATTACH_CALLBACK_MSG, msg);

        FileUtil.callGallery(activity);
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() class name:" + getClass().getName());
    }
}
