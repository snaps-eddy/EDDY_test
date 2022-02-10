package com.snaps.mobile.utils.network.retrofit2.interfacies;

import android.content.Context;
import androidx.annotation.NonNull;

import com.snaps.mobile.utils.network.provider.SnapsRetrofitRequestBuilder;
import com.snaps.mobile.utils.network.provider.listener.SnapsRetrofitResultListener;
import com.snaps.mobile.utils.network.retrofit2.data.response.common.SnapsNetworkAPIBaseResponse;

public interface ISnapsNetworkAPI {
    void request(@NonNull Context context);

    void request(@NonNull SnapsRetrofitRequestBuilder requestBuilder);

    //결과값을 받아 처리 해야 한다면
    <T extends SnapsNetworkAPIBaseResponse> void request(@NonNull SnapsRetrofitRequestBuilder requestBuilder,
                                                         SnapsRetrofitResultListener<T> resultListener);

    <T extends SnapsNetworkAPIBaseResponse> void request(@NonNull Context context, @NonNull String url,
                                                         SnapsRetrofitResultListener<T> resultListener);

    <T extends SnapsNetworkAPIBaseResponse> void request(@NonNull Context context,
                                                         SnapsRetrofitResultListener<T> resultListener);

    /**
     * 필요한 요청 형태가 있으면 작성해서 쓰자.
     */
}
