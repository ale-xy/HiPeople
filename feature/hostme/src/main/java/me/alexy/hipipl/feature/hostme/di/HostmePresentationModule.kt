package me.alexy.hipipl.feature.hostme.di

import me.alexy.hipipl.feature.hostme.ui.HostDetailsViewModel
import me.alexy.hipipl.feature.hostme.ui.HostsSearchViewModel
import me.alexy.hipipl.feature.hostme.ui.UserSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hostmePresentationModule = module {
    viewModelOf(::HostsSearchViewModel)
    viewModelOf(::UserSearchViewModel)
    viewModelOf(::HostDetailsViewModel)
}
