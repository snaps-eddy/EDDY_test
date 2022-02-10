package com.snaps.common.http;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface SnapsNewAPIService {


    /**
     * 임시로 구현한 헤더. 가변적인 값이나 사용자 정보등을 보내려면 파라미터로 전달받아 넣어줘야한다.
     */
    @Headers({
            "X-SNAPS-CHANNEL: ANDROID",
            "X-SNAPS-VERSION: 1",
            "X-SNAPS-OS-VERSION: 1",
            "X-SNAPS-DEVICE: 1",
            "X-SNAPS-DEVICE-TOKEN: 1",
            "X-SNAPS-DEVICE-UUID: 1",
    })
    @GET("v1/product/goods/areaPrice")
    Flowable<AreaPriceEntity> getProductAreaPrice(@Query("menu") String menuType, @Query("width") int width, @Query("height") int height, @Query("discountRate") float discountRate, @Query("paperCode") String paperCode);

}
