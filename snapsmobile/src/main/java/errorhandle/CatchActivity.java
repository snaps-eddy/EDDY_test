package errorhandle;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

public class CatchActivity extends AppCompatActivity {
    private static final String TAG = CatchActivity.class.getSimpleName();
    String className = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIUtil.applyLanguage(this);

        super.onCreate(savedInstanceState);
        className = this.getClass().getName();

//		Thread.setDefaultUncaughtExceptionHandler(new CatchExceptionHandler(this, className));
        Config.setCurrentClassName(className);
        sendTracker();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Dlog.d("onDestroy() className:" + className);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (!SnapsTPAppManager.isThirdPartyApp(this)) GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (!SnapsTPAppManager.isThirdPartyApp(this)) GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    void sendTracker() {
        Intent intent = new Intent(Const_VALUE.SNAPS_ACTIVITY_TRACKER_ACTION);

        intent.putExtra("activity_name", className);

        sendBroadcast(intent);
    }

}
