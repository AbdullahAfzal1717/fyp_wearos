package com.vitalsync.wear.sensors

import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Simulator-style vitals for emulator / when Health Services HR is unavailable.
 * Matches the backend risk demo patterns (safe / warning / critical mix).
 */
class DemoVitalsSampler {
    private var tick = 0

    fun next(): VitalsSample {
        tick += 1
        val activity = if (tick % 5 == 0) "RUNNING" else "STATIC"

        var heartRate = 68 + Random.nextInt(18)
        var spo2 = 96 + Random.nextInt(4)
        var temperature = ((36.5 + Random.nextDouble() * 0.6) * 10).roundToInt() / 10.0

        when {
            tick % 12 == 7 -> {
                heartRate = if (activity == "STATIC") 125 else 175
                spo2 = 88 + Random.nextInt(3)
                temperature = ((39.2 + Random.nextDouble() * 0.8) * 10).roundToInt() / 10.0
            }
            tick % 8 == 3 -> {
                heartRate = if (activity == "STATIC") 110 + Random.nextInt(15) else 140
                spo2 = 93 + Random.nextInt(3)
                temperature = ((37.4 + Random.nextDouble() * 0.4) * 10).roundToInt() / 10.0
            }
            activity == "RUNNING" -> {
                heartRate = 120 + Random.nextInt(20)
            }
        }

        return VitalsSample(
            heartRate = heartRate,
            spo2 = spo2,
            temperature = temperature,
            activityState = activity,
            source = "demo",
        )
    }
}
