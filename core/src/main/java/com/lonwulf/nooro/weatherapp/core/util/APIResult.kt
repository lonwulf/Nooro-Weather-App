package com.lonwulf.nooro.weatherapp.core.util

sealed class APIResult<out T> {
    object Loading : APIResult<Nothing>()
    data class Success<out T>(val result: T) : APIResult<T>()
    data class Error(val code: Int? = null, val msg: String? = null, val cause: HttpResult) :
        APIResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data = $result]"
            is Error -> "Error [code = $code, msg = $msg, cause = $cause]"
            is Loading -> "Loading"
        }
    }
}

enum class HttpResult {
    SLOW_CONNECTION, TIMEOUT, SERVER_ERROR, CLIENT_ERROR, BAD_RESPONSE, UN_DEFINED
}
