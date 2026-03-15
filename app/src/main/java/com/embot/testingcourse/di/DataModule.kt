package com.embot.testingcourse.di

import android.content.Context
import androidx.room.Room
import com.embot.testingcourse.core.data.coroutine.DefaultDispatcherProvider
import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.productList.data.local.db.MiniMarketDatabase
import com.embot.testingcourse.productList.data.local.db.dao.ProductDao
import com.embot.testingcourse.productList.data.local.db.dao.PromotionDao
import com.embot.testingcourse.productList.data.repository.ProductRepositoryImpl
import com.embot.testingcourse.productList.data.repository.PromotionRepositoryImpl
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun providePromotionRepository(promotionRepository: PromotionRepositoryImpl): PromotionRepository {
        return promotionRepository
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MiniMarketDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = MiniMarketDatabase::class.java,
            name = "market_db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: MiniMarketDatabase): ProductDao = database.productDao()

    @Provides
    fun providePromotionDao(database: MiniMarketDatabase): PromotionDao = database.promotionDao()

}