package com.snaps.mobile.service.ai;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.snaps.mobile.activity.home.RenewalHomeActivity;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 디버그용 Notification
 * 마켓에 배포한 앱에 대해서 단말기에 특정 디렉토리가 존재하면 로그를 Notification으로 출력한다.
 */
class DebugNotification {
    private static final String TAG = DebugNotification.class.getSimpleName();
    private static final String TITLE_PREFIX = "\uD83D\uDE0E ";
    private static final String LOG_FILE_DIR_NAME = "snaps_logs";
    private static final int NOTIFY_ID = 4885;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final SimpleDateFormat sLogTimeFormatter = new SimpleDateFormat("HH:mm:ss.SSS: ", Locale.getDefault());
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private LogListener mLogListener = new LogListener();
    private LogFile mLogFile;
    private volatile boolean mIsShow;

    public DebugNotification(Context context) {
        mIsShow = false;
        mContext = context;
        mLogFile = new LogFile();
    }

    /**
     * DebugNotification 활성화 여부
     * @param context
     * @return
     */
    private boolean isEnable(Context context) {
        try {
            DeviceManager deviceManager = new DeviceManager(context);
            if (deviceManager.isGantedPermissionReadExternalStorage() == false) {
                return false;
            }
            return isExistMagicDir();
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 조건에 맞는 디렉토리가 있는디 검사한다.
     * @return
     */
    private boolean isExistMagicDir() {
        File docDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File[] files = docDir.listFiles();
        if (files == null) return false;

        // 조건1 (리버스 엔지리어링 방어)
        // show me the snaps log 1234 -> SHA256
        final String magicDirNameSHA256 = "FBC9F39882A0F083ED42BBDC8656CAC11CA8FD9CA256262598B998A79C6FA016";  //show me the snaps log 1234

        String magicDirPath = "";
        int count = Math.min(files.length, 32);
        for (int i = 0; i < count; i++) {
            File file = files[i];
            if (file.isFile()) continue;

            String dirNameSHA256 = sha256(file.getName());
            if (dirNameSHA256.equals(magicDirNameSHA256)) {
                magicDirPath = file.getAbsolutePath();
                break;
            }
        }
        if (magicDirPath.length() == 0) return false;

        //조건2
        //월 * 일
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        String subDirName = String.valueOf(month * day);
        File subFile = new File(magicDirPath, subDirName);

        return subFile.exists();
    }

    /**
     * sha256
     * @param text
     * @return
     */
    private String sha256(String text) {
        String sha256 = "";
        try {
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(text.getBytes("utf8"));
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            sha256 = sb.toString().toUpperCase();
        } catch (Exception e) {
            Loggg.e(TAG, e);
        }
        return sha256;
    }

    /**
     * show
     */
    public void show() {
        if (isEnable(mContext) == false) {
            return;
        }

        if (mIsShow) {
            return;
        }

        File logFileDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), LOG_FILE_DIR_NAME);
        FileUtils.mkdirs(logFileDir.getAbsolutePath());
        mLogFile.create(logFileDir.getAbsolutePath());

        Intent resultIntent = new Intent(mContext, RenewalHomeActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int iconRes = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                com.snaps.mobile.R.drawable.ic_status_new : com.snaps.mobile.R.drawable.ic_status;
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(iconRes)
                .setContentTitle(TITLE_PREFIX)
                .setContentText("")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(false)
                .setVibrate(new long[]{0L})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            "NOTIFICATION_CHANNEL_NAME",
                            NotificationManager.IMPORTANCE_LOW);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());

        Loggg.addListener(mLogListener);

        mIsShow = true;
    }

    /**
     * hide
     */
    public void hide() {
        if (mIsShow == false) {
            return;
        }

        Loggg.removeListener(mLogListener);
        mLogFile.deleteLockFiles();
    }

    /**
     * title 설정
     * @param title
     */
    public void setTitle(String title) {
        if (mIsShow == false) {
            return;
        }

        mBuilder.setContentTitle(TITLE_PREFIX + title);
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    /**
     * 메시지를 설정한다.
     * @param time
     * @param msg
     */
    public void setMessage(long time, String msg) {
        if (mIsShow == false) {
            return;
        }

        mBuilder.setContentText(msg);
        mBuilder.setWhen(time);
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }

    class LogListener implements Loggg.Listener {
        @Override
        public void onWrite(int level, String msg) {
            String levelString;
            switch (level) {
                case Log.ASSERT:
                    levelString = "A";
                    break;

                case Log.VERBOSE:
                    levelString = "V";
                    break;

                case Log.INFO:
                    levelString = "I";
                    break;

                case Log.DEBUG:
                    levelString = "D";
                    break;

                case Log.WARN:
                    levelString = "W";
                    break;

                case Log.ERROR:
                    levelString = "E";
                    break;

                default:
                    levelString = "?";
            }

            Date date = new Date();
            SimpleDateFormat sf = (SimpleDateFormat) sLogTimeFormatter.clone();
            String message = sf.format(date) + levelString + "/" + msg;

            setMessage(date.getTime(), msg);

            mLogFile.write(message);
        }
    }
}
