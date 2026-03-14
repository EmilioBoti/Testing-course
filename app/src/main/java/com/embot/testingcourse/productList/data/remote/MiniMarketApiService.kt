package com.embot.testingcourse.productList.data.remote

import com.embot.testingcourse.productList.data.remote.response.ProductsResponse
import com.embot.testingcourse.productList.data.remote.response.PromotionsResponse
import retrofit2.http.GET

interface MiniMarketApiService {

    @GET("data/products.json")
    suspend fun getProducts(): ProductsResponse

    @GET("data/promotions.json")
    suspend fun getPromotions(): PromotionsResponse
}