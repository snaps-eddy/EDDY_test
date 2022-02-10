package com.snaps.mobile.service.ai;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;

import java.util.List;

public class DeviceManager {
    private static final String TAG = DeviceManager.class.getSimpleName();
    private Context mContext;
    private volatile ActivityManager mActivityManager;
    private volatile ConnectivityManager mConnectivityManager;
    private volatile PowerManager mPowerManager;
    private volatile BatteryManager mBatteryManager;

    public DeviceManager(Context context) {
        mContext = context;
        mActivityManager = null;
        mConnectivityManager = null;
        mBatteryManager = null;
        mPowerManager = null;
    }

    private ActivityManager getActivityManager() {
        if (mActivityManager != null) return mActivityManager;
        mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        return mActivityManager;
    }

    private ConnectivityManager getConnectivityManager() {
        if (mConnectivityManager != null) return mConnectivityManager;
        mConnectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return mConnectivityManager;
    }

    private PowerManager getPowerManager() {
        if (mPowerManager != null) return mPowerManager;
        mPowerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        return mPowerManager;
    }

    private BatteryManager getBatteryManager() {
        if (mBatteryManager != null) return mBatteryManager;
        mBatteryManager = (BatteryManager)mContext.getSystemService(Context.BATTERY_SERVICE);
        return mBatteryManager;
    }

    /**
     * 외장 메모리 쓰기 권한을 검사한다.
     * @return
     */
    public boolean isGantedPermissionWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writeExternalStoragePermission = mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return (writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED);
        }
        else {
            return true;
        }
    }

    /**
     * 외장 메모리 읽기 권한을 검사한다.
     * @return
     */
    public boolean isGantedPermissionReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writeExternalStoragePermission = mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return (writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED);
        }
        else {
            return true;
        }
    }

    /**
     * 앱이 현재 실행중인지 검사한다.
     * @return
     */
    public boolean isAppRunning() {
        List<RunningAppProcessInfo> appProcesses = getActivityManager().getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }

        final String packageName = mContext.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 앱이 현재 포그라운드인지 백그라운드인지 검사한다.
     * @return
     */
    public boolean isAppForeground() {
        List<RunningAppProcessInfo> appProcesses = getActivityManager().getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }

        final String packageName = mContext.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) &&
                    appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            {
                return true;
            }
        }
        return false;
    }


    /**
     * 앱의 프로세스 정보를 리턴한다.
     * @return 앱이 실행중이 아니라면 null을 리턴한다.
     */
    public RunningAppProcessInfo getAppProcessInfo() {
        List<RunningAppProcessInfo> appProcesses = getActivityManager().getRunningAppProcesses();
        if (appProcesses == null) {
            return null;
        }

        final String packageName = mContext.getPackageName();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) return appProcess;
        }
        return null;
    }

    /**
     * 앱이 현재 포그라운드인지 백그라운드인지 검사한다.
     * @param info
     * @return
     */
    public boolean isAppForeground(RunningAppProcessInfo info) {
        if (info == null) {
            return false;
        }

        return info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    /**
     * 와이파이 연결 상태를 검사한다.
     * @return
     */
    public boolean isWiFiConnected() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 모바일 네트워크 연결 상태를 검사한다.
     * @return
     */
    public boolean isMobileNetworkConnected() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 와이파이 연결 속도를 구한다. (*정학한지는 모름)
     * @param context
     * @return
     */
    /*
    public static float getWiFiSpeed(Context context) {
        if (context == null) return 0;
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int speed = wifiInfo.getLinkSpeed();
        return (float)speed / (float)8.0;
    }
    */

    /**
     * 와이파이 신호 강도를 구한다.
     * @param context
     * @return
     */
    /*
    public static int getWiFiSignalLevel(Context context) {
        if (context == null) return 0;
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        return level;
    }
    */

    /**
     * 실제 인터넷에 연결되어 있는지 검사한다.
     * @return
     */
    public boolean isInternetAvailable() {
        /*
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
        */
        // 참고한 코드인데 문제가 있음
        return true;
    }


    /**
     * 절전 모드인지 검사한다.
     * @return (LOLLIPOP 이하일 항상 true)
     */
    public boolean isPowerSaveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getPowerManager().isPowerSaveMode();
        }
        return false;
    }


    /**
     * 배터리 레벨을 구한다. (0 ~ 100)
     * @return (LOLLIPOP 이하일 항상 0)
     */
    public int getBattryLevel() {
        int batLevel = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batLevel = getBatteryManager().getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }

        return batLevel;
    }
}
