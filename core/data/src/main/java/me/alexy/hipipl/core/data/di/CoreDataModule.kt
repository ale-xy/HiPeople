package me.alexy.hipipl.core.data.di

import io.ktor.client.engine.okhttp.OkHttp
import me.alexy.hipipl.core.data.HttpClientFactory
import me.alexy.hipipl.core.data.KtorGeoDataSource
import me.alexy.hipipl.core.data.KtorHostDataSource
import me.alexy.hipipl.core.domain.GeoRemoteDataSource
import me.alexy.hipipl.core.domain.HostRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single { HttpClientFactory.create(OkHttp.create()) }
    
    singleOf(::KtorGeoDataSource) bind GeoRemoteDataSource::class
    singleOf(::KtorHostDataSource) bind HostRemoteDataSource::class
}
