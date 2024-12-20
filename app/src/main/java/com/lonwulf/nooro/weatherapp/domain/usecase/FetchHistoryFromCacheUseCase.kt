package com.lonwulf.nooro.weatherapp.domain.usecase

import com.lonwulf.nooro.weatherapp.domain.model.WeatherHistoryPreferences
import com.lonwulf.nooro.weatherapp.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class FetchHistoryFromCacheUseCase(private val dataStoreRepository: DataStoreRepository) {
    operator fun invoke(): Flow<List<WeatherHistoryPreferences>> =
        dataStoreRepository.weatherHistory

    suspend fun clearHistory() = dataStoreRepository.clearWeatherHistory()

    suspend fun addHistory(weatherObject: WeatherHistoryPreferences) {
        dataStoreRepository.addWeatherHistory(weatherObject)
    }
}