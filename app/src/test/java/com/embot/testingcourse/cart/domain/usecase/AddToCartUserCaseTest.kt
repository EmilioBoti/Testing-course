package com.embot.testingcourse.cart.domain.usecase

import com.embot.testingcourse.cart.domain.repository.CartItemRepository
import com.embot.testingcourse.core.builders.product
import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.core.fakes.FakeCartItemRepository
import com.embot.testingcourse.core.fakes.FakeProductRepository
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

    @Test
    fun zero_quantity_does_not_call_any_repository() = runTest {
        // GIVEN
        val productRepository = mockk<ProductRepository>()
        val cartItemRepository = mockk<CartItemRepository>()
        val useCase = AddToCartUserCase(cartItemRepository, productRepository)

        // WHEN
        runCatching { useCase("id", -2) }.exceptionOrNull()

        // THEN
        coVerify(exactly = 0) { productRepository.getProductById(any()) }
        coVerify(exactly = 0) { cartItemRepository.getCartItemById(any()) }
        coVerify(exactly = 0) { cartItemRepository.addToCart(any(), any()) }

    }

    @Test
    fun valid_product_calls_addToCart_with_expect_values() = runTest {
        // GIVEN
        val productRepository = mockk<ProductRepository>()
        val cartItemRepository = mockk<CartItemRepository>()

        val productId = "id_test_2"
        val quantity = 3
        val product = product {
            withId(productId)
            withStock(10)
        }

        coEvery { productRepository.getProductById(productId) } returns flowOf(product)
        coEvery { cartItemRepository.getCartItemById(productId) } returns null
        coEvery { cartItemRepository.addToCart(productId, quantity) } just Runs

        val useCase = AddToCartUserCase(cartItemRepository, productRepository)

        // WHEN
        useCase(productId, quantity)

        // THEN
        coVerify(exactly = 1) { productRepository.getProductById(productId) }
        coVerify(exactly = 1) { cartItemRepository.getCartItemById(productId) }
        coVerify(exactly = 1) { cartItemRepository.addToCart(productId, quantity) }
    }


}