package com.vitalsync.wear.data

data class LoginRequest(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val token: String,
    val userData: UserData?,
)

data class UserData(
    val id: String?,
    val _id: String?,
    val name: String?,
    val email: String?,
    val role: String?,
) {
    val userId: String get() = id ?: _id.orEmpty()
}

data class VitalUploadRequest(
    val heartRate: Int,
    val spo2: Int,
    val temperature: Double,
    val activityState: String,
    val timestamp: String,
)

data class VitalUploadResponse(
    val _id: String?,
    val riskLevel: String?,
    val riskScore: Double?,
    val heartRate: Int?,
    val spo2: Int?,
    val temperature: Double?,
    val activityState: String?,
)

data class ApiErrorBody(
    val message: String?,
)
