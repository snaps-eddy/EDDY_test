package com.snaps.mobile.service.ai;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.snaps.common.utils.net.http.HttpReq;

import java.io.File;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class SyncPhotoService extends Service {
    private static final String TAG = SyncPhotoService.class.getSimpleName();
    public static final String INTENT_PARAM_KEY_DEVICE_ID = "DEVICE_ID";
    public static final String INTENT_PARAM_KEY_USER_NO = "USER_NO";
    public static final String INTENT_PARAM_KEY_IS_ALLOW_UPLOAD_MOBILE_NETWORK = "IS_ALLOW_UPLOAD_MOBILE_NETWORK";
    public static final String INTENT_PARAM_KEY_IS_RUN_BY_APP = "IS_RUN_BY_APP";
    public static final String INTENT_PARAM_KEY_SET_RUN_TIME = "SET_RUN_TIME";
    public static final String INTENT_PARAM_KEY_SNAPS_API_DOMAIN = "SNAPS_API_DOMAIN";
    public static final String INTENT_PARAM_KEY_AI_SYNC_PHOTO_DOMAIN = "AI_SYNC_PHOTO_DOMAIN";
    private volatile SyncWorkingThread mSyncWorkingThread = null;
    private WiFiStateChangeReceiver mWiFiStateChangeReceiver = null;
    private volatile StopServiceTimer mStopServiceTimer = null;
    private volatile AppOnForegroundChecker mAppOnForegroundChecker = null;
    private IPCServer mIPCServer;
    private String mSnapsAPIDomain = "";
    private DebugNotification mDebugNotification;

    @Override
    public void onCreate() {
        Loggg.d(TAG, "onCreate()");

        Context context = getApplicationContext();

        mDebugNotification = new DebugNotification(context);

        mIPCServer = new IPCServer(context, new StopTimerCommand(), new ChangeAppConfigListener());

        mStopServiceTimer = null;
        mAppOnForegroundChecker = null;

        startWiFiStateChangeReceiver();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Loggg.d(TAG, "onStartCommand()" + " intent=" + intent + " ,flags=" + flags + " ,startId=" + startId);

        if (mSyncWorkingThread != null) {
            Loggg.e(TAG, "The service has already started.");
            return START_NOT_STICKY;
        }

        writeLogStart();

        ///////////////////////////////////////////////////////////////////////
        boolean isRunByApp = intent.getBooleanExtra(INTENT_PARAM_KEY_IS_RUN_BY_APP,false);
        boolean isAllowUploadMobileNetwork = intent.getBooleanExtra(INTENT_PARAM_KEY_IS_ALLOW_UPLOAD_MOBILE_NETWORK,false);
        int runTime = intent.getIntExtra(INTENT_PARAM_KEY_SET_RUN_TIME,0);
        String deviceId = intent.getStringExtra(INTENT_PARAM_KEY_DEVICE_ID);
        String userNo = intent.getStringExtra(INTENT_PARAM_KEY_USER_NO);
        String AISyncPhotoDomain = intent.getStringExtra(INTENT_PARAM_KEY_AI_SYNC_PHOTO_DOMAIN);
        mSnapsAPIDomain = intent.getStringExtra(INTENT_PARAM_KEY_SNAPS_API_DOMAIN);

        Loggg.d(TAG, "isRunByApp : " + isRunByApp);
        Loggg.d(TAG, "deviceID : " + deviceId);
        Loggg.d(TAG, "userNo : " + userNo);
        Loggg.d(TAG, "isAllowUploadMobileNetwork : " + isAllowUploadMobileNetwork);
        Loggg.d(TAG, "runTime : " + runTime);
        Loggg.d(TAG, "AISyncPhotoDomain : " + AISyncPhotoDomain);
        Loggg.d(TAG, "snapsAPIDomain : " + mSnapsAPIDomain);
        ///////////////////////////////////////////////////////////////////////

        Context context = getApplicationContext();

        PerformanceMeasurementTool.resetAll();

        if (userNo == null || userNo.length() == 0) {
            Loggg.e(TAG, "userNo is null");
            return START_NOT_STICKY;
        }

        if (AISyncPhotoDomain == null || AISyncPhotoDomain.length() == 0) {
            Loggg.e(TAG, "AISyncPhotoDomain is invalid");
            return START_NOT_STICKY;
        }

        if (mSnapsAPIDomain == null || mSnapsAPIDomain.length() == 0) {
            Loggg.e(TAG, "snapsAPIDomain is invalid");
            return START_NOT_STICKY;
        }

        AppConfigClone.getInstance().setAllowUploadMobileNetwork(isAllowUploadMobileNetwork);

        Monitoring.getInstance().init(context, userNo);
        Monitoring.getInstance().setListener(new MonitoringEventListener());
        Monitoring.getInstance().setStartTime();

        mSyncWorkingThread = new SyncWorkingThread(
                context,
                AISyncPhotoDomain,
                userNo,
                deviceId,
                new SyncWorkingThreadEventListener());
        mSyncWorkingThread.start();

        //실행 시간 설정
        if (runTime > 0) {
            Loggg.d(TAG, "set runTime : " + runTime);
            startShutdownTimer(runTime);
        }

        if (isRunByApp) {
            startAppOnForegroundChecker(context);
            mDebugNotification.show();
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Loggg.d(TAG, "onBind()" + " intent=" + intent);
        return mIPCServer.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Loggg.d(TAG, "onUnbind()" + " intent=" + intent);
        mIPCServer.sendClose();
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Loggg.d(TAG, "onRebind()" + " intent=" + intent);
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        Loggg.w(TAG, "onTaskRemoved()" + " intent=" + intent);
        stopSelf();
    }

    @Override
    public void onLowMemory() {
        Loggg.e(TAG, "onLowMemory()");
        stopSelf();
    }

    @Override
    public void onTrimMemory(int level) {
        Loggg.d(TAG, "onTrimMemory() : " + level);
    }

    @Override
    public void onDestroy() {
        Loggg.d(TAG, "onDestroy()");

        Monitoring.getInstance().setFinishTime();

        mIPCServer.sendShutdown();

        mDebugNotification.hide();

        stopWiFiStateChangeReceiver();

        stopShutdownTimer();

        stopAppOnForegroundChecker();

        if (mSyncWorkingThread != null) {
            mSyncWorkingThread.forceStop();
            try {
                mSyncWorkingThread.interrupt();
            } catch (Exception e) {
                Loggg.w(TAG, e);
            }
            try {
                mSyncWorkingThread.join(500);
            } catch (Exception e) {
                Loggg.w(TAG, e);
            }
            mSyncWorkingThread = null;
        }

        writeLogFinish();
    }

    private void startAppOnForegroundChecker(Context context) {
        if (mAppOnForegroundChecker != null) return;

        mAppOnForegroundChecker = new AppOnForegroundChecker(context);
        mAppOnForegroundChecker.start();
    }

    private void stopAppOnForegroundChecker() {
        if (mAppOnForegroundChecker == null) return;

        mAppOnForegroundChecker.forceStop();
        try {
            mAppOnForegroundChecker.interrupt();
        } catch (Exception e) {
            Loggg.w(TAG, e);
        }
        mAppOnForegroundChecker = null;
    }


    private void startWiFiStateChangeReceiver() {
        if (mWiFiStateChangeReceiver != null) return;

        mWiFiStateChangeReceiver = new WiFiStateChangeReceiver();
        mWiFiStateChangeReceiver.setWiFiStateChangeListener(new WiFiListener());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            registerReceiver(mWiFiStateChangeReceiver, filter);
        } catch (Exception e) {
            Loggg.e(TAG, e);
        }
    }

    private void stopWiFiStateChangeReceiver() {
        if (mWiFiStateChangeReceiver == null) return;

        mWiFiStateChangeReceiver.setWiFiStateChangeListener(null);
        try {
            unregisterReceiver(mWiFiStateChangeReceiver);
        } catch (Exception e) {
            Loggg.w(TAG, e);
        }
        mWiFiStateChangeReceiver = null;
    }

    private void startShutdownTimer(int delaySecond) {
        if (delaySecond == 0) {
            stopShutdownTimer();
            stopSelf();
            return;
        }

        //작업 스레드가 이미 종료 되었다면 종료 타이머를 돌리면 안된다.
        if (mSyncWorkingThread != null) {
            if (mStopServiceTimer == null) {
                mStopServiceTimer = new StopServiceTimer(delaySecond);
                mStopServiceTimer.start();
            }
        }
    }

    private void stopShutdownTimer() {
        if (mStopServiceTimer != null) {
            mStopServiceTimer.forceStop();
            try {
                mStopServiceTimer.interrupt();
            }catch (Exception e) {
                Loggg.w(TAG, e);
            }
            mStopServiceTimer = null;
        }
    }

    private void forceStopNetwork() {
        if (mSyncWorkingThread == null) {
            return;
        }

        if (mSyncWorkingThread.isAlive()) {
            mSyncWorkingThread.forceStopNetwork();
        }
    }

    class StopTimerCommand implements IPCServer.StopTimerCommand {
        private final String TAG = SyncPhotoService.TAG + "." + StopTimerCommand.class.getSimpleName();

        @Override
        public void start(int second) {
            Loggg.d(TAG, "start()");
            startShutdownTimer(second);
        }

        @Override
        public void stop() {
            Loggg.d(TAG, "stop()");
            stopShutdownTimer();
        }
    }

    class AppOnForegroundChecker extends Thread {
        private final String TAG = SyncPhotoService.TAG + "." + AppOnForegroundChecker.class.getSimpleName();
        private volatile boolean mIsRunning;
        private DeviceManager mDeviceManager;

        public AppOnForegroundChecker(Context context) {
            Loggg.d(TAG, "create instance()");
            mIsRunning = true;
            mDeviceManager = new DeviceManager(context);
        }

        public void forceStop() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            Loggg.d(TAG, "start run()");
            boolean isPreAppForeground = mDeviceManager.isAppForeground();

            while(isInterrupted() == false && mIsRunning) {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    break;
                }

                boolean isAppForeground = mDeviceManager.isAppForeground();
                if (isPreAppForeground != isAppForeground) {
                    Loggg.d(TAG, "App status changed : " + (isAppForeground ? "Foreground" : "Background"));
                    if (isAppForeground) {
                        stopShutdownTimer();
                    }
                    else {
                        //앱이 포그라운드가 아니면 일정 시간 후 작업을 종료한다.
                        startShutdownTimer(SyncPhotoServiceManager.END_TIME_WHEN_APP_IS_IN_BACKGROUND);
                    }
                }
                isPreAppForeground = isAppForeground;
            }
            Loggg.d(TAG, "stop run()");
        }
    }

    class ChangeAppConfigListener implements IPCServer.ChangeAppConfigListener {
        private final String TAG = SyncPhotoService.TAG + "." + ChangeAppConfigListener.class.getSimpleName();

        @Override
        public void onChangeAllowUploadMobileNetwork(boolean isAllowUploadMobileNetwork) {
            if (isAllowUploadMobileNetwork == false) {
                DeviceManager deviceManager = new DeviceManager(getApplicationContext());
                if (deviceManager.isMobileNetworkConnected()) {
                    forceStopNetwork();
                }
            }
        }
    }

    class StopServiceTimer extends Thread {
        private final String TAG = SyncPhotoService.TAG + "." + StopServiceTimer.class.getSimpleName();
        private volatile boolean mIsRunning;
        private long mDelaySecond;

        public StopServiceTimer(long delaySecond) {
            Loggg.d(TAG, "create instance()");
            mIsRunning = true;
            mDelaySecond = delaySecond;
        }

        public void forceStop() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            Loggg.d(TAG, "start run()");
            while(isInterrupted() == false && mIsRunning) {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                    return;
                }

                mDelaySecond--;
                if (mDelaySecond <= 0) {
                    stopSelf();
                    break;
                }
                else {
                    Loggg.d(TAG, "▶ Exits after " + mDelaySecond + " seconds");
                }
            }
            Loggg.d(TAG, "stop run()");
        }
    }

    class MonitoringEventListener implements Monitoring.EventListener {
        private final String TAG = SyncPhotoService.TAG + "." + MonitoringEventListener.class.getSimpleName();

        @Override
        public void onUncatchedException(String msg, Throwable throwable) {
            mIPCServer.sendException(msg, throwable);
        }
    }


    class SyncWorkingThreadEventListener implements SyncWorkingThread.EventListener {
        private final String TAG = SyncPhotoService.TAG + "." + SyncWorkingThreadEventListener.class.getSimpleName();

        @Override
        public void onSentExifZipFile(boolean isInit, String userNo, String deviceId) {
            /*
            if (isInit) {
                try {
                    String response = HttpReq.requestPutAIInit(mSnapsAPIDomain, userNo, deviceId,
                            SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    Loggg.d(TAG, "HttpReq.requestPutAIInit() : " + response);
                } catch (Exception e) {
                    Loggg.e(TAG, e);
                }
            }
            */
        }

        @Override
        public void onChangeWorkStatus(SyncWorkingThread.WorkStatus workStatus) {
            mDebugNotification.setTitle(workStatus.toString());
        }

        @Override
        public void onStop() {
            stopSelf();
        }

        @Override
        public void onUncatchedException(String msg, Throwable throwable) {
            mIPCServer.sendException(msg, throwable);
        }
    }

    class WiFiListener implements WiFiStateChangeReceiver.Listener {
        private final String TAG = SyncPhotoService.TAG + "." + WiFiListener.class.getSimpleName();

        @Override
        public void onWiFiOff() {
            if (AppConfigClone.getInstance().isAllowUploadMobileNetwork() == false) {
                forceStopNetwork();
            }
        }
    }

    private void writeLogStart() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("███████╗████████╗ █████╗ ██████╗ ████████╗").append("\n");
        sb.append("██╔════╝╚══██╔══╝██╔══██╗██╔══██╗╚══██╔══╝").append("\n");
        sb.append("███████╗   ██║   ███████║██████╔╝   ██║").append("\n");
        sb.append("╚════██║   ██║   ██╔══██║██╔══██╗   ██║").append("\n");
        sb.append("███████║   ██║   ██║  ██║██║  ██║   ██║").append("\n");
        sb.append("╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝").append("\n\n");
        Loggg.d(TAG, sb.toString());
    }

    private void writeLogFinish() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("███████╗██╗███╗   ██╗██╗███████╗██╗  ██╗").append("\n");
        sb.append("██╔════╝██║████╗  ██║██║██╔════╝██║  ██║").append("\n");
        sb.append("█████╗  ██║██╔██╗ ██║██║███████╗███████║").append("\n");
        sb.append("██╔══╝  ██║██║╚██╗██║██║╚════██║██╔══██║").append("\n");
        sb.append("██║     ██║██║ ╚████║██║███████║██║  ██║").append("\n");
        sb.append("╚═╝     ╚═╝╚═╝  ╚═══╝╚═╝╚══════╝╚═╝  ╚═╝").append("\n\n");
        Loggg.d(TAG, sb.toString());
    }
}