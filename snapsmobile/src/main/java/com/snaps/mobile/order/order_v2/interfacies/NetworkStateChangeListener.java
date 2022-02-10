package com.snaps.mobile.order.order_v2.interfacies;

import android.net.NetworkInfo;

/**
 * Created by ysjeong on 2017. 4. 7..
 */

public interface NetworkStateChangeListener {
    void onChangeNetworkState(NetworkInfo networkInfo);
}
