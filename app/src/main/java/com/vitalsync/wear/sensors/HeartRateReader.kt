package com.vitalsync.wear.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Best-effort HR from the platform TYPE_HEART_RATE sensor.
 * Emulators usually have no HR sensor → returns null → caller uses DemoVitalsSampler.
 */
class HeartRateReader(private val context: Context) {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.BODY_SENSORS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasSensor(): Boolean =
        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null

    suspend fun readOnce(timeoutMs: Long = 4000L): Int? {
        if (!hasPermission() || !hasSensor()) return null
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE) ?: return null

        return suspendCancellableCoroutine { cont ->
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    val bpm = event?.values?.firstOrNull()?.toInt() ?: return
                    if (bpm <= 0) return
                    sensorManager.unregisterListener(this)
                    if (cont.isActive) cont.resume(bpm)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            sensorManager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL,
            )

            val handler = android.os.Handler(context.mainLooper)
            val timeout = Runnable {
                sensorManager.unregisterListener(listener)
                if (cont.isActive) cont.resume(null)
            }
            handler.postDelayed(timeout, timeoutMs)

            cont.invokeOnCancellation {
                handler.removeCallbacks(timeout)
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
