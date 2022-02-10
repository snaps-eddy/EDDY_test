package com.snaps.mobile.kr.di

import com.snaps.common.android_utils.*
import com.snaps.mobile.kr.provider.ApiProviderImpl
import com.snaps.mobile.kr.NetworkChangeMonitorImpl
import com.snaps.mobile.kr.provider.NetworkProviderImpl
import com.snaps.mobile.kr.provider.ResourceProviderImpl
import com.snaps.mobile.kr.provider.SchedulerProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.xml.sax.ErrorHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GlobalBindsModule {

    @Singleton
    @Binds
    abstract fun schedulerProvider(impl: SchedulerProviderImpl): SchedulerProvider

    @Singleton
    @Binds
    abstract fun resourceProvider(impl: ResourceProviderImpl): ResourceProvider

    @Singleton
    @Binds
    abstract fun networkProvider(impl: NetworkProviderImpl): NetworkProvider

    @Singleton
    @Binds
    abstract fun apiProvider(impl: ApiProviderImpl): ApiProvider

    @Singleton
    @Binds
    abstract fun networkChangeMonitor(impl: NetworkChangeMonitorImpl): NetworkChangeMonitor
}