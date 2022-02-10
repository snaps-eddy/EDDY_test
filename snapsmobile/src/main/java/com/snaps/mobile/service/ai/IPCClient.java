package com.snaps.mobile.service.ai;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * 리모트 서비스와 통신
 */
class IPCClient {
    private static final String TAG = IPCClient.class.getSimpleName();
    private volatile MyServiceConnection mMyServiceConnection;
    private volatile Messenger mMessengerServer = null;
    private volatile Messenger mMessenger = null;
    private volatile List<ConnectionStatusListener> mConnectionStatusListenerList;
    private volatile ProgressListener mProgressListener;
    private volatile LogListener mLogListener;
    private volatile ExceptionListener mExceptionListener;

    /**
     * 리모트 서비스 연결 상태 리스너
     */
    interface ConnectionStatusListener {
        void onConnect();
        void onDisconnect();
    }

    /**
     * AI 사진 업로드 전체 진행 상태 프로그래스바 (Exif 생성부터 전체 진행 상태)
     */
    interface ProgressListener {
        void onChangeProgress(int percent);
    }

    interface LogListener {
        void logEvent(int level, String msg);
    }

    interface ExceptionListener {
        void onException(String msg, Throwable throwable);
    }

    public IPCClient() {
        mConnectionStatusListenerList = new ArrayList<ConnectionStatusListener>();
        mMyServiceConnection = new MyServiceConnection();
        mProgressListener = null;
    }

    public boolean isRunning() {
        return mMessengerServer != null;
    }

    public ServiceConnection getServiceConnection() {
        return mMyServiceConnection;
    }

    public void createMessenger() {
        if (mMessenger == null) {
            mMessenger = new Messenger(new CallbackHandler());
        }
    }

    public void setProgressListener(ProgressListener listener) {
        mProgressListener = listener;
        if (isRunning() == false) return;

        int msgWhat = (listener == null ? IPCServer.MSG_DISABLE_PROGRESS_BAR : IPCServer.MSG_ENABLE_PROGRESS_BAR);
        sendMessage(msgWhat);
    }

    public void setLogListener(LogListener listener) {
        mLogListener = listener;
        if (isRunning() == false) return;

        int msgWhat = (listener == null ? IPCServer.MSG_DISABLE_LOG_LISTENER : IPCServer.MSG_ENABLE_LOG_LISTENER);
        sendMessage(msgWhat);
    }

    public void setExceptionListener(ExceptionListener listener) {
        mExceptionListener = listener;
    }

    private void notifyConnectionStatus(boolean isConnected) {
        synchronized (mConnectionStatusListenerList) {
            for(ConnectionStatusListener listener : mConnectionStatusListenerList) {
                if (isConnected) {
                    listener.onConnect();
                }
                else {
                    listener.onDisconnect();
                }
            }
        }
    }

    public void addConnectionStatusListener(ConnectionStatusListener listener) {
        synchronized (mConnectionStatusListenerList) {
            if (mConnectionStatusListenerList.contains(listener) == false) {
                mConnectionStatusListenerList.add(listener);
            }
        }
    }

    public void removeConnectionStatusListener(ConnectionStatusListener listener) {
        synchronized (mConnectionStatusListenerList) {
            if (mConnectionStatusListenerList.contains(listener)) {
                mConnectionStatusListenerList.remove(listener);
            }
        }
    }

    public void setStopServiceDelay(int delay) {
        sendMessage(IPCServer.MSG_SET_STOP_SERVICE_DELAY, delay);
    }

    public void setChangeAllowUploadMobileNetwork(boolean isAllowUploadMobileNetwork) {
        sendMessage(IPCServer.MSG_CHANGE_IS_ALLOW_UPLOAD_MOBILE_NETWORK, isAllowUploadMobileNetwork);
    }

    // https://codechacha.com/ko/remoteservice-messenger/
    class CallbackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String receiveMsg;
            String logMsg;

