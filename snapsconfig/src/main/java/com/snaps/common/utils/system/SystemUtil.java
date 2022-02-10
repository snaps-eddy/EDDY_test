package com.snaps.common.utils.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

//import com.snaps.common.R;

public class SystemUtil {
    private static final String TAG = SystemUtil.class.getSimpleName();

    private static final long MIN_STORAGE_SPACE_SIZE_FOR_SAVE_PROJECT = 200L; //MB
    private static final String BUGGY_ANDROID_ID = "9774d56d682e549c";
    private static final String DEVICE_ID_PREFIX = "v1_";

    private static String sCacheDeviceId = null;

    public static boolean isActivityFinishing(Activity activity) {
        return activity == null || (Build.VERSION.SDK_INT > 16 && activity.isDestroyed()) || activity.isFinishing();
    }

    public static void removeNotification(Activity activity) {
        try {
            NotificationManager nMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    //기본 저장 공간 체크 로직..
    public static boolean isEnoughStorageSpace() {
        long megAvailable = getStorageSpaceMB();
        return MIN_STORAGE_SPACE_SIZE_FOR_SAVE_PROJECT <= megAvailable;
    }

    public static long getStorageSpaceMB() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }

        return bytesAvailable / (1024 * 1024);
    }

    public static long getInternalMemorySizeMB() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return (availableBlocks * blockSize) / (1024 * 1024);
    }

    /**
     * 스크린 sleep 모드
     *
     * @param win
     * @param isOn
     */
    public static void toggleScreen(Window win, boolean isOn) {
        if (isOn)
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 현재 설정된 언어 가져오기.
     *
     * @return
     */
    public static String getLanguage() {

        return Locale.getDefault().getDisplayLanguage();
    }

    /**
     * 스크린 사이즈 가져오기
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        return display.getWidth() + " " + display.getHeight();
    }


    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(sCacheDeviceId)) {
            return sCacheDeviceId;
        }

        String androidId = getAndroidId(context);

        if (BUGGY_ANDROID_ID.equals(androidId)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                sCacheDeviceId = createRandomUUID(context);
            } else {
                sCacheDeviceId = createUUID(getDeviceSerialNumber());
            }

        } else {
            sCacheDeviceId = createUUID(androidId);
        }
        return sCacheDeviceId;
    }

    @SuppressLint("HardwareIds")
    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static String createUUID(@Nullable String seedText) {
        String passedText = seedText;
        if (passedText == null) {
            passedText = UUID.randomUUID().toString();
        }

        byte[] seedBytes;
        try {
            seedBytes = passedText.getBytes("utf8");
        } catch (UnsupportedEncodingException e) {
            seedBytes = passedText.getBytes();
        }
        return DEVICE_ID_PREFIX + UUID.nameUUIDFromBytes(seedBytes).toString();
    }

    private static String createRandomUUID(Context context) {
        String oldDeviceId = Setting.getString(context, Const_VALUE.KEY_DEVICE_ID, null);

        if (TextUtils.isEmpty(oldDeviceId)) {
            String generateDeviceId = DEVICE_ID_PREFIX + UUID.randomUUID().toString();
            Setting.set(context, Const_VALUE.KEY_DEVICE_ID, generateDeviceId);
            return generateDeviceId;

        } else {
            return DEVICE_ID_PREFIX + oldDeviceId;
        }
    }

    private static String getDeviceSerialNumber() {
        try {
            return (String) Build.class.getField("SERIAL").get(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 앱 설치여부확인
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstallApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static boolean isKorean() {
        return (Locale.getDefault().equals(Locale.KOREA) || Locale.getDefault().equals(Locale.KOREAN));
    }

    public static boolean isJapan() {
        return (Locale.getDefault().equals(Locale.JAPAN) || Locale.getDefault().equals(Locale.JAPANESE));
    }

    public static boolean isEng() {
        return (Locale.getDefault().equals(Locale.ENGLISH) || Locale.getDefault().equals(Locale.US) || Locale.getDefault().equals(Locale.UK));
    }

    public static boolean isCHN() {
        return (Locale.getDefault().equals(Locale.CHINA) || Locale.getDefault().equals(Locale.CHINESE));
    }

    public static String getAppVersion(Context context) {
        if (context == null) return "";
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    public static Map<String, String> getWebviewVersionMapData(Context context) {
        Map<String, String> mapVersion = new HashMap<String, String>();
        mapVersion.put("snapsVer", getAppVersion(context != null ? context : ContextUtil.getContext()));
        return mapVersion;
    }
}
