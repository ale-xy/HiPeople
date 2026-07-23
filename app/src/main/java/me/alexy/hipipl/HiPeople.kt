
package me.alexy.hipipl

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.vk.id.VKID
import me.alexy.hipipl.core.data.di.coreDataModule
import me.alexy.hipipl.feature.auth.di.authPresentationModule
import me.alexy.hipipl.feature.hostme.di.hostmePresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HiPeople : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

        VKID.init(this)
        VKID.logsEnabled = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        startKoin {
            androidLogger()
            androidContext(this@HiPeople)
            modules(
                coreDataModule,
                hostmePresentationModule,
                authPresentationModule,
            )
        }
    }
    
    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}