            switch(msg.what) {
                case IPCServer.MSG_NOTIFY_CLIENT_TO_CLOSE_CONNECTION:
                    Loggg.d(TAG, "receive MSG_NOTIFY_CLIENT_TO_CLOSE_CONNECTION");
                    mMessengerServer = null;
                    notifyConnectionStatus(false);
                    break;

                case IPCServer.MSG_SERVER_SHUTDOWN_NOTIFICATION:
                    Loggg.d(TAG, "receive MSG_SERVER_SHUTDOWN_NOTIFICATION");
                    mMessengerServer = null;
                    notifyConnectionStatus(false);
                    break;

                case IPCServer.MSG_UPDATE_PROGRESS_BAR:
                    int progress = msg.arg1;
                    Loggg.d(TAG, "receive MSG_UPDATE_PROGRESS_BAR : " + progress);
                    if (mProgressListener != null) {
                        mProgressListener.onChangeProgress(progress);
                    }
                    break;

                case IPCServer.MSG_LOG_EVENT:
                    int level = msg.arg1;
                    String log = msg.getData().getString("String", "");
                    //주의!!! 로그 메시지 처리하면서 로그 출력하면 안됨. 무한 루프에 빠진다
                    if (mLogListener != null) {
                        mLogListener.logEvent(level, log);
                    }
                    break;

                case IPCServer.MSG_NOTIFY_EXCEPTION:
                    receiveMsg = msg.getData().getString("msg");
                    Throwable throwable = (Throwable)msg.getData().getSerializable("exception");
                    logMsg = "receive MSG_NOTIFY_EXCEPTION : " + receiveMsg + ", " + throwable;
                    Loggg.d(TAG, logMsg);
                    if (mExceptionListener != null) {
                        mExceptionListener.onException(receiveMsg, throwable);
                    }
                    break;

                default:
                    Loggg.w(TAG, "receive unknown message : " + msg.toString());
                    break;
            }
        }
    }

    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Loggg.d(TAG, "onServiceConnected");
            mMessengerServer = new Messenger(service);

            Message msg = Message.obtain(null, IPCServer.MSG_CLIENT_ATTEMPTS_TO_CONNECT);
            msg.replyTo = mMessenger;   //수신 할 것 설정
            try {
                mMessengerServer.send(msg);
            } catch (RemoteException e) {
                Loggg.e(TAG, e);
            }

            //앱이 시작되면 기존의 종료 타이머를 취소환다.
            sendMessage(IPCServer.MSG_CANCEL_STOP_SERVICE_DELAY, 0);

            //연결이 안되어있을때 설정한 리스너 등록
            if (mProgressListener != null) {
                sendMessage(IPCServer.MSG_ENABLE_PROGRESS_BAR);
            }

            //연결이 안되어있을때 설정한 리스너 등록
            if (mLogListener != null) {
                sendMessage(IPCServer.MSG_ENABLE_LOG_LISTENER);
            }

            notifyConnectionStatus(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Loggg.d(TAG, "onServiceDisconnected");
            mMessengerServer = null;
            notifyConnectionStatus(false);
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Loggg.d(TAG, "onBindingDied");
            mMessengerServer = null;
            notifyConnectionStatus(false);
        }
    }

    public boolean sendMessage(Message msg) {
        if (mMessengerServer == null) {
            return false;
        }

        try {
            mMessengerServer.send(msg);
            return true;
        }catch (Exception e) {
            Loggg.e(TAG, e);
        }
        return false;
    }

    public boolean sendMessage(int msgWhat) {
        Message msg = Message.obtain(null, msgWhat);
        return sendMessage(msg);
    }

    public boolean sendMessage(int msgWhat, boolean value) {
        Message msg = Message.obtain(null, msgWhat);
        msg.arg1 = (value ? 1 : 0);
        return sendMessage(msg);
    }

    public boolean sendMessage(int msgWhat, int value) {
        Message msg = Message.obtain(null, msgWhat);
        msg.arg1 = value;
        return sendMessage(msg);
    }
}
