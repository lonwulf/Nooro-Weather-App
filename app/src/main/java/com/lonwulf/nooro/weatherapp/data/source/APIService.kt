package com.lonwulf.nooro.weatherapp.data.source

import com.lonwulf.nooro.weatherapp.data.response.WeatherDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("v1/current.json")
    suspend fun getWeatherForeCast(
        @Query("q") query: String, @Query("key") apiKey: String,
        @Query("aqi") aqi: String
    ): WeatherDTO
}
