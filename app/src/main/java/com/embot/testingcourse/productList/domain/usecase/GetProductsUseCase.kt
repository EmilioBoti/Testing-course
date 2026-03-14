package com.embot.testingcourse.productList.domain.usecase

import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {

    operator  fun invoke(): Flow<List<Product>> {
        return productRepository.getProduct()
    }
}