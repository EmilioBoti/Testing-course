package com.embot.testingcourse.productList.data.local.db.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.core.builders.promotionEntity
import com.embot.testingcourse.core.data.local.db.MiniMarketDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PromotionDaoTest {

    private lateinit var database: MiniMarketDatabase
    private lateinit var dao: PromotionDao


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            MiniMarketDatabase::class.java
        ).build()
        dao = database.promotionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenEmptyDatabase_whenGetAllPromotion_thenEmitsEmptyListPromotion() = runTest {
        val promotion = dao.getAllPromotions().first()
        assertTrue(promotion.isEmpty())
    }

    @Test
    fun givenPromotion_whenGetPromotionById_thenEmitsPromotion() = runTest {
        val promoId = "promo1"
        val type = "PERCENT"
        val promotion = promotionEntity { withId(promoId); withType(type) }

        dao.insertPromotions(listOf(promotion))

        val result = dao.getPromotionsById(promoId).first()

        assertEquals(type, result.type)
    }

    @Test
    fun givenMultiplePromotion_whenClearPromotions_thenDeleteAllPromotions() = runTest {
        val type = "PERCENT"
        val promo = promotionEntity { withId("promo1"); withType(type) }
        val promo2 = promotionEntity { withId("promo2"); withType(type) }

        dao.insertPromotions(listOf(promo, promo2))

        dao.clearPromotions()

        val promotions = dao.getAllPromotions().first()
        assertTrue(promotions.isEmpty())
    }

    @Test
    fun givenOldPromotions_whenReplaceAllPromotions_thenUpdateAllPromotion() = runTest {
        val type = "PERCENT"
        val oldPromo = promotionEntity { withId("promo1"); withType(type) }
        val oldPromo2 = promotionEntity { withId("promo2"); withType(type) }
        dao.insertPromotions(listOf(oldPromo, oldPromo2))

        val newType = "BUY_X_PAY_Y"

        val newPromo3 = promotionEntity { withId("promo3"); withType(newType) }
        val newPromo4 = promotionEntity { withId("promo4"); withType(newType) }
        val newPromo5 = promotionEntity { withId("promo5"); withType(type) }

        dao.replaceAll(listOf(newPromo3, newPromo4, newPromo5))

        val promotions = dao.getAllPromotions().first()

        assertTrue(promotions.any { it.id == "promo3" && it.type == newType })
        assertTrue(promotions.any { it.id == "promo4" && it.type == newType })
        assertTrue(promotions.any { it.id == "promo5" && it.type == type })
    }

}