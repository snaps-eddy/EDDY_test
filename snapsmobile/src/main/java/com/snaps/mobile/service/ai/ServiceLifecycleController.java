package com.snaps.mobile.service.ai;

import android.content.Context;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;

/**
 * 서비스 실행/종료를 제어한다.
 */
class ServiceLifecycleController implements IPCClient.ConnectionStatusListener {
    private static final String TAG = ServiceLifecycleController.class.getSimpleName();
    private ControlCommand mControlCommand;
    private volatile WatchDogThread mWatchDogThread;

    public interface ControlCommand {
        void start(Context context, boolean isRunByApp, int runTime);
        void stop();
    }

    public ServiceLifecycleController(ControlCommand controlCommand) {
        mControlCommand = controlCommand;
        mWatchDogThread = null;
    }

    public void start(Context context, int runTime) {
        if (mWatchDogThread == null) {
            mWatchDogThread = new WatchDogThread(context, runTime);
            mWatchDogThread.start();
        }
    }

    public void stop() {
        if (mWatchDogThread != null) {
            mWatchDogThread.forceStop();
            try {
                mWatchDogThread.interrupt();
            }catch (Exception e) {
                Loggg.e(TAG, e);
            }
            mWatchDogThread = null;
        }
    }

    @Override
    public void onConnect() {
        if (mWatchDogThread != null) {
            mWatchDogThread.setConnectedService(true);
        }
    }

    @Override
    public void onDisconnect() {
        if (mWatchDogThread != null) {
            mWatchDogThread.setConnectedService(false);
        }
    }

    class WatchDogThread extends Thread {
        private final String TAG = ServiceLifecycleController.TAG + "." + WatchDogThread.class.getSimpleName();
        private static final int MAX_START_COUNT = 10;  //뭔가 잘못되었는데 계속 서비스를 무한히 실행 시키면 안되므로
        private static final int SLEEP_TIME_MILLISECOND = 250;
        private Context mContext;
        private DeviceManager mDeviceManager;
        private int mStartCount;
        private volatile boolean mIsRunning;
        private volatile boolean mIsConnectedService;
        private int mRunTime;

        public WatchDogThread(Context context, int runTime) {
            Loggg.d(TAG, "create instance()");
            mContext = context;
            mRunTime = runTime;
            mStartCount = 0;
            mIsRunning = true;
            mIsConnectedService = false;
            mDeviceManager = new DeviceManager(context);
        }

        public void setConnectedService(boolean isConnected) {
            mIsConnectedService = isConnected;
        }

        public void forceStop() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            Loggg.d(TAG, "start run()");
            while(isInterrupted() == false && mIsRunning) {
                try {
                    Thread.sleep(SLEEP_TIME_MILLISECOND);
                }catch (InterruptedException e) {
                    break;
                }

                try {
                    work();
                }catch (Exception e) {
                    Loggg.e(TAG, e);
                    break;
                }
            }
            Loggg.d(TAG, "stop run()");
        }

        private void work() {
            boolean isLogOn = SnapsLoginManager.isLogOn(mContext);
            boolean useKorean = Config.useKorean();
            boolean isEnableSyncPhoto = Setting.getBoolean(mContext, Const_VALUE.KEY_SNAPS_AI, false);

            if (isLogOn && useKorean && isEnableSyncPhoto) {
                startService();
            }
            else {
                stopService();
            }
        }

        private void startService() {
            if (mIsConnectedService) return;
            if (mStartCount == MAX_START_COUNT) return;
            if (mDeviceManager.isAppForeground() == false) return;

            mStartCount++;

            Loggg.d(TAG, "startService()");
            mControlCommand.start(mContext, true, mRunTime);
            try {
                Thread.sleep(250);
            }catch (InterruptedException e) {
            }
        }

        private void stopService() {
            if (mIsConnectedService == false) return;

            Loggg.d(TAG, "stopService()");
            mControlCommand.stop();
            try {
                Thread.sleep(250);
            }catch (InterruptedException e) {
            }
        }
    }
}
