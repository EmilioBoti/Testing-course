package com.embot.testingcourse.di

import com.embot.testingcourse.core.data.coroutine.DefaultDispatcherProvider
import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.productList.data.repository.ProductRepositoryImpl
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDispatcher(
        defaultDispatcherProvider: DefaultDispatcherProvider
    ): DispatcherProvider {
        return defaultDispatcherProvider
    }

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository {
        return  productRepositoryImpl
    }

}