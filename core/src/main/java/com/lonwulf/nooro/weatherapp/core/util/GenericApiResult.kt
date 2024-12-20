package com.lonwulf.nooro.weatherapp.core.util

data class GenericApiResult<out T>(
    val results: T,
    val isSuccessful: Boolean,
    val msg: String? = "success"
)
