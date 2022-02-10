package com.snaps.mobile.service.ai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * WiFi 연결 종료를 감지한다.
 */
class WiFiStateChangeReceiver extends BroadcastReceiver {
    private static final String TAG = WiFiStateChangeReceiver.class.getSimpleName();
    private volatile boolean mIsConnectedWiFi = true;   //초기값이 아주 중요!! false로 하면 안됨
    private volatile ConnectivityManager mConnectivityManager = null;
    private volatile Listener mListener;

    public interface Listener {
        void onWiFiOff();
    }

    public void setWiFiStateChangeListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mConnectivityManager == null) {
            if (context == null) return;
            mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager == null) return;
        }

        NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isCurrentConnectedWiFi = networkInfo.isConnected();

        if (mIsConnectedWiFi != isCurrentConnectedWiFi) {
            if (isCurrentConnectedWiFi == false) {
                if (mListener != null) {
                    Loggg.d(TAG, "onWiFiOff");
                    mListener.onWiFiOff();
                }
            }
        }

        mIsConnectedWiFi = isCurrentConnectedWiFi;
    }
}
