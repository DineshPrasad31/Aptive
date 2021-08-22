package com.aptive.mediaplayer.application

import android.app.Application
import com.aptive.mediaplayer.injection.appModule
import org.koin.core.context.startKoin

class MediaPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}