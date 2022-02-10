package com.snaps.mobile.service.ai;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

class ServiceGenerator {
    private static final String TAG = ServiceGenerator.class.getSimpleName();
    private static final boolean IS_ACTIVE_HTTP_LOG = true;
    private static final int TIMEOUT = 5;
    public static final String SERVER_BASE_URL_AI_REAL = "http://saida-m.snaps.com/";   //서버 주소
    public static final String SERVER_BASE_URL_AI_TEST = "http://stg-ai.snaps.com/";  //서버 주소

    public <S> S createService(String baseUrl, Class<S> serviceClass) {
        return createService(baseUrl, serviceClass, TIMEOUT);
    }


    public <S> S createService(String baseUrl, Class<S> serviceClass, long timeoutSecond) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(baseUrl);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.retryOnConnectionFailure(false);
        okHttpClientBuilder.connectTimeout(timeoutSecond, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(timeoutSecond, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(timeoutSecond, TimeUnit.SECONDS);
        okHttpClientBuilder.cache(null);

        HTTPLog.setLogger(okHttpClientBuilder);   //logger

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        retrofitBuilder.client(okHttpClient);

        Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(serviceClass);
    }

    static class HTTPLog {
        private static final String TAG = HTTPLog.class.getSimpleName();

        public static void setLogger(OkHttpClient.Builder httpClientBuilder) {
            if (Loggg.IS_DEBUG == false) {
                return;
            }

            if (IS_ACTIVE_HTTP_LOG == false) {
                return;
            }

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Loggg.d(TAG, message);
                }
            });

            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClientBuilder.addInterceptor(interceptor);
        }
    }
}
