package com.lonwulf.nooro.weatherapp.data.util

import androidx.datastore.core.Serializer
import com.lonwulf.nooro.weatherapp.domain.model.AppSettings
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings
        get() = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(serializer = AppSettings.serializer(), value = t)
                .encodeToByteArray()
        )
    }
}