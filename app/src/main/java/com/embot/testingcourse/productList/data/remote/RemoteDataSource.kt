package com.embot.testingcourse.productList.data.remote

import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.productList.data.remote.response.ProductsResponse
import com.embot.testingcourse.productList.data.remote.response.PromotionResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    val miniMarketApiService: MiniMarketApiService
) {

    suspend fun getProduts(): Result<ProductsResponse> {
        return try {
            val response = miniMarketApiService.getProducts()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(this.mapToDomainError(e))
        }
    }

    suspend fun getPromotions(): Result<List<PromotionResponse>> {
        return try {
            val response = miniMarketApiService.getPromotions()
            Result.success(response.promotions)
        } catch (e: Exception) {
            Result.failure(mapToDomainError(e))
        }
    }

    private fun mapToDomainError(e: Exception): AppError {
        return when(e) {
            is UnknownHostException,
            is SocketException,
            is IOException -> AppError.NetworkError
            is HttpException -> {
                when(e.code()) {
                    404 -> AppError.NotFoundError
                    500 -> AppError.NetworkError
                    else -> AppError.UnKnownError(e.message)
                }
            }
            else -> AppError.UnKnownError(e.message)
        }
    }

}