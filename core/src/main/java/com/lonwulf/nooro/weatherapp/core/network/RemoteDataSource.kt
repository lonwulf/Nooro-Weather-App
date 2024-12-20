package com.lonwulf.nooro.weatherapp.core.network

import com.lonwulf.nooro.weatherapp.core.util.APIResult
import com.lonwulf.nooro.weatherapp.core.util.HttpResult
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException

open class RemoteDataSource {

    open suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): APIResult<T> {
        return withContext(dispatcher) {
            try {
                APIResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        when (throwable.code()) {
                            in 400..451 -> parseHttpError(throwable)
                            in 500..599 -> error(
                                HttpResult.SERVER_ERROR,
                                throwable.code(),
                                throwable.message
                            )

                            else -> error(
                                HttpResult.UN_DEFINED,
                                throwable.code(),
                                throwable.message()
                            )
                        }
                    }

                    is SocketTimeoutException -> error(HttpResult.TIMEOUT, null, "time out")
                    else -> error(HttpResult.UN_DEFINED, null, throwable.message)
                }
            }
        }
    }

    private fun parseHttpError(exception: HttpException): APIResult<Nothing> {
        return try {
            val errorBody = exception.response()?.errorBody()?.toString() ?: "unknown error body"
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(ErrorParser::class.java)
            val errorMsg = adapter.fromJson(errorBody)
            error(HttpResult.CLIENT_ERROR, exception.code(), errorMsg?.message)
        } catch (ex: Exception) {
            error(HttpResult.CLIENT_ERROR, exception.code(), exception.localizedMessage)
        }
    }

    private fun error(cause: HttpResult, code: Int?, msg: String?): APIResult.Error =
        APIResult.Error(code, msg, cause)

    data class ErrorParser(@Json(name = "message") val message: String? = null)
}
