package com.aptive.mediaplayer.injection
import com.aptive.mediaplayer.repository.MediaRepository
import org.koin.dsl.module

val appModule = module {

    single {
        MediaRepository()
    }


}