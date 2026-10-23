package com.vitalsync.wear.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vitalsync.wear.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "vitalsync_wear")

class SessionStore(private val context: Context) {
    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val USER_ID = stringPreferencesKey("user_id")
        val API_BASE = stringPreferencesKey("api_base")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val apiBaseFlow: Flow<String> = context.dataStore.data.map {
        it[Keys.API_BASE] ?: BuildConfig.DEFAULT_API_BASE
    }

    data class Session(
        val token: String?,
        val email: String?,
        val name: String?,
        val userId: String?,
        val apiBase: String,
    )

    val sessionFlow: Flow<Session> = context.dataStore.data.map { prefs ->
        Session(
            token = prefs[Keys.TOKEN],
            email = prefs[Keys.EMAIL],
            name = prefs[Keys.NAME],
            userId = prefs[Keys.USER_ID],
            apiBase = prefs[Keys.API_BASE] ?: BuildConfig.DEFAULT_API_BASE,
        )
    }

    suspend fun getToken(): String? = context.dataStore.data.first()[Keys.TOKEN]

    suspend fun getApiBase(): String =
        context.dataStore.data.first()[Keys.API_BASE] ?: BuildConfig.DEFAULT_API_BASE

    suspend fun saveLogin(
        token: String,
        user: UserData,
        apiBase: String,
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOKEN] = token
            prefs[Keys.EMAIL] = user.email.orEmpty()
            prefs[Keys.NAME] = user.name.orEmpty()
            prefs[Keys.USER_ID] = user.userId
            prefs[Keys.API_BASE] = apiBase.trimEnd('/')
        }
    }

    suspend fun saveApiBase(apiBase: String) {
        context.dataStore.edit { it[Keys.API_BASE] = apiBase.trimEnd('/') }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.TOKEN)
            prefs.remove(Keys.EMAIL)
            prefs.remove(Keys.NAME)
            prefs.remove(Keys.USER_ID)
        }
    }
}
