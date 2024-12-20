package com.lonwulf.nooro.weatherapp.data.repository

import com.lonwulf.nooro.weatherapp.core.util.APIResult
import com.lonwulf.nooro.weatherapp.data.source.AppRemoteDataSource
import com.lonwulf.nooro.weatherapp.domain.mapper.toDomainModel
import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel
import com.lonwulf.nooro.weatherapp.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers

class AppRepositoryImpl(private val source: AppRemoteDataSource) : AppRepository {
    override suspend fun getWeatherForeCast(
        query: String
    ): APIResult<WeatherModel> {
        return when (val response = source.fetchWeatherForeCast(Dispatchers.IO, query)) {
            is APIResult.Loading -> APIResult.Loading
            is APIResult.Success -> {
                val result = response.result.toDomainModel()
                APIResult.Success(result)
            }

            is APIResult.Error -> APIResult.Error(response.code, response.msg, response.cause)
        }
    }
}
