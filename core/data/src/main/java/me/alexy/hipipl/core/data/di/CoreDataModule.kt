package me.alexy.hipipl.core.data.di

import io.ktor.client.engine.okhttp.OkHttp
import me.alexy.hipipl.core.data.DeviceIdManager
import me.alexy.hipipl.core.data.HttpClientFactory
import me.alexy.hipipl.core.data.KtorAuthDataSource
import me.alexy.hipipl.core.data.KtorContactSearchDataSource
import me.alexy.hipipl.core.data.KtorFavoritesDataSource
import me.alexy.hipipl.core.data.KtorGeoDataSource
import me.alexy.hipipl.core.data.KtorHostDataSource
import me.alexy.hipipl.core.data.KtorHostMapDataSource
import me.alexy.hipipl.core.data.KtorMessagingDataSource
import me.alexy.hipipl.core.data.KtorSupportDataSource
import me.alexy.hipipl.core.data.SessionManager
import me.alexy.hipipl.core.domain.AuthRemoteDataSource
import me.alexy.hipipl.core.domain.ContactSearchRemoteDataSource
import me.alexy.hipipl.core.domain.FavoritesRemoteDataSource
import me.alexy.hipipl.core.domain.GeoRemoteDataSource
import me.alexy.hipipl.core.domain.HostMapRemoteDataSource
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import me.alexy.hipipl.core.domain.MessagingRemoteDataSource
import me.alexy.hipipl.core.domain.SupportRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::SessionManager)
    singleOf(::DeviceIdManager)

    single { HttpClientFactory.create(OkHttp.create(), get(), get()) }

    singleOf(::KtorGeoDataSource) bind GeoRemoteDataSource::class
    singleOf(::KtorHostDataSource) bind HostRemoteDataSource::class
    singleOf(::KtorHostMapDataSource) bind HostMapRemoteDataSource::class
    singleOf(::KtorContactSearchDataSource) bind ContactSearchRemoteDataSource::class
    singleOf(::KtorFavoritesDataSource) bind FavoritesRemoteDataSource::class
    singleOf(::KtorSupportDataSource) bind SupportRemoteDataSource::class
    singleOf(::KtorMessagingDataSource) bind MessagingRemoteDataSource::class
    singleOf(::KtorAuthDataSource) bind AuthRemoteDataSource::class
}
