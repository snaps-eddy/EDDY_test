package com.snaps.mobile.utils.network.retrofit2.util;


import android.content.Context;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsNetworkResponseContentType;

import retrofit2.Retrofit;

public class SnapsRetrofitAPIGenerator {
    private Context context;

    public static SnapsRetrofitAPIGenerator createGeneratorWithContext(Context context) {
        return new SnapsRetrofitAPIGenerator(context);
    }

    private SnapsRetrofitAPIGenerator(Context context) {
        this.context = context;
    }

    public <T> T generate(Class<T> serviceClass, eSnapsNetworkResponseContentType responseContentType) {
//        return generateWithBaseUrl(SnapsDomain.getDomain(eSnapsDomainType.API), serviceClass, responseContentType); //FIXME...나중에 API 리뉴얼할때만 사용할 것임.
        return generateWithBaseUrl(SnapsAPI.DOMAIN(), serviceClass, responseContentType);
    }

    //직접 baseUrl을 변경해야 하는 상황에 사용
    public <T> T generateWithBaseUrl(String baseUrl, Class<T> serviceClass, eSnapsNetworkResponseContentType responseContentType) {
        return getRetrofitWithBaseUrl(baseUrl, responseContentType).create(serviceClass);
    }

    private Retrofit getRetrofitWithBaseUrl(String baseUrl, eSnapsNetworkResponseContentType responseContentType) {
        SnapsRetrofitProvider snapsRetrofitManager = SnapsRetrofitProvider.getInstance();
        return snapsRetrofitManager.getRetrofitService(getContext(), baseUrl, responseContentType);
    }

    private Context getContext() {
        return context;
    }
}
