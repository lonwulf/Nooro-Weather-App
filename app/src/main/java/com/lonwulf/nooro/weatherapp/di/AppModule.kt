package com.lonwulf.nooro.weatherapp.di

import com.lonwulf.nooro.weatherapp.core.util.NetworkMonitor
import com.lonwulf.nooro.weatherapp.data.repository.AppRepositoryImpl
import com.lonwulf.nooro.weatherapp.data.repository.DataStoreRepositoryImpl
import com.lonwulf.nooro.weatherapp.data.source.APIService
import com.lonwulf.nooro.weatherapp.data.source.AppRemoteDataSource
import com.lonwulf.nooro.weatherapp.domain.repository.AppRepository
import com.lonwulf.nooro.weatherapp.domain.repository.DataStoreRepository
import com.lonwulf.nooro.weatherapp.domain.usecase.FetchHistoryFromCacheUseCase
import com.lonwulf.nooro.weatherapp.domain.usecase.WeatherForeCastUseCase
import com.lonwulf.nooro.weatherapp.ui.viewmodel.SharedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    single<APIService> {
        get<Retrofit>().create(APIService::class.java)
    }
    single { AppRemoteDataSource(get()) }
    single<AppRepository> { AppRepositoryImpl(get()) }
    single<DataStoreRepository> { DataStoreRepositoryImpl(androidContext()) }
    single { WeatherForeCastUseCase(get()) }
    single { FetchHistoryFromCacheUseCase(get()) }
    single { NetworkMonitor(androidContext()) }
    viewModel { SharedViewModel(get(), get()) }
}
