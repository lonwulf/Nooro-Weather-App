package com.lonwulf.nooro.weatherapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.nooro.weatherapp.core.util.GenericResultState
import com.lonwulf.nooro.weatherapp.domain.model.WeatherHistoryPreferences
import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel
import com.lonwulf.nooro.weatherapp.domain.usecase.FetchHistoryFromCacheUseCase
import com.lonwulf.nooro.weatherapp.domain.usecase.WeatherForeCastUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class SharedViewModel(
    private val weatherForeCastUseCase: WeatherForeCastUseCase,
    private val fetchHistoryFromCacheUseCase: FetchHistoryFromCacheUseCase
) : ViewModel() {
    private var _weatherForeCastStateFlow =
        MutableStateFlow<GenericResultState<WeatherModel>>(GenericResultState.Loading)
    val weatherForeCastStateFlow
        get() = _weatherForeCastStateFlow.asStateFlow()
    private var _weatherPreferencesList =
        MutableStateFlow<GenericResultState<List<WeatherHistoryPreferences>>>(GenericResultState.Loading)
    val weatherPreferencesList
        get() = _weatherPreferencesList.asStateFlow()

    fun fetchWeatherForeCast(query: String) = viewModelScope.launch(Dispatchers.IO) {
        weatherForeCastUseCase(query).onStart {
            setWeatherForeCastApiResult(GenericResultState.Loading)
        }
            .flowOn(Dispatchers.IO).collect { result ->
                if (result.isSuccessful) {
                    setWeatherForeCastApiResult(GenericResultState.Success(result.result))
                } else {
                    setWeatherForeCastApiResult(GenericResultState.Error(result.msg))
                }
            }

    }

    fun fetchAllHistory() = viewModelScope.launch(Dispatchers.IO) {
        fetchHistoryFromCacheUseCase().onStart { setWeatherHistoryResult(GenericResultState.Loading)}
            .flowOn(Dispatchers.IO).collect {
                setWeatherHistoryResult(GenericResultState.Success(it))
            }
    }

    fun clearAllData() = viewModelScope.launch(Dispatchers.IO) {
        fetchHistoryFromCacheUseCase.clearHistory()
    }

    fun addWeatherHistory(model: WeatherModel) = viewModelScope.launch(Dispatchers.IO) {
        fetchHistoryFromCacheUseCase.addHistory(
            WeatherHistoryPreferences(
                name = model.name ?: "",
                humidity = model.humidity ?: 0,
                temp = model.tempC ?: 0.0,
                iconUrl = model.iconUrl ?: "",
                feelsLike = model.feelsLike ?: 0.0,
                condition = model.condition ?: ""
            )
        )
    }


    private fun setWeatherForeCastApiResult(data: GenericResultState<WeatherModel>) {
        _weatherForeCastStateFlow.value = data
    }

    private fun setWeatherHistoryResult(data: GenericResultState<List<WeatherHistoryPreferences>>) {
        _weatherPreferencesList.value = data
    }
}
