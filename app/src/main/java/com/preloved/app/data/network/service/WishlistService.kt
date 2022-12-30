package com.preloved.app.data.network.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.preloved.app.BuildConfig
import com.preloved.app.data.network.model.request.wishlist.WishlistRequest
import com.preloved.app.data.network.model.response.whislist.AddWishlistResponse
import com.preloved.app.data.network.model.response.whislist.DeleteWishlistResponse
import com.preloved.app.data.network.model.response.whislist.GetWishlistResponse
import com.preloved.app.data.network.model.response.whislist.GetWishlistByIdResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface WishlistService {
    @POST("buyer/wishlist")
    suspend fun addWishlistProduct(@Header("access_token") accessToken: String, @Body wishlistRequest: WishlistRequest): AddWishlistResponse

    @GET("buyer/wishlist")
    suspend fun getWishlistProduct(@Header("access_token") accessToken: String): GetWishlistResponse

    @GET("buyer/wishlist/{id}")
    suspend fun getWishlistProductById(@Header("access_token") accessToken: String, @Path("id") productId: Int): GetWishlistByIdResponse

    @DELETE("buyer/wishlist/{id}")
    suspend fun deleteWishlistProductById(@Header("access_token") accessToken: String, @Path("id") productId: Int): DeleteWishlistResponse

    companion object {
        @JvmStatic
        operator fun invoke(chuckerInterceptor: ChuckerInterceptor): WishlistService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(chuckerInterceptor)
                .addInterceptor {
                    val req = it.request()
                    val query = req.url
                        .newBuilder()
                        .build()
                    return@addInterceptor it.proceed(req.newBuilder().url(query).build())
                }
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(WishlistService::class.java)
        }
    }
}