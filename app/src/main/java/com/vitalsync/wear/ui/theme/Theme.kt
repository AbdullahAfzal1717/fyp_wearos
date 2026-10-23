package com.vitalsync.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val colors = Colors(
    primary = Color(0xFF3B82F6),
    primaryVariant = Color(0xFF1D4ED8),
    secondary = Color(0xFF22C55E),
    secondaryVariant = Color(0xFF16A34A),
    background = Color(0xFF0B1220),
    surface = Color(0xFF111827),
    error = Color(0xFFEF4444),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White,
)

@Composable
fun VitalSyncWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = colors,
        content = content,
    )
}
