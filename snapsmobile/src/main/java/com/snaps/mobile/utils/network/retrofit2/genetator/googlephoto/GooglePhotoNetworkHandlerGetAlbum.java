package com.snaps.mobile.utils.network.retrofit2.genetator.googlephoto;

import androidx.annotation.NonNull;

import com.snaps.mobile.utils.network.provider.SnapsRetrofitRequestBuilder;
import com.snaps.mobile.utils.network.retrofit2.api.external.IGooglePhotoAPI;
import com.snaps.mobile.utils.network.retrofit2.genetator.SnapsNetworkGenerateBase;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsNetworkResponseContentType;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;

import io.reactivex.Flowable;

public class GooglePhotoNetworkHandlerGetAlbum extends SnapsNetworkGenerateBase {
    @Override
    protected Flowable<?> generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
        return createAPI(IGooglePhotoAPI.class).requestAPIAlbum("application/json", GooglePhotoUtil.getBearerToken());
    }

    @Override
    public eSnapsNetworkResponseContentType getResponseContentType() {
        return eSnapsNetworkResponseContentType.STRING;
    }
}
