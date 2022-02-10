package com.snaps.mobile.order.order_v2.interfacies;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public interface SnapsOrderResultListener {
    void onSnapsOrderResultSucceed(Object resultMsgObj);
    void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType);
}
