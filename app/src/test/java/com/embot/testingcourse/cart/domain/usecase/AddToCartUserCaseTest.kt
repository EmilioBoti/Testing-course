package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
import com.embot.testingcourse.core.fakes.FakeProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AddToCartUserCaseTest {


    @Test
    fun zero_quantity_throws_quantityMustBePositive() = runTest {
        // GIVEN
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository()
        val useCase = AddToCartUserCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository
        )
        // WHEN
        val exception = runCatching { useCase("id", 0) }.exceptionOrNull()

        // THEN
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun negative_quantity_throws_quantityMustBePositive() = runTest {
        // GIVEN
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository()
        val useCase = AddToCartUserCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository
        )
        // WHEN
        val exception = runCatching { useCase("id", -2) }.exceptionOrNull()

        // THEN
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun non_existing_product_quantity_throws_NotFoundError() = runTest {
        // GIVEN
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(emptyList())
        }
        val useCase = AddToCartUserCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository
        )
        // WHEN
        val exception = runCatching { useCase("id", 1) }.exceptionOrNull()

        // THEN
        assertTrue(exception is AppError.NotFoundError)
    }

    @Test
    fun insuffient_stock_throws_Insufficient_stock() = runTest {
        // GIVEN
        val productId = "id_test_1"
        val product = product {
            withId(productId)
            withStock(2)
        }
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUserCase(
            cartItemRepository = cartItemRepository,
            productRepository = productRepository
        )
        // WHEN
        val exception = runCatching { useCase(productId, 3) }.exceptionOrNull()
        // THEN
        assertTrue(exception is AppError.Validation.InsufficientStock)
        assertEquals(2, (exception as AppError.Validation.InsufficientStock).available)
    }

    @Test
    fun successful_case_adds_item_to_cart() = runTest {
        // GIVEN
        val productId = "id_test_1"
        val product = product {
            withId(productId)
            withStock(10)
        }
        val fakeCartItemRepository = FakeCartItemRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUserCase(
            cartItemRepository = fakeCartItemRepository,
            productRepository = fakeProductRepository
        )
        // WHEN
        useCase(productId, 3)
        // THEN
        val items = fakeCartItemRepository.getCartItems().first()
        assertEquals(productId, items.first().productId)
        assertEquals(3, items.first().quantity)
        assertEquals(1, items.size)
    }

    @Test
    fun defatul_quantity_adds_one_item() = runTest {
        // GIVEN
        val productId = "id_test_1"
        val product = product {
            withId(productId)
            withStock(10)
        }
        val fakeCartItemRepository = FakeCartItemRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUserCase(
            cartItemRepository = fakeCartItemRepository,
            productRepository = fakeProductRepository
        )
        // WHEN
        useCase(productId)
        // THEN
        val items = fakeCartItemRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(1, items.first().quantity)
    }

}