package com.embot.testingcourse.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.embot.testingcourse.cart.data.local.db.dao.CartItemDao
import com.embot.testingcourse.cart.data.local.repository.CartItemRepositoryImpl
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.data.coroutine.DefaultDispatcherProvider
import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import com.embot.testingcourse.productList.data.local.db.dao.ProductDao
import com.embot.testingcourse.productList.data.local.db.dao.PromotionDao
import com.embot.testingcourse.productList.data.repository.ProductRepositoryImpl
import com.embot.testingcourse.productList.data.repository.PromotionRepositoryImpl
import com.embot.testingcourse.productList.data.repository.SettingsRepositoryImpl
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import com.embot.testingcourse.productList.domain.repository.PromotionRepository
import com.embot.testingcourse.productList.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

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
    fun provideSettingsRepository(settingsRepository: SettingsRepositoryImpl): SettingsRepository {
        return settingsRepository
    }

    @Provides
    @Singleton
    fun provideCartItemRepository(cartItemRepository: CartItemRepositoryImpl): CartItemRepository {
        return cartItemRepository
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

    @Provides
    fun provideCartItemDao(database: MiniMarketDatabase): CartItemDao = database.cartItemDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

}