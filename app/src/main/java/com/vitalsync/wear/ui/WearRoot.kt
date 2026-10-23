package com.vitalsync.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vitalsync.wear.data.SessionStore
import com.vitalsync.wear.data.VitalSyncRepository

@Composable
fun WearRoot(
    sessionStore: SessionStore,
    repository: VitalSyncRepository,
) {
    val session by sessionStore.sessionFlow.collectAsState(
        initial = SessionStore.Session(
            token = null,
            email = null,
            name = null,
            userId = null,
            apiBase = com.vitalsync.wear.BuildConfig.DEFAULT_API_BASE,
        ),
    )

    if (session.token.isNullOrBlank()) {
        LoginScreen(
            initialApiBase = session.apiBase,
            repository = repository,
        )
    } else {
        StreamScreen(
            session = session,
            sessionStore = sessionStore,
        )
    }
}
