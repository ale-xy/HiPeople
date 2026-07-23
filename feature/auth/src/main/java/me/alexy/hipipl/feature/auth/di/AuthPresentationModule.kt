package me.alexy.hipipl.feature.auth.di

import me.alexy.hipipl.feature.auth.ui.AuthViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::AuthViewModel)
}
