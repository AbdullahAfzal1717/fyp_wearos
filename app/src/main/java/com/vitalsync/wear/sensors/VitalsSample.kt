package com.vitalsync.wear.sensors

data class VitalsSample(
    val heartRate: Int,
    val spo2: Int,
    val temperature: Double,
    val activityState: String,
    val source: String,
)
