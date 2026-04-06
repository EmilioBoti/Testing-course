package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
import com.embot.testingcourse.core.fakes.FakeProductRepository
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

}