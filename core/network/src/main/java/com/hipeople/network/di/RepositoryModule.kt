package com.hipeople.network.di

import com.hipeople.network.repository.NetworkAuthRepository
import com.hipeople.network.repository.NetworkGeoRepository
import com.hipeople.network.repository.NetworkHostRepository
import com.hipeople.repository.AuthRepository
import com.hipeople.repository.GeoRepository
import com.hipeople.repository.HostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsHostRepository(
        hostRepository: NetworkHostRepository
    ): HostRepository

    @Singleton
    @Binds
    fun bindsGeoRepository(
        geoRepository: NetworkGeoRepository
    ): GeoRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(
        authRepository: NetworkAuthRepository
    ): AuthRepository

}
