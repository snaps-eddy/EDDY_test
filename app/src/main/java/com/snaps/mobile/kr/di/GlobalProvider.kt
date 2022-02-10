package com.snaps.mobile.kr.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.ConnectivityManager
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.snaps.common.HomeActivity
import com.snaps.mobile.data.BaseHttpClient
import com.snaps.mobile.data.BaseRetrofit
import com.snaps.mobile.data.asset.SnapsResourceApi
import com.snaps.mobile.data.product.ProductApi
import com.snaps.mobile.data.project.ProjectApi
import com.snaps.mobile.kr.HomeActivityImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import java.lang.reflect.Type
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class GlobalProvider {

    @Provides
    @Singleton
    fun contextProvide(@ApplicationContext context: Context): Context =
        context

    @Provides
    @Singleton
    fun contentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    @Singleton
    fun resources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun connectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun sharedPreference(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences("snapsSetting", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun gson(): Gson {
        // https://stackoverflow.com/questions/52837686/gson-format-double-values-to-4-decimal-places
        return GsonBuilder().apply {
            registerTypeAdapter(
                object : TypeToken<Float>() {}.type,
                JsonSerializer { src: Float?, _: Type?, _: JsonSerializationContext? ->
                    val df = DecimalFormat("#.###").apply {
                        roundingMode = RoundingMode.HALF_UP // 소수점은 3자리까지 표시해요 (반올림)
                    }
                    JsonPrimitive(df.format(src).toFloat())
                }
            )
            registerTypeAdapter(
                object : TypeToken<Double>() {}.type,
                JsonSerializer { src: Double?, _: Type?, _: JsonSerializationContext? ->
                    val df = DecimalFormat("#.###").apply {
                        roundingMode = RoundingMode.HALF_UP // 소수점은 3자리까지 표시해요 (반올림)
                    }
                    JsonPrimitive(df.format(src).toDouble())
                }
            )
            disableHtmlEscaping()  // 유니코드가 싫어요
        }.create()
    }

    @Singleton
    @Provides
    fun okHttpClient(baseHttpClient: BaseHttpClient): OkHttpClient = baseHttpClient.okHttpClient

    @Singleton
    @Provides
    fun rApi(baseRetrofit: BaseRetrofit): ProjectApi = baseRetrofit.httpRetrofit.create(ProjectApi::class.java)

    @Singleton
    @Provides
    fun productApi(baseRetrofit: BaseRetrofit): ProductApi = baseRetrofit.httpRetrofit.create(ProductApi::class.java)

    @Singleton
    @Provides
    fun assetApi(baseRetrofit: BaseRetrofit): SnapsResourceApi = baseRetrofit.resourceRetrofit.create(SnapsResourceApi::class.java)

    @Provides
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun homeActivity(context: Context): HomeActivity = HomeActivityImpl()

//    @Singleton
//    @Provides
//    fun sharedPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

}