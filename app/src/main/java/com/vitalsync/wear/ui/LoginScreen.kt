package com.vitalsync.wear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.vitalsync.wear.data.VitalSyncRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    initialApiBase: String,
    repository: VitalSyncRepository,
) {
    var email by remember { mutableStateOf("patient1@test.com") }
    var password by remember { mutableStateOf("123456") }
    var apiBase by remember { mutableStateOf(initialApiBase) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        item {
            Text(
                text = "VitalSync",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }
        item {
            Text(
                text = "Wear login (USER)",
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            WearField(label = "Email", value = email, onChange = { email = it })
        }
        item {
            WearField(
                label = "Password",
                value = password,
                onChange = { password = it },
                password = true,
            )
        }
        item {
            WearField(label = "API base URL", value = apiBase, onChange = { apiBase = it })
        }
        item {
            Text(
                text = "Emulator → 10.0.2.2:5000\nReal watch → PC LAN IP:5000",
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.55f),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        if (error != null) {
            item {
                Text(
                    text = error!!,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption2,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
        item {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(12.dp))
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            loading = true
                            error = null
                            try {
                                repository.login(email, password, apiBase.trim())
                            } catch (e: Exception) {
                                error = e.message ?: "Login failed"
                            } finally {
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) {
                    Text("Sign in")
                }
            }
        }
        item {
            CompactChip(
                onClick = { apiBase = "http://10.0.2.2:5000" },
                label = { Text("Emulator host") },
            )
        }
    }
}

@Composable
private fun WearField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    password: Boolean = false,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.caption3,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontSize = 13.sp,
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            visualTransformation = if (password) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
