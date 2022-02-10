package errorhandle;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.lang.reflect.InvocationTargetException;

public class CatchFragmentActivity extends AppCompatActivity {
    private static final String TAG = CatchFragmentActivity.class.getSimpleName();

    static int screen_wdith = 0;
    static int screen_height = 0;

    @Override
    protected void onResume() {
        super.onResume();

        // AppEventsLogger.activateApp(this.getApplicationContext(), getString(com.snaps.common.R.string.facebook_appid_kr));
        if (!SnapsTPAppManager.isThirdPartyApp(this)) {
            IFacebook facebook = SnsFactory.getInstance().queryInteface();
            try {
                if (facebook != null) {
                    if (!FacebookSdk.isInitialized())
                        facebook.init(this);

                    if (FacebookSdk.isInitialized())
                        facebook.activeApp(this.getApplicationContext(), getString(R.string.facebook_appid_kr));
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    String className = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIUtil.applyLanguage(this);

        super.onCreate(savedInstanceState);
        className = this.getClass().getName();
        if (screen_wdith == 0 || screen_height == 0) {
            setScreenInfo(this);
        } else {
            setDensity();
        }
//		Thread.setDefaultUncaughtExceptionHandler(new CatchExceptionHandler(this, className));

        Config.setCurrentClassName(className);
        sendTracker();

    }

    private void setDensity() {
        float density = 0f;
        if (screen_wdith == 720 && screen_height == 1480) {
            density = 2.0f;
        } else if (screen_wdith == 1080 && screen_height == 2220) {
            density = 3.0f;
        } else if (screen_wdith == 1440 && screen_height == 2960) {
            density = 4.0f;
            Config.setWQHDResolutionDevice(true);
        } else {
            return;
        }
        getResources().getDisplayMetrics().density = density;
    }

    public void setScreenInfo(Context context) {
        if (Build.VERSION.SDK_INT >= 14) {

            android.view.Display display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();

            Point realSize = new Point();

            try {

                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);

            } catch (IllegalArgumentException e) {
                Dlog.e(TAG, e);
            } catch (IllegalAccessException e) {
                Dlog.e(TAG, e);
            } catch (InvocationTargetException e) {
                Dlog.e(TAG, e);
            } catch (NoSuchMethodException e) {
                Dlog.e(TAG, e);
            }

            screen_wdith = realSize.x;

            screen_height = realSize.y;

        } else {

            DisplayMetrics dmath = context.getResources().getDisplayMetrics();    // 화면의 가로,세로 길이를 구할 때 사용합니다.

            screen_wdith = dmath.widthPixels;

            screen_height = dmath.heightPixels;

        }
        if (screen_wdith != 0 || screen_height != 0) {
            setDensity();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed;
        ed = pref.edit();
        ed.putBoolean("touchcheck", false);
        ed.commit();

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

    public void sendPageEvent(String message) {
        if (SnapsTPAppManager.isThirdPartyApp(this)) return;

        Intent intent = new Intent(Const_VALUE.SNAPS_EVENT_TRACKER_ACTION);

        intent.putExtra("event_category", "페이지 이동");
        intent.putExtra("event_action", message);

        sendBroadcast(intent);
    }

    public void sendActionEvent(String message) {
        if (SnapsTPAppManager.isThirdPartyApp(this)) return;

        Intent intent = new Intent(Const_VALUE.SNAPS_EVENT_TRACKER_ACTION);

        intent.putExtra("event_category", "전환");
        intent.putExtra("event_action", message);

        sendBroadcast(intent);
    }
}
