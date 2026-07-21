package me.alexy.hipipl.feature.hostme.di

import me.alexy.hipipl.feature.hostme.ui.hostdetails.HostDetailsViewModel
import me.alexy.hipipl.feature.hostme.ui.hostsearch.HostsSearchViewModel
import me.alexy.hipipl.feature.hostme.ui.usersearch.UserSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hostmePresentationModule = module {
    viewModelOf(::HostsSearchViewModel)
    viewModelOf(::UserSearchViewModel)
    viewModelOf(::HostDetailsViewModel)
}
