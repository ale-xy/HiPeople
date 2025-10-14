package com.hipeople.auth.ui

import androidx.lifecycle.ViewModel
import com.hipeople.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) //todo auth repo
    : ViewModel() {

    fun onVkAuth(
        token: String,
        onAuthSuccess: () -> Unit,
        onAuthFailure: () -> Unit
    ) {

    }


}