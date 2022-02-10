package com.snaps.mobile.utils.network.provider.listener;

import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIBaseResponse;
import com.snaps.mobile.utils.network.retrofit2.exception.SnapsNetworkThrowable;

public abstract class SnapsRetrofitResultListener<T extends SnapsNetworkAPIBaseResponse> extends SnapsNetworkAPIBaseResponse {
    private boolean isErrorProcessed = false; //에러 처리를 한번만 하기 위해..

    public void onPrepare() {
        //API 통신 하기 전에 호출되니, 필요하면 오버라이딩 해서 쓰세요
    }

    public abstract void onResultSuccess(T result);
    public abstract void onResultFailed(SnapsNetworkThrowable throwable);

    public boolean isErrorProcessed() {
        return isErrorProcessed;
    }

    public void setErrorProcessed(boolean errorProcessed) {
        isErrorProcessed = errorProcessed;
    }
}
