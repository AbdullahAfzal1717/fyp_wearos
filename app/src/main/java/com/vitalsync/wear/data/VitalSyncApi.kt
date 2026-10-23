package com.vitalsync.wear.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface VitalSyncApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("vitals/upload")
    suspend fun uploadVital(
        @Header("Authorization") bearer: String,
        @Body body: VitalUploadRequest,
    ): Response<VitalUploadResponse>

    @POST("vitals/sync")
    suspend fun syncBulk(
        @Header("Authorization") bearer: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<Any>
}

object ApiFactory {
    fun create(baseUrl: String, tokenProvider: (() -> String?)? = null): VitalSyncApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val authInterceptor = Interceptor { chain ->
            val token = tokenProvider?.invoke()
            val req = if (!token.isNullOrBlank()) {
                chain.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(req)
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

        return Retrofit.Builder()
            .baseUrl(normalized)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VitalSyncApi::class.java)
    }
}
