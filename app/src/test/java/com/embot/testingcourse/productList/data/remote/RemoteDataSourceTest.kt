package com.embot.testingcourse.productList.data.remote

import com.embot.testingcourse.core.domain.model.AppError
import com.embot.testingcourse.productList.data.remote.response.ProductResponse
import com.embot.testingcourse.productList.data.remote.response.ProductsResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RemoteDataSourceTest {

    private val server: MockWebServer = MockWebServer()
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var json: Json

    @Before
    fun setUp() {
        server.start()
        json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        val api = retrofit.create(MiniMarketApiService::class.java)
        remoteDataSource = RemoteDataSource(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `given empty json response when getProducts then returns empty list`() = runTest {
        server.enqueue(MockResponse().setBody("""{ "products": [] }""").setResponseCode(200))

        val result = remoteDataSource.getProduts()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `given valid json file when getProducts then returns mapped dtos`() = runTest {
        val jsonResponse = ClassLoader.getSystemResource("products_success.json").readText()
        server.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val result = remoteDataSource.getProduts()
        assertTrue(result.isSuccess)
        assertEquals(40, result.getOrThrow().products.size)
    }

    @Test
    fun `given serialized products when getProducts then data matches original object`() = runTest {
        val productResponse = ProductResponse(
            id = "id1",
            name = "pan",
            priceCents = 100,
            category = "bread",
            stock = 5,
        )
        val jsonString = json.encodeToString(
            value = ProductsResponse(
                products = listOf(productResponse)
            )
        )
        server.enqueue(MockResponse().setBody(jsonString).setResponseCode(200))

        val result = remoteDataSource.getProduts()
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().products.first().id == "id1")
    }

    @Test
    fun `given 404 response when getProducts then returns NotFountError`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))

        val result = remoteDataSource.getProduts()
        assertTrue(result.isFailure)
        assertEquals(AppError.NotFoundError, result.exceptionOrNull())
    }

    @Test
    fun `given malformed json when getProducts then returns UnKnownError`() = runTest {
        server.enqueue(MockResponse().setBody("error").setResponseCode(200))

        val result = remoteDataSource.getProduts()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AppError.UnKnownError)
    }

    @Test
    fun `given promotions request when getPromotions then calls correct endpoint`() = runTest {
        server.enqueue(MockResponse().setBody("""{"promotions: []"}""").setResponseCode(200))

        remoteDataSource.getPromotions()

        val result = server.takeRequest()

        assertEquals("/data/promotions.json", result.path)
        assertEquals("GET", result.method)

    }

}