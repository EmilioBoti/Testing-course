package com.embot.testingcourse.cart.data.local.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.domain.model.AppError
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CartItemRepositoryImplTest {

    @get:Rule
    val hilt: HiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var cartItemRepository: CartItemRepository


    @Before
    fun setUp() {
        hilt.inject()
    }

    @Test
    fun givenNotExistingCartItem_whenAddToCart_thenInsertCartItem() = runTest {
        val productId = "p1"
        cartItemRepository.addToCart(productId, 3)

        val result = cartItemRepository.getCartItems().first()
        assertEquals(1, result.size)
        assertEquals(productId, result.find { it.productId == "p1" }?.productId )
    }

    @Test
    fun givenExistingCartItem_whenAddToCart_thenUpdateQuantity() = runTest {
        val productId = "p1"
        cartItemRepository.addToCart(productId, 3)
        cartItemRepository.addToCart(productId, 7)

        val result = cartItemRepository.getCartItems().first()

        assertEquals(1, result.size)
        assertEquals(10, result.find { it.productId == productId }?.quantity)
    }

    @Test(expected = AppError.NotFoundError::class)
    fun givenNotExistingCartItem_whenRemoveFromCart_thenItThrowNotFoundError() = runTest {
        cartItemRepository.removeFromCart("p1")
    }

    @Test
    fun givenExistingCartItem_whenRemoveFromCart_thenRemoveItemFromCart() = runTest {
        val productId = "p1"

        cartItemRepository.addToCart(productId, 3)
        cartItemRepository.removeFromCart(productId)

        val result = cartItemRepository.getCartItems().first()

        assertEquals(0, result.size)
    }

    @Test(expected = AppError.NotFoundError::class)
    fun givenNotExistingCartItem_whenUpdateQuantity_thenItThrowNotFoundError() = runTest {
        cartItemRepository.updateQuantity("p1", 2)
    }

    @Test
    fun givenExistingCartItem_whenUpdateQuantity_thenUpdateQuantity() = runTest {
        val productId = "p1"
        cartItemRepository.addToCart(productId, 3)
        cartItemRepository.updateQuantity(productId, 5)

        val result = cartItemRepository.getCartItems().first()

        assertEquals(1, result.size)
        assertEquals(5, result.find { it.productId == productId }?.quantity)
    }

}