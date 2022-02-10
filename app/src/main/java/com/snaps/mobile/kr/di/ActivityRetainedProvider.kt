package com.snaps.mobile.kr.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedProvider {

//    @ActivityRetainedScoped
//    @Provides
//    fun aiTaskService(): RequestAiAnalysisService {
//        return RequestAiAnalysisService()
//    }
}