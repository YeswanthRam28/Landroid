// app/src/main/java/com/landroid/core/network/RetrofitClient.kt
package com.landroid.core.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface LandroidApiService {
    @GET
    suspend fun getSoilData(
        @Url url: String,
        @QueryMap params: Map<String, String>
    ): SoilResponse

    @GET
    suspend fun getOverpassData(
        @Url url: String,
        @Query("data") query: String
    ): OverpassResponse

    @GET
    suspend fun getNominatimData(
        @Url url: String,
        @QueryMap params: Map<String, String>
    ): NominatimResponse
}

object RetrofitClient {
    fun create(authInterceptor: AuthInterceptor): LandroidApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(LandroidApiService::class.java)
    }
}
