package me.alexy.hipipl.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.alexy.hipipl.core.domain.AuthState
import java.io.IOException

/**
 * Holds the logged-in session as a [Flow] of [AuthState] and persists JWT/refresh token via
 * DataStore Preferences (EncryptedSharedPreferences is deprecated, so plain DataStore is used -
 * the tokens are short-lived/rotatable, not long-term secrets like a password).
 */
class SessionManager(private val context: Context) {

    private object Keys {
        val USER_ID = intPreferencesKey("user_id")
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val authState: Flow<AuthState> = context.authDataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val userId = prefs[Keys.USER_ID]
            val jwtToken = prefs[Keys.JWT_TOKEN]
            val refreshToken = prefs[Keys.REFRESH_TOKEN]
            if (userId != null && jwtToken != null && refreshToken != null) {
                AuthState.Authenticated(userId = userId, account = null)
            } else {
                AuthState.Unauthenticated
            }
        }

    suspend fun currentUserId(): Int? = context.authDataStore.data.first()[Keys.USER_ID]

    suspend fun save(userId: Int, jwtToken: String, refreshToken: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.JWT_TOKEN] = jwtToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun updateJwt(jwtToken: String) {
        context.authDataStore.edit { prefs -> prefs[Keys.JWT_TOKEN] = jwtToken }
    }

    suspend fun tokens(): BearerTokens? {
        val prefs = context.authDataStore.data.first()
        val jwtToken = prefs[Keys.JWT_TOKEN] ?: return null
        val refreshToken = prefs[Keys.REFRESH_TOKEN] ?: return null
        return BearerTokens(accessToken = jwtToken, refreshToken = refreshToken)
    }

    suspend fun clear() {
        context.authDataStore.edit {
            it.remove(Keys.USER_ID)
            it.remove(Keys.JWT_TOKEN)
            it.remove(Keys.REFRESH_TOKEN)
        }
    }
}
