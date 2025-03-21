
package me.alexy.hipipl.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.alexy.hipipl.core.data.DefaultGeoRepository
import me.alexy.hipipl.core.data.DefaultHostRepository
import me.alexy.hipipl.core.data.GeoRepository
import me.alexy.hipipl.core.data.HostRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsHostRepository(
        hostRepository: DefaultHostRepository
    ): HostRepository

    @Singleton
    @Binds
    fun bindsGeoRepository(
        geoRepository: DefaultGeoRepository
    ): GeoRepository
}
