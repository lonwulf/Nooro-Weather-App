package com.lonwulf.nooro.weatherapp.domain.repository

import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel
import com.lonwulf.nooro.weatherapp.core.util.APIResult

interface AppRepository {
    suspend fun getWeatherForeCast(query:String): APIResult<WeatherModel>

}
