package com.lonwulf.nooro.weatherapp

import com.lonwulf.nooro.weatherapp.core.util.APIResult
import com.lonwulf.nooro.weatherapp.core.util.HttpResult
import com.lonwulf.nooro.weatherapp.data.repository.AppRepositoryImpl
import com.lonwulf.nooro.weatherapp.data.response.WeatherDTO
import com.lonwulf.nooro.weatherapp.data.source.AppRemoteDataSource
import com.lonwulf.nooro.weatherapp.domain.mapper.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class AppRepositoryImplTest {

    private lateinit var repository: AppRepositoryImpl
    private val mockRemoteDataSource: AppRemoteDataSource = mock()

    @Before
    fun setUp() {
        repository = AppRepositoryImpl(mockRemoteDataSource)
    }

    @Test
    fun `getWeatherForeCast should return APIResult_Loading`(): Unit = runTest {
        // Given
        val query = "Nairobi"
        `when`(mockRemoteDataSource.fetchWeatherForeCast(Dispatchers.IO, query))
            .thenReturn(APIResult.Loading)

        // When
        val result = repository.getWeatherForeCast(query)

        // Then
        assertEquals(APIResult.Loading, result)
        verify(mockRemoteDataSource, times(1)).fetchWeatherForeCast(Dispatchers.IO, query)
    }

    @Test
    fun `getWeatherForeCast should return APIResult_Success`(): Unit = runTest {
        // Given
        val query = "Nairobi"
        val apiResponse = APIResult.Success(MockWeatherModel.apiWeatherDTO)
        val domainModel = MockWeatherModel.domainWeatherModel

        // Mock the data source response
        `when`(mockRemoteDataSource.fetchWeatherForeCast(Dispatchers.IO, query))
            .thenReturn(apiResponse)

        // When
        val result = repository.getWeatherForeCast(query)

        // Then
        assert(result is APIResult.Success)
        val successResult = result as APIResult.Success
        assertEquals(domainModel, successResult.result)

        verify(mockRemoteDataSource, times(1)).fetchWeatherForeCast(Dispatchers.IO, query)
    }

    @Test
    fun `getWeatherForeCast should return APIResult_Error`(): Unit = runTest {
        // Given
        val query = "InvalidCity"
        val errorCode = 404
        val errorMessage = "Not Found"
        val apiError = APIResult.Error(errorCode, errorMessage, HttpResult.NOT_FOUND)

        `when`(mockRemoteDataSource.fetchWeatherForeCast(Dispatchers.IO, query))
            .thenReturn(apiError)

        // When
        val result = repository.getWeatherForeCast(query)

        // Then
        assertEquals(apiError, result)
        verify(mockRemoteDataSource, times(1)).fetchWeatherForeCast(Dispatchers.IO, query)
    }
}

object MockWeatherModel {
    val apiWeatherDTO = WeatherDTO(
        current = WeatherDTO.Current(uv = 4.0, temp_c = 34.1, humidity = 5),
        location = WeatherDTO.Location(name = "Nairobi")
    )
    val domainWeatherModel = apiWeatherDTO.toDomainModel()
}