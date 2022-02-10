package com.snaps.mobile.utils.network.retrofit2.genetator.etc;

import android.content.Context;
import androidx.annotation.NonNull;

import com.snaps.mobile.utils.network.provider.SnapsRetrofitRequestBuilder;
import com.snaps.mobile.utils.network.provider.listener.SnapsRetrofitResultListener;
import com.snaps.mobile.utils.network.retrofit2.exception.SnapsNetworkThrowable;
import com.snaps.mobile.utils.network.retrofit2.genetator.SnapsNetworkGenerateBase;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsNetworkResponseContentType;

import errorhandle.SnapsAssert;
import io.reactivex.Flowable;

public class SnapsNetworkGenerateImpErr extends SnapsNetworkGenerateBase {

    @Override
    protected Flowable generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
        SnapsAssert.assertException(new Exception("##### develop error!! you must implement SnapsNetworkService"));
        return null;
    }

    @Override
    public eSnapsNetworkResponseContentType getResponseContentType() {
        return eSnapsNetworkResponseContentType.JSON;
    }

    @Override
    public void request(@NonNull SnapsRetrofitRequestBuilder requestBuilder, SnapsRetrofitResultListener resultListener) {
        SnapsAssert.assertException(new Exception("##### develop error!! you must implement SnapsNetworkService"));
        if (resultListener != null) {
            resultListener.onResultFailed(SnapsNetworkThrowable.withErrorMessage("##### develop error!! you must implement SnapsNetworkService"));
        }
    }

    @Override
    public void request(@NonNull Context context, SnapsRetrofitResultListener resultListener) {
        SnapsAssert.assertException(new Exception("##### develop error!! you must implement SnapsNetworkService"));
        if (resultListener != null) {
            resultListener.onResultFailed(SnapsNetworkThrowable.withErrorMessage("##### develop error!! you must implement SnapsNetworkService"));
        }
    }

    @Override
    public void request(@NonNull Context context) {
        super.request(context);
        SnapsAssert.assertException(new Exception("##### develop error!! you must implement SnapsNetworkService"));
    }

    @Override
    public void request(@NonNull SnapsRetrofitRequestBuilder requestBuilder) {
        super.request(requestBuilder);
        SnapsAssert.assertException(new Exception("##### develop error!! you must implement SnapsNetworkService"));
    }
}
