package com.vitalsync.wear

import android.app.Application
import com.vitalsync.wear.data.SessionStore
import com.vitalsync.wear.data.VitalSyncRepository

class VitalSyncWearApp : Application() {
    lateinit var sessionStore: SessionStore
        private set
    lateinit var repository: VitalSyncRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionStore = SessionStore(this)
        repository = VitalSyncRepository(sessionStore)
    }
}
