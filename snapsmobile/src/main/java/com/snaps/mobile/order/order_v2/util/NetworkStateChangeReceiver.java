package com.snaps.mobile.order.order_v2.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.snaps.mobile.order.order_v2.interfacies.NetworkStateChangeListener;

/**
 * Created by ysjeong on 2017. 4. 7..
 */

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private NetworkStateChangeListener networkStateChangeListener = null;

    private long lastReceiveTime = 0l;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) return;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        //연속으로 들어오는 값은 무시하기 위해
        if (System.currentTimeMillis() - lastReceiveTime < 1000) return;
        lastReceiveTime = System.currentTimeMillis();

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (getNetworkStateChangeListener() != null)
            getNetworkStateChangeListener().onChangeNetworkState(activeNetInfo);
    }

    public NetworkStateChangeListener getNetworkStateChangeListener() {
        return networkStateChangeListener;
    }

    public void setNetworkStateChangeListener(NetworkStateChangeListener networkStateChangeListener) {
        this.networkStateChangeListener = networkStateChangeListener;
    }
}
