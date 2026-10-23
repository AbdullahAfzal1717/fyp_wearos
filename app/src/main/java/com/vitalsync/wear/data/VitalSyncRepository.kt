package com.vitalsync.wear.data

import android.content.Context
import com.google.gson.Gson

class VitalSyncRepository(
    private val sessionStore: SessionStore,
) {
    private val gson = Gson()

    suspend fun login(email: String, password: String, apiBase: String): UserData {
        val api = ApiFactory.create(apiBase)
        val response = api.login(LoginRequest(email.trim(), password))
        val user = response.userData
            ?: throw IllegalStateException("Login ok but no userData")
        if (user.role != null && user.role != "USER") {
            throw IllegalStateException("Wear app is for USER accounts (got ${user.role})")
        }
        sessionStore.saveLogin(response.token, user, apiBase)
        return user
    }

    suspend fun upload(request: VitalUploadRequest): VitalUploadResponse {
        val token = sessionStore.getToken()
            ?: throw IllegalStateException("Not logged in")
        val api = ApiFactory.create(sessionStore.getApiBase())
        val response = api.uploadVital("Bearer $token", request)
        if (response.isSuccessful) {
            return response.body() ?: VitalUploadResponse(
                _id = null,
                riskLevel = null,
                riskScore = null,
                heartRate = request.heartRate,
                spo2 = request.spo2,
                temperature = request.temperature,
                activityState = request.activityState,
            )
        }
        val err = response.errorBody()?.string()
        val message = runCatching {
            gson.fromJson(err, ApiErrorBody::class.java)?.message
        }.getOrNull() ?: "Upload failed (${response.code()})"
        throw IllegalStateException(message)
    }

    fun offlineQueue(context: Context) = OfflineQueue(context)
}
