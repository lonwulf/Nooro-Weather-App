package com.lonwulf.nooro.weatherapp.domain.usecase

import com.lonwulf.nooro.weatherapp.core.util.APIResult
import com.lonwulf.nooro.weatherapp.core.util.GenericUseCaseResult
import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel
import com.lonwulf.nooro.weatherapp.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherForeCastUseCase(private val repository: AppRepository) {

    operator fun invoke(query: String): Flow<GenericUseCaseResult<WeatherModel?>> =
        flow {
            when (val response = repository.getWeatherForeCast(query)) {
                is APIResult.Loading -> APIResult.Loading
                is APIResult.Success -> {
                    val result =
                        GenericUseCaseResult(result = response.result, isSuccessful = true)
                    emit(result)
                }

                is APIResult.Error -> {
                    val errorResult = GenericUseCaseResult(
                        result = null,
                        isSuccessful = false,
                        msg = response.msg
                    )
                    emit(errorResult)
                }
            }
        }
}
