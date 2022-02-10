package com.snaps.mobile.data

import com.google.gson.Gson
import com.snaps.common.android_utils.ApiProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class BaseRetrofit @Inject constructor(
    okHttpClient: OkHttpClient,
    gson: Gson,
    apiProvider: ApiProvider
) {
    val httpRetrofit: Retrofit = Retrofit.Builder()
//        .baseUrl("https://${BuildConfig.BASE_URL}")
        .baseUrl(apiProvider.newApiBaseUrl)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    val resourceRetrofit: Retrofit = Retrofit.Builder()
//        .baseUrl("https://${BuildConfig.BASE_URL}")
        .baseUrl(apiProvider.newApiBaseUrl)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    val fakeRetrofit: Retrofit = Retrofit.Builder()
//        .baseUrl("https://${BuildConfig.BASE_URL}")
        .baseUrl("https://picsum.photos")
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()
}