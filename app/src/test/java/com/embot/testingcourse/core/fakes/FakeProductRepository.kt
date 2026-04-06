package com.embot.testingcourse.core.fakes

import com.embot.testingcourse.productList.domain.model.Product
import com.embot.testingcourse.productList.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProductRepository: ProductRepository {

    private val _productItems: MutableStateFlow<List<Product>> = MutableStateFlow(emptyList())

    fun setProducts(products: List<Product>) {
        _productItems.value = products
    }

    override fun getProduct(): Flow<List<Product>> = _productItems.asStateFlow()

    override fun getProductById(id: String): Flow<Product?> {
        return _productItems.asStateFlow().map { products ->
            products.find { it.id == id }
        }
    }

    override fun getProductsByIds(ids: Set<String>): Flow<List<Product>> {
        return _productItems.asStateFlow().map { products ->
            products.filter { it.id in ids }
        }
    }

    override suspend fun refreshPRoduct() {
        // No Efffect
    }
}