package com.snaps.mobile.kr.di

import com.snaps.mobile.data.app.AppPreferenceRepositoryImpl
import com.snaps.mobile.data.asset.AssetRepositoryImpl
import com.snaps.mobile.data.product.ProductRepositoryImpl
import com.snaps.mobile.data.project.ProjectRepositoryImpl
import com.snaps.mobile.domain.app.AppPreferenceRepository
import com.snaps.mobile.domain.asset.AssetRepository
import com.snaps.mobile.domain.product.ProductRepository
import com.snaps.mobile.domain.project.ProjectRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedBindsModule {

    @ActivityRetainedScoped
    @Binds
    abstract fun projectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun productRepository(impl: ProductRepositoryImpl): ProductRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun userImageSourceRepository(impl: AssetRepositoryImpl): AssetRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun appPreferenceRepository(impl: AppPreferenceRepositoryImpl): AppPreferenceRepository
}