package me.alexy.hipipl.feature.hostme.di

import me.alexy.hipipl.feature.hostme.ui.HostDetailsViewModel
import me.alexy.hipipl.feature.hostme.ui.HostListByLocationViewModel
import me.alexy.hipipl.feature.hostme.ui.LocationSearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hostmePresentationModule = module {
    viewModelOf(::LocationSearchViewModel)
    viewModelOf(::HostListByLocationViewModel)
    viewModelOf(::HostDetailsViewModel)
}
