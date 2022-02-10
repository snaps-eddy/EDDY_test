package com.snaps.common.http;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIConnection {

    private static final String TAG = APIConnection.class.getSimpleName();
    private static final String IP_CHECKING_BASE_URL = "https://wtfismyip.com/";

    private Retrofit snapsNewAPI;
    private IPCheckingAPI ipCheckingAPI;

    /**
     * 새로운 API 주소는 프론트엔드 주소와 같다.
     */
    private APIConnection() {
    }

    private static class LazyHolder {
        static final APIConnection INSTANCE = new APIConnection();
    }

    public static APIConnection getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void init(String languageCode) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient client = httpClient.addInterceptor(loggingInterceptor).build();

        Retrofit.Builder snapsNewAPIBuilder = new Retrofit.Builder()
                .baseUrl(SnapsAPI.FRONTEND_DOMAIN(languageCode, false))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client);

        snapsNewAPI = snapsNewAPIBuilder.build();

        ipCheckingAPI = new Retrofit.Builder()
                .baseUrl(IP_CHECKING_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build().create(IPCheckingAPI.class);
    }

    public SnapsNewAPIService getNewSnapsAPIService() {
        return snapsNewAPI.create(SnapsNewAPIService.class);
    }

    public IPCheckingAPI getIpCheckingAPI() {
        return ipCheckingAPI;
    }
}
