package com.snaps.mobile.service.ai;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.Serializable;

/**
 * 리모트 서비스(나)에 연결된 클라이언트와 통신
 */
class IPCServer {
    private static final String TAG = IPCServer.class.getSimpleName();
    public static final int MSG_CLIENT_ATTEMPTS_TO_CONNECT = 10;
    public static final int MSG_NOTIFY_CLIENT_TO_CLOSE_CONNECTION = 11;
    public static final int MSG_SERVER_SHUTDOWN_NOTIFICATION = 12;
    public static final int MSG_SET_STOP_SERVICE_DELAY = 20;
    public static final int MSG_CANCEL_STOP_SERVICE_DELAY = 21;
    public static final int MSG_ENABLE_PROGRESS_BAR = 30;
    public static final int MSG_DISABLE_PROGRESS_BAR = 31;
    public static final int MSG_UPDATE_PROGRESS_BAR = 32;
    public static final int MSG_ENABLE_LOG_LISTENER = 40;
    public static final int MSG_DISABLE_LOG_LISTENER = 41;
    public static final int MSG_LOG_EVENT = 42;
    public static final int MSG_CHANGE_IS_ALLOW_UPLOAD_MOBILE_NETWORK = 52;
    public static final int MSG_NOTIFY_EXCEPTION = 101;
    private Context mContext;
    private volatile Messenger mMessengerClient;
    private Messenger mMessenger;
    private ProgressListener mProgressListener;
    private LogListener mLogListener;
    private StopTimerCommand mStopTimerCommand;
    private ChangeAppConfigListener mChangeAppConfigListener;

    /**
     * 리모트 서비스(나) 종료 타이머 시작/종료 커맨드
     */
    interface StopTimerCommand {
        void start(int second);
        void stop();
    }

    interface ChangeAppConfigListener {
        void onChangeAllowUploadMobileNetwork(boolean isAllowUploadMobileNetwork);
    }

    public IPCServer(Context context,
                     StopTimerCommand stopTimerCommand, ChangeAppConfigListener changeAppConfigListener)
    {
        mContext = context;
        mLogListener = new LogListener();
        mMessenger = new Messenger(new CallbackHandler());
        mStopTimerCommand = stopTimerCommand;
        mChangeAppConfigListener = changeAppConfigListener;

        mProgressListener = new ProgressListener();
        OverallProgress.getInstance().setListener(mProgressListener);
    }

    public IBinder getBinder() {
        return mMessenger.getBinder();
    }

    public void sendClose() {
        sendMessage(MSG_NOTIFY_CLIENT_TO_CLOSE_CONNECTION);
        mMessengerClient = null;
    }

    public void sendShutdown() {
        sendMessage(MSG_SERVER_SHUTDOWN_NOTIFICATION);
    }

    // https://codechacha.com/ko/remoteservice-messenger/
    class CallbackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_CLIENT_ATTEMPTS_TO_CONNECT:
                    Loggg.d(TAG, "receive MSG_CLIENT_ATTEMPTS_TO_CONNECT");
                    mMessengerClient = msg.replyTo; //연결된 클라이언트 저장
                    break;

                case MSG_ENABLE_PROGRESS_BAR:
                    Loggg.d(TAG, "receive MSG_REQUEST_PROGRESS_BAR");
                    OverallProgress.getInstance().setActive(true);
                    break;

                case MSG_DISABLE_PROGRESS_BAR:
                    Loggg.d(TAG, "receive MSG_CANCEL_REQUEST_PROGRESS_BAR");
                    OverallProgress.getInstance().setActive(false);
                    break;

                case MSG_ENABLE_LOG_LISTENER:
                    Loggg.d(TAG, "receive MSG_ENABLE_LOG_LISTENER");
                    Loggg.addListener(mLogListener);
                    break;

                case MSG_DISABLE_LOG_LISTENER:
                    Loggg.d(TAG, "receive MSG_DISABLE_LOG_LISTENER");
                    Loggg.removeListener(mLogListener);
                    break;

                case MSG_SET_STOP_SERVICE_DELAY:
                    int second = msg.arg1;
                    Loggg.d(TAG, "receive MSG_SET_STOP_SERVICE_DELAY : " + second);
                    mStopTimerCommand.start(second);
                    break;

                case MSG_CANCEL_STOP_SERVICE_DELAY:
                    Loggg.d(TAG, "receive MSG_CANCEL_STOP_SERVICE_DELAY");
                    mStopTimerCommand.stop();
                    break;

                case MSG_CHANGE_IS_ALLOW_UPLOAD_MOBILE_NETWORK:
                    boolean isAllowUploadMobileNetwork = (msg.arg1 == 1);
                    Loggg.d(TAG, "receive MSG_CHANGE_IS_ALLOW_UPLOAD_MOBILE_NETWORK : " + isAllowUploadMobileNetwork);
                    AppConfigClone.getInstance().setAllowUploadMobileNetwork(isAllowUploadMobileNetwork);
                    mChangeAppConfigListener.onChangeAllowUploadMobileNetwork(isAllowUploadMobileNetwork);
                    break;

                default:
                    Loggg.w(TAG, "receive unknown message : " + msg.toString());
                    break;
            }
        }
    }

    private boolean sendMessage(Message msg) {
        if (mMessengerClient == null) return false;

        try {
            mMessengerClient.send(msg);
            return true;
        }catch (RemoteException e) {
            Loggg.e(TAG, e);
        }
        return false;
    }

    private void sendMessage(int msgWhat) {
        Message msg = Message.obtain(null, msgWhat);
        sendMessage(msg);
    }

    private void sendMessage(int msgWhat, int value) {
        Message msg = Message.obtain(null, msgWhat);
        msg.arg1 = value;
        sendMessage(msg);
    }

    private void sendLogMessage(int value, String text) {
        //로그를 출력하다가 에러가 발생해서 로그를 출력하면 무한 반복 되므로 별도로 만듬
        Bundle data = new Bundle();
        data.putString("String", text);
        Message msg = Message.obtain(null, MSG_LOG_EVENT);
        msg.arg1 = value;
        msg.setData(data);

        sendMessage(msg);
    }

    public void sendException(String msg, Throwable throwable) {
        Bundle data = new Bundle();
        data.putString("msg", msg);
        data.putSerializable("exception", (Serializable)throwable);
        Message message = Message.obtain(null, MSG_NOTIFY_EXCEPTION);
        message.setData(data);
        sendMessage(message);
    }

    class ProgressListener implements SyncPhotoServiceManager.ProgressListener {
        @Override
        public void onChangeProgress(int perent) {
            sendMessage(MSG_UPDATE_PROGRESS_BAR, perent);
        }
    }

    class LogListener implements Loggg.Listener {
        @Override
        public void onWrite(int level, String msg) {
            sendLogMessage(level, msg);
        }
    }
}
