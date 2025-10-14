
package com.hipeople

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.vk.id.VKID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiPeople : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
        VKID.logsEnabled = true
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}
