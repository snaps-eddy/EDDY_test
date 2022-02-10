package com.snaps.mobile.service;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.snaps.common.data.interfaces.ISnapsApplication;
import com.snaps.common.push.PushManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.SnapsBaseActivity;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import errorhandle.logger.Logg;

public class NotificationActivity extends SnapsBaseActivity {
    private static final String TAG = NotificationActivity.class.getSimpleName();
    String pushTarget = "";
    boolean isRunning = false;
    private PushManager pushService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pushService = new PushManager(this);

        setContentView(R.layout.activity_push_noti);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                // 키잠금 해제하기
                // 화면 켜기
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (PushDialogTextActivity.pushdialogtextactivity != null) {
            PushDialogTextActivity.pushdialogtextactivity.finish();

        } else if (PushDialogImageActivity.pushdialogimageactivity != null) {
            PushDialogImageActivity.pushdialogimageactivity.finish();

        }

        RelativeLayout backGround = (RelativeLayout) findViewById(R.id.push_noti);
        backGround.setBackgroundColor(Color.argb(100, 0, 0, 0));

        if (Config.isAppProcess()) {
            showAlert();
        } else {
            Intent intent = null;
            if (SnapsTPAppManager.isThirdPartyApp(this)) {
                if (Config.isSnapsBitween()) {
                    intent = new Intent();
                    intent.setClassName("com.snaps.mobile.between", "com.snaps.mobile.between.MainActivity");
                }
                //FIXME SDK 도 들어가려나..?
            } else {
//				NotifyUtil.sendPushInterface( this, true );
                pushService.requestPushReceived(true);
                intent = new Intent();
                String mPackageName = getPackageName();
                String mClass = ((ISnapsApplication) getApplication()).getLauncherActivityName();
                intent.setClassName(mPackageName, mClass);
            }

            goIntentTarget(intent);
        }
    }

    public void showAlert() {
        MessageUtil.alertnoTitle(NotificationActivity.this, " " + getString(R.string.confirm_move_page_and_dont_save_editing_info), clickedOk -> {
            if (clickedOk == ICustomDialogListener.OK) {
                pushService.requestPushReceived(true);
                Intent intent = new Intent(NotificationActivity.this, RenewalHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                goIntentTarget(intent);
            } else
                finish();
        });
    }

    public void goIntentTarget(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Dlog.d("goIntentTarget() push target:" + pushTarget);

        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }

        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
