
package me.alexy.hipipl

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import me.alexy.hipipl.core.data.di.coreDataModule
import me.alexy.hipipl.feature.hostme.di.hostmePresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HiPeople : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@HiPeople)
            modules(
                coreDataModule,
                hostmePresentationModule
            )
        }
    }
    
    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}
