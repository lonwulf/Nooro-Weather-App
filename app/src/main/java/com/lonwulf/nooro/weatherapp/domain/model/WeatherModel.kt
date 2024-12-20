package com.lonwulf.nooro.weatherapp.domain.model

data class WeatherModel(
    val name: String? = null,
    val region: String? = null,
    val tempC: Double? = null,
    val condition: String? = null,
    val iconUrl: String? = null,
    val cloud: Int? = null,
    val humidity: Int? = null,
    val pressure:Double? = null,
    val localTime:String? = null,
    val heatIndex:Double? = null,
    val feelsLike:Double? = null,
    val uv:Int? = null
)
