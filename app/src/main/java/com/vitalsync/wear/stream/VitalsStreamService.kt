package com.vitalsync.wear.stream

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vitalsync.wear.MainActivity
import com.vitalsync.wear.R
import com.vitalsync.wear.VitalSyncWearApp
import com.vitalsync.wear.data.VitalUploadRequest
import com.vitalsync.wear.sensors.VitalsSampler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant

class VitalsStreamService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var loopJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(NOTIF_ID, buildNotification("Starting…"))
                startLoop()
            }
        }
        return START_STICKY
    }

    private fun startLoop() {
        if (loopJob?.isActive == true) return
        val app = application as VitalSyncWearApp
        val sampler = VitalsSampler(this)
        val queue = app.repository.offlineQueue(this)

        loopJob = scope.launch {
            while (isActive) {
                val sample = sampler.sample()
                val payload = VitalUploadRequest(
                    heartRate = sample.heartRate,
                    spo2 = sample.spo2,
                    temperature = sample.temperature,
                    activityState = sample.activityState,
                    timestamp = Instant.now().toString(),
                )

                try {
                    // Flush any queued packets first
                    val pending = queue.drain()
                    val failed = mutableListOf<VitalUploadRequest>()
                    for (item in pending) {
                        runCatching { app.repository.upload(item) }
                            .onFailure { failed.add(item) }
                    }
                    if (failed.isNotEmpty()) queue.restore(failed)

                    val result = app.repository.upload(payload)
                    StreamBus.publish(
                        StreamBus.Update(
                            streaming = true,
                            lastHr = sample.heartRate,
                            lastSpo2 = sample.spo2,
                            lastTemp = sample.temperature,
                            activity = sample.activityState,
                            risk = result.riskLevel ?: "—",
                            source = sample.source,
                            queued = queue.size(),
                            message = "Synced",
                            error = null,
                        ),
                    )
                    updateNotification(
                        "HR ${sample.heartRate} · ${result.riskLevel ?: "SAFE"}",
                    )
                } catch (e: Exception) {
                    queue.enqueue(payload)
                    StreamBus.publish(
                        StreamBus.Update(
                            streaming = true,
                            lastHr = sample.heartRate,
                            lastSpo2 = sample.spo2,
                            lastTemp = sample.temperature,
                            activity = sample.activityState,
                            risk = "—",
                            source = sample.source,
                            queued = queue.size(),
                            message = "Queued offline",
                            error = e.message,
                        ),
                    )
                    updateNotification("Offline · queued ${queue.size()}")
                }

                delay(INTERVAL_MS)
            }
        }
    }

    private fun buildNotification(content: String): Notification {
        val channelId = "vitals_stream"
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Vitals streaming",
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }

        val open = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.stream_notification_title))
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(open)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(content: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIF_ID, buildNotification(content))
    }

    override fun onDestroy() {
        loopJob?.cancel()
        scope.cancel()
        StreamBus.publish(StreamBus.Update(streaming = false))
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.vitalsync.wear.STOP_STREAM"
        private const val NOTIF_ID = 42
        const val INTERVAL_MS = 4000L
    }
}
