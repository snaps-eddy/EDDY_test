package com.snaps.mobile.service.ai;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncPhotoServiceManager {
    private static final String TAG = SyncPhotoServiceManager.class.getSimpleName();

    //설정 시작
    public static final boolean IS_CHECK_SYNC_INIT_COMPLETE = false;
    public static final int END_TIME_WHEN_APP_IS_IN_BACKGROUND = 30;
    //설정 끝

    private volatile Context mContext;
    private volatile boolean mIsBindService;
    private volatile ChangeAppConfigObserverThread mChangeAppConfigObserverThread;
    private volatile ServiceLifecycleController mServiceLifecycleController;
    private volatile IPCClient mIPCClient;

    public interface ConnectionStatusListener extends IPCClient.ConnectionStatusListener {
    }

    public interface ProgressListener extends IPCClient.ProgressListener {
    }

    public interface LogListener extends IPCClient.LogListener {
    }

    private SyncPhotoServiceManager() {
        mContext = null;
        mIsBindService = false;
        mChangeAppConfigObserverThread = null;
        mServiceLifecycleController = null;

        mIPCClient = new IPCClient();
        mIPCClient.setExceptionListener(new MyExceptionListener());
    }

    public static SyncPhotoServiceManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final SyncPhotoServiceManager INSTANCE = new SyncPhotoServiceManager();
    }

    public void deleteAllDate(@NonNull Context context) {
        stopService();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Loggg.w(TAG, e);
        }

        String externalFilesDirPath = context.getExternalFilesDir(null).getAbsolutePath();
        String syncRoot = externalFilesDirPath + File.separator + SyncWorkingThread.SYNC_ROOT_DIR_NAME;
        FileUtils.deleteAllInDirectory(syncRoot);
    }

    /**
     * 서비스를 시작한다.
     * 앱이 서비스를 실행시키면 로그인 상태 및 기타 조건에 따라서 서비스 시작/종료를 제어한다.
     *
     * @param context
     * @param isRunByApp 앱이 서비스를 실행 시키면 true, Silent Push를 받아서 실행시키면 false
     * @param runTime
     */
    public void startService(@NonNull Context context, boolean isRunByApp, int runTime) {
        mContext = context;
        try {
            if (isRunByApp) {
                initControllerRunByApp(context, runTime);
            } else {
                startServiceInternal(context, isRunByApp, runTime);
            }
        } catch (Exception e) {
            Loggg.e(TAG, e);
        }
    }

    private void initControllerRunByApp(@NonNull Context context, int runTime) {
        if (mServiceLifecycleController != null) {
            return;
        }

        mServiceLifecycleController = new ServiceLifecycleController(new ServiceControlCommand());
        mServiceLifecycleController.start(context, runTime);

        mIPCClient.addConnectionStatusListener(mServiceLifecycleController);
        mIPCClient.createMessenger();

        mChangeAppConfigObserverThread = new ChangeAppConfigObserverThread(context);
        mChangeAppConfigObserverThread.start();
    }

    /**
     * 실제 서비스를 실행 시킨다.
     *
     * @param context
     * @param isRunByApp
     * @param runTime
     */
    private void startServiceInternal(Context context, boolean isRunByApp, int runTime) {
        boolean isAllowUploadMobileNetwork = isAllowUploadMobileNetwork(context);
        String userNo = SnapsLoginManager.getUUserNo(context);
        String deviceId = SystemUtil.getDeviceId(context);
        String AISyncPhotoDomain = (Config.isRealServer() ?
                SnapsAPI.AI_SYNC_PHOTO_REAL_DOMAIN : SnapsAPI.AI_SYNC_PHOTO_TEST_DOMAIN);
        String snapAPIDomain = SnapsAPI.DOMAIN();

        if (userNo == null || userNo.length() == 0) {
            Loggg.e(TAG, "userNo is null");
            return;
        }

        if (Config.useKorean() == false) {
            Loggg.e(TAG, "not korean");
            return;
        }

        if (Setting.getBoolean(context, Const_VALUE.KEY_SNAPS_AI, false) == false) {
            Loggg.e(TAG, "sync photo is disable");
            return;
        }

        Intent intent = new Intent(context, SyncPhotoService.class);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_IS_RUN_BY_APP, isRunByApp);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_AI_SYNC_PHOTO_DOMAIN, AISyncPhotoDomain);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_DEVICE_ID, deviceId);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_USER_NO, userNo);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_IS_ALLOW_UPLOAD_MOBILE_NETWORK, isAllowUploadMobileNetwork);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_SET_RUN_TIME, runTime);
        intent.putExtra(SyncPhotoService.INTENT_PARAM_KEY_SNAPS_API_DOMAIN, snapAPIDomain);

        context.startService(intent);

        if (isRunByApp) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Loggg.w(TAG, e);
            }

            //앱에 의해 시작된 경우만 리모트 서비스에 Bind
            intent = new Intent(context, SyncPhotoService.class);
            mIsBindService = context.bindService(intent, mIPCClient.getServiceConnection(), Context.BIND_NOT_FOREGROUND);
        }
    }

    /**
     * 서비스를 종료한다.
     * 앱이 서비스를 실행 시킨 경우, Silent Push를 받아서 실행 시킨 경우 관계 없이 서비스를 종료한다.
     */
    public void stopService() {
        try {
            stopServiceController(); //서비스 실행/종료 제어 기능을 종료한다.
            stopServiceInternal();
        } catch (Exception e) {
            Loggg.e(TAG, e);
        }
    }

    /**
     * 서비스 실행/종료 제어 기능을 종료한다.
     */
    private void stopServiceController() {
        if (mServiceLifecycleController != null) {
            mServiceLifecycleController.stop();
            mServiceLifecycleController = null;
        }
    }

    /**
     * 실제 서비스를 종료한다.
     */
    private void stopServiceInternal() {
        if (mChangeAppConfigObserverThread != null) {
            mChangeAppConfigObserverThread.forceStop();
            try {
                mChangeAppConfigObserverThread.interrupt();
            } catch (Exception e) {
                Loggg.e(TAG, e);
            }
            mChangeAppConfigObserverThread = null;
        }

        if (mIsBindService) {
            if (mContext != null) {
                unbindService(mContext);
            }
        }

        if (mContext != null) {
            Intent intent = new Intent(mContext, SyncPhotoService.class);
            try {
                mContext.stopService(intent);
            } catch (Exception e) {
                Loggg.e(TAG, e);
            }
        }
    }

    private void unbindService(Context context) {
        try {
            if (mIPCClient != null) {
                context.unbindService(mIPCClient.getServiceConnection());
            }
        } catch (Exception e) {
            Loggg.e(TAG, e);
        } finally {
            mIsBindService = false;
        }
    }

    private boolean isAllowUploadMobileNetwork(Context context) {
        return Setting.getBoolean(context, Const_VALUE.KEY_SNAPS_AI_ALLOW_UPLOAD_MOBILE_NET, false);
    }

    public boolean isRunning() {
        return mIPCClient.isRunning();
    }

    public void setProgressListener(ProgressListener listener) {
        mIPCClient.setProgressListener(listener);
    }

    public void setLogListener(LogListener listener) {
        mIPCClient.setLogListener(listener);
    }

    public void addConnectionStatusListener(ConnectionStatusListener listener) {
        mIPCClient.addConnectionStatusListener(listener);
    }

    public void removeConnectionStatusListener(ConnectionStatusListener listener) {
        mIPCClient.removeConnectionStatusListener(listener);
    }

    class ServiceControlCommand implements ServiceLifecycleController.ControlCommand {
        @Override
        public void start(Context context, boolean isRunByApp, int runTime) {
            startServiceInternal(context, isRunByApp, runTime);
        }

        @Override
        public void stop() {
            stopServiceInternal();
        }
    }

    class MyExceptionListener implements IPCClient.ExceptionListener {
        @Override
        public void onException(String msg, Throwable throwable) {
            stopServiceController();
            sendLogToServer(msg, throwable);
        }

        private void sendLogToServer(String msg, Throwable throwable) {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            final String LOG_TAG = "AI_SYNC";

            String userInfo = "";
            if (mContext != null) {
                String userID = SnapsLoginManager.getUserId(mContext);
                userInfo = userID + "(" + SnapsLoginManager.getUUserNo(mContext) + ")";
//                Crashlytics.setUserIdentifier(userInfo);
                crashlytics.setUserId(userInfo);
//                Crashlytics.log(3, LOG_TAG, "");
                crashlytics.log("");
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            String nowTime = sdf.format(date);
//            Crashlytics.log(3, LOG_TAG, "Report Date : " + nowTime);
            crashlytics.log("Report Date : " + nowTime);

//            Crashlytics.log(3, LOG_TAG, "userInfo : " + userInfo);
            crashlytics.log("userInfo : " + userInfo);
//            Crashlytics.log(3, LOG_TAG, "");
            crashlytics.log("");

            if (msg != null && msg.length() > 0) {
                String[] splitMsg = msg.split("\n");
                for (String line : splitMsg) {
//                    Crashlytics.log(3, LOG_TAG, line);
                    crashlytics.log(line);
                }
            }

//            Crashlytics.log(3, LOG_TAG, "");
//            Crashlytics.log(3, LOG_TAG, "####################");
            crashlytics.log("");
            crashlytics.log("####################");

            String throwableString = Log.getStackTraceString(throwable);
            String[] splitThrowableString = throwableString.split("\n");
            for (String line : splitThrowableString) {
//                Crashlytics.log(6, LOG_TAG, line);
                crashlytics.log(line);
            }

//            Crashlytics.setString("AI_SYNC_PHOTO_ERROR", throwable.toString());
            crashlytics.setCustomKey("AI_SYNC_PHOTO_ERROR", throwable.toString());
//            Crashlytics.logException(throwable);
            crashlytics.recordException(throwable);
        }
    }

    class ChangeAppConfigObserverThread extends Thread {
        private final String TAG = SyncPhotoServiceManager.TAG + "." + ChangeAppConfigObserverThread.class.getSimpleName();
        private static final int SLEEP_TIME_MILLISECOND = 250;
        private Context mContext;
        private boolean mIsAllowUploadMobileNetwork;
        private volatile boolean mIsRunning;

        public ChangeAppConfigObserverThread(Context context) {
            Loggg.d(TAG, "create instance()");
            mContext = context;
            mIsRunning = true;

            mIsAllowUploadMobileNetwork = isAllowUploadMobileNetwork(mContext);
        }

        public void forceStop() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            Loggg.d(TAG, "start run()");
            while (isInterrupted() == false && mIsRunning) {
                try {
                    Thread.sleep(SLEEP_TIME_MILLISECOND);
                } catch (InterruptedException e) {
                    break;
                }

                work();
            }
            Loggg.d(TAG, "stop run()");
        }

        private void work() {
            boolean isAllowUploadMobileNetwork = isAllowUploadMobileNetwork(mContext);

            if (mIsAllowUploadMobileNetwork != isAllowUploadMobileNetwork) {
                mIPCClient.setChangeAllowUploadMobileNetwork(isAllowUploadMobileNetwork);
            }

            mIsAllowUploadMobileNetwork = isAllowUploadMobileNetwork;
        }
    }
}
