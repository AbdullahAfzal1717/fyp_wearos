package com.vitalsync.wear.stream

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lightweight in-process bus so the foreground service can push UI updates.
 */
object StreamBus {
    data class Update(
        val streaming: Boolean = false,
        val lastHr: Int? = null,
        val lastSpo2: Int? = null,
        val lastTemp: Double? = null,
        val activity: String? = null,
        val risk: String? = null,
        val source: String? = null,
        val queued: Int = 0,
        val message: String? = null,
        val error: String? = null,
    )

    private val _state = MutableStateFlow(Update())
    val state: StateFlow<Update> = _state.asStateFlow()

    fun publish(update: Update) {
        _state.value = update
    }
}
