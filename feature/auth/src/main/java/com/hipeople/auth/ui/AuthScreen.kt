package com.hipeople.auth.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vk.id.auth.AuthCodeData
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.compose.onetap.OneTap

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit,
    onAuthFailure: () -> Unit
) {
    val authParams = VKIDAuthUiParams {
        scopes = setOf("phone", "email")
    }
    Scaffold(
        modifier = modifier,
        content = { padding ->

            Box (
                modifier = modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                OneTap(
                    signInAnotherAccountButtonEnabled = true,
                    authParams = authParams,
                    onAuth = { _, accessToken ->
                        Log.d("AuthScreen", "VK auth token ${accessToken.token} ")
                        Log.d("AuthScreen", "VK auth idtoken ${accessToken.idToken} ")
                        Log.d("AuthScreen", "VK auth userid ${accessToken.userID} ")
                        Log.d("AuthScreen", "VK auth email ${accessToken.userData.email} ")
                        Log.d("AuthScreen", "VK auth phone ${accessToken.userData.phone} ")

                        viewModel.onVkAuth(accessToken.token, onAuthSuccess, onAuthFailure)
                    },
                    onAuthCode = { data: AuthCodeData, isCompletion: Boolean ->
                        Log.d("AuthScreen", "VK auth code ${data.code}")
                        Log.d("AuthScreen", "VK auth deviceid ${data.deviceId}")
                        Log.d("AuthScreen", "VK auth isCompletion ${isCompletion}")

                    },
                    onFail = { _, fail ->
                        Log.e("AuthScreen", "VK auth fail ${fail.description}")
                        onAuthFailure()
                    },
                )
            }
        }
    )
}