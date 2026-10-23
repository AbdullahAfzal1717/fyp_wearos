package com.vitalsync.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.vitalsync.wear.ui.WearRoot
import com.vitalsync.wear.ui.theme.VitalSyncWearTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as VitalSyncWearApp
        setContent {
            VitalSyncWearTheme {
                WearRoot(
                    sessionStore = app.sessionStore,
                    repository = app.repository,
                )
            }
        }
    }
}
