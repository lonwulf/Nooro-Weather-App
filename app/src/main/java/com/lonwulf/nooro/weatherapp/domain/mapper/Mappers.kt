package com.lonwulf.nooro.weatherapp.domain.mapper

import com.lonwulf.nooro.weatherapp.data.response.WeatherDTO
import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel

fun WeatherDTO.toDomainModel(): WeatherModel =
    WeatherModel(
        name = this.location?.name,
        region = this.location?.region,
        tempC = this.current?.temp_c,
        condition = this.current?.condition?.text,
        iconUrl = this.current?.condition?.icon,
        cloud = this.current?.cloud,
        humidity = this.current?.humidity,
        pressure = this.current?.pressure_in,
        localTime = this.location?.localtime,
        heatIndex = this.current?.heatindex_c,
        feelsLike = this.current?.feelslike_c,
        uv = this.current?.uv
    )
