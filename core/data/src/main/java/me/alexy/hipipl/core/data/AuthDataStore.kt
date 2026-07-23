package me.alexy.hipipl.core.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.authDataStore by preferencesDataStore(name = "auth_session")
