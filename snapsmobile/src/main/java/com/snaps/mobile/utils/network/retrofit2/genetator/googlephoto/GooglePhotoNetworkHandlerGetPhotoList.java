package com.snaps.mobile.utils.network.retrofit2.genetator.googlephoto;

import androidx.annotation.NonNull;

import com.snaps.mobile.utils.network.provider.SnapsRetrofitRequestBuilder;
import com.snaps.mobile.utils.network.retrofit2.api.external.IGooglePhotoAPI;
import com.snaps.mobile.utils.network.retrofit2.genetator.SnapsNetworkGenerateBase;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsNetworkResponseContentType;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;

import org.json.JSONObject;

import io.reactivex.Flowable;

public class GooglePhotoNetworkHandlerGetPhotoList extends SnapsNetworkGenerateBase {
    @Override
    protected Flowable<?> generateRetrofitAPIService(@NonNull SnapsRetrofitRequestBuilder requestBuilder) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("albumId", requestBuilder.getSimplePostParamMap().get("albumId"));
        if(requestBuilder.getSimplePostParamMap().get("pageToken") != null) {
            jsonObject.put("pageToken", requestBuilder.getSimplePostParamMap().get("pageToken"));
        }
        return createAPI(IGooglePhotoAPI.class).requestAPIPhotoList("application/json","application/json", GooglePhotoUtil.getBearerToken(), jsonObject.toString());
    }

    @Override
    public eSnapsNetworkResponseContentType getResponseContentType() {
        return eSnapsNetworkResponseContentType.STRING;
    }
}
