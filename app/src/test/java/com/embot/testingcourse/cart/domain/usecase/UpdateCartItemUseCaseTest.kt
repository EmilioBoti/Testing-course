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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateCartItemUseCaseTest {

    @Test
    fun given_negative_quantity_when_invoke_then_throws_QuantityMustBePositive() = runTest {
        // GIVEN

        val productId = "Product-1"
        val quantity = -1

        val fakeCartItemRepository = FakeCartItemRepository()
        val fakeProductRepository = FakeProductRepository()

        val useCase = UpdateCartItemUseCase(fakeCartItemRepository, fakeProductRepository)

        // WHEN
        val response = runCatching { useCase(productId, quantity) }.exceptionOrNull()

        // THEN
        assertTrue(response is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun give_quantity_zero_when_invoke_remove_item_from_cart() = runTest {
        // GIVEN
        val productId = "Product-1"
        val quantity = 0

        val cartItemRepository = mockk<CartItemRepository>()
        val productRepository = mockk<ProductRepository>()

        coEvery { cartItemRepository.removeFromCart(productId) } just Runs

        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        // WHEN
        useCase(productId, quantity)

        coVerify { cartItemRepository.removeFromCart(productId) }

    }

    @Test
    fun given_missing_product_when_invoke_then_throws_NotFoundError() = runTest {
        // GIVEN
        val productId = "product-1"
        val cartItemRepository = mockk<CartItemRepository>()
        val productRepository = mockk<ProductRepository>()

        coEvery { productRepository.getProductById(productId) } returns emptyFlow()

        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)
        // WHEN
        val exception = runCatching { useCase(productId, 3) }.exceptionOrNull()

        // THEN
        assertTrue(exception is AppError.NotFoundError)

    }

    @Test
    fun given_quantity_higher_than_stock_when_invoke_throws_InsufficientStock() = runTest {
        // GIVEN
        val productId = "product-1"
        val product = product {
            withId(productId)
            withStock(5)
        }
        val cartItemRepository = mockk<CartItemRepository>()
        val productRepository = mockk<ProductRepository>()

        coEvery { productRepository.getProductById(productId) } returns flowOf(product)
        
        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        // WHEN
        val exception = runCatching { useCase(productId, 7) }.exceptionOrNull()

        // THEN
        assertTrue(exception is AppError.Validation.InsufficientStock)
        
    }

    @Test
    fun given_quantity_higher_than_zero_and_lower_than_stock_when_invoke_update_product_quantity() = runTest {
        // GIVEN
        val productId = "product-1"
        val quantity = 7
        val product = product {
            withId(productId)
            withStock(10)
        }
        val cartItemRepository = mockk<CartItemRepository>()
        val productRepository = mockk<ProductRepository>()

        coEvery { productRepository.getProductById(productId) } returns flowOf(product)
        coEvery { cartItemRepository.updateQuantity(productId, any()) } just Runs

        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        // WHEN
        useCase(productId, quantity)

        // THEN
        coVerify(exactly = 1) { productRepository.getProductById(productId) }
        coVerify(exactly = 1) { cartItemRepository.updateQuantity(productId, quantity) }
    }


}