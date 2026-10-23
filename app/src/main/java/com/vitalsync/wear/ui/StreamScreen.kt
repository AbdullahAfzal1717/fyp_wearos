package com.vitalsync.wear.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.vitalsync.wear.data.SessionStore
import com.vitalsync.wear.stream.StreamBus
import com.vitalsync.wear.stream.VitalsStreamService
import kotlinx.coroutines.launch

@Composable
fun StreamScreen(
    session: SessionStore.Session,
    sessionStore: SessionStore,
) {
    val context = LocalContext.current
    val update by StreamBus.state.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { /* streaming can start anyway with demo sampler */ }

    fun ensurePermissions() {
        val needed = buildList {
            add(Manifest.permission.BODY_SENSORS)
            if (Build.VERSION.SDK_INT >= 33) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (Build.VERSION.SDK_INT >= 29) {
                add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    fun startStream() {
        ensurePermissions()
        val intent = Intent(context, VitalsStreamService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopStream() {
        val intent = Intent(context, VitalsStreamService::class.java).apply {
            action = VitalsStreamService.ACTION_STOP
        }
        context.startService(intent)
        context.stopService(Intent(context, VitalsStreamService::class.java))
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        item {
            Text(
                text = session.name?.ifBlank { null } ?: "Patient",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
        item {
            Text(
                text = session.email.orEmpty(),
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Text(
                text = if (update.streaming) "STREAMING" else "IDLE",
                style = MaterialTheme.typography.caption1,
                color = if (update.streaming) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
            )
        }
        item {
            Text(
                text = "HR ${update.lastHr ?: "--"}",
                style = MaterialTheme.typography.display3,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Text(
                text = "SpO₂ ${update.lastSpo2 ?: "--"}% · ${update.lastTemp ?: "--"}°C",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Text(
                text = "Risk ${update.risk ?: "—"} · ${update.activity ?: "—"}",
                style = MaterialTheme.typography.caption2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Text(
                text = buildString {
                    append(update.message ?: "Ready")
                    if ((update.queued) > 0) append(" · Q ${update.queued}")
                    update.source?.let { append(" · $it") }
                },
                style = MaterialTheme.typography.caption3,
                color = if (update.error != null) {
                    MaterialTheme.colors.error
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.55f)
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        item {
            if (update.streaming) {
                Button(
                    onClick = { stopStream() },
                    colors = ButtonDefaults.secondaryButtonColors(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Stop")
                }
            } else {
                Button(
                    onClick = { startStream() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Start stream")
                }
            }
        }
        item {
            CompactChip(
                onClick = {
                    scope.launch {
                        stopStream()
                        sessionStore.clear()
                    }
                },
                label = { Text("Log out") },
            )
        }
        item {
            Text(
                text = session.apiBase,
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        }
    }
}
