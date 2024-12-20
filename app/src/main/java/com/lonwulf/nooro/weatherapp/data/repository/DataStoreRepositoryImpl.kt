package com.lonwulf.nooro.weatherapp.data.repository

import android.content.Context
import androidx.datastore.dataStore
import com.lonwulf.nooro.weatherapp.data.util.AppSettingsSerializer
import com.lonwulf.nooro.weatherapp.domain.model.WeatherHistoryPreferences
import com.lonwulf.nooro.weatherapp.domain.repository.DataStoreRepository
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(private val context: Context) : DataStoreRepository {
    private val Context.weatherHistoryDataStore by dataStore(
        "weather-settings.json",
        AppSettingsSerializer
    )

    override val weatherHistory: Flow<List<WeatherHistoryPreferences>> =
        context.weatherHistoryDataStore.data
            .map { preferences ->
                preferences.history
            }

    override suspend fun addWeatherHistory(weatherItem: WeatherHistoryPreferences) {
        context.weatherHistoryDataStore.updateData { preferences ->
            preferences.copy(history = preferences.history.mutate {
                it.add(
                    WeatherHistoryPreferences(
                        name = weatherItem.name,
                        humidity = weatherItem.humidity,
                        temp = weatherItem.temp,
                        iconUrl = weatherItem.iconUrl,
                        feelsLike = weatherItem.feelsLike,
                        condition = weatherItem.condition,
                        uv = weatherItem.uv
                    )
                )
            })
        }
    }

    override suspend fun clearWeatherHistory() {
        context.weatherHistoryDataStore.updateData { preferences ->
            preferences.copy(history = preferences.history.clear())
        }
    }
}