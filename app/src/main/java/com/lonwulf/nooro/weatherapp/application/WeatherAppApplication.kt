package com.lonwulf.nooro.weatherapp.application

import android.app.Application
import com.lonwulf.nooro.weatherapp.core.di.networkModule
import com.lonwulf.nooro.weatherapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WeatherAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WeatherAppApplication)
            modules(appModule, networkModule)
        }
    }
}