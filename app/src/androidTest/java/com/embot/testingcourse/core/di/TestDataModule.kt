package com.embot.testingcourse.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.embot.testingcourse.cart.data.local.db.dao.CartItemDao
import com.embot.testingcourse.cart.data.local.repository.CartItemRepositoryImpl
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.data.SystemClock
import com.embot.testingcourse.core.data.coroutine.DefaultDispatcherProvider
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import com.embot.testingcourse.core.domain.coroutines.DispatcherProvider
import com.embot.testingcourse.core.domain.utils.Clock
import com.embot.testingcourse.di.DataModule
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
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

private val Context.testingDataStore: DataStore<Preferences> by preferencesDataStore("testing_settings")

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {

    @Provides
    @Singleton
    fun provideSystemClock(clock: SystemClock): Clock =  clock

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
    fun provideDatabase(): MiniMarketDatabase {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return Room.inMemoryDatabaseBuilder(
            context = context,
            klass = MiniMarketDatabase::class.java,
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
    fun provideDataStore(): DataStore<Preferences> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return context.testingDataStore
    }

}