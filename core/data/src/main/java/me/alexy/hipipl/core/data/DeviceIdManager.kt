package me.alexy.hipipl.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Generates and persists a stable per-install device id (base64url, per
 * `POST_auth_login_vk.md`'s `device_id` param). Owns the sole `device_id` entry in the auth
 * DataStore - callers needing it for a login/refresh request go through here rather than
 * duplicating storage.
 */
class DeviceIdManager(private val context: Context) {

    private val key = stringPreferencesKey("device_id")

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getOrCreate(): String {
        val existing = context.authDataStore.data.first()[key]
        if (existing != null) return existing

        val randomBytes = ByteArray(48).also { SecureRandom().nextBytes(it) }
        val generated = "android_" + Base64.UrlSafe.encode(randomBytes).trimEnd('=')
        context.authDataStore.edit { it[key] = generated }
        return generated
    }
}
