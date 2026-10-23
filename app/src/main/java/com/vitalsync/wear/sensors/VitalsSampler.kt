package com.vitalsync.wear.sensors

import android.content.Context

class VitalsSampler(context: Context) {
    private val demo = DemoVitalsSampler()
    private val heartRateReader = HeartRateReader(context)

    suspend fun sample(): VitalsSample {
        val demoSample = demo.next()
        val realHr = runCatching { heartRateReader.readOnce() }.getOrNull()
        return if (realHr != null && realHr in 30..220) {
            demoSample.copy(heartRate = realHr, source = "sensor+demo")
        } else {
            demoSample
        }
    }

    fun sensorAvailable(): Boolean = heartRateReader.hasSensor()
}
