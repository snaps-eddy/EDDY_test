package com.snaps.common.http;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * 외부 웹사이트를 호출하여 디바이스의 public ip 획득하는 용도.
 */
public interface IPCheckingAPI {

    @GET("json")
    Flowable<ResponseDeviceIP> getIPInfo();

}
