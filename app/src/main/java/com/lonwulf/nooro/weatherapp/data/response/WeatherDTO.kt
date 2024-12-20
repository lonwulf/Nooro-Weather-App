package com.lonwulf.nooro.weatherapp.data.response

import com.squareup.moshi.Json


data class WeatherDTO(
    @Json(name = "location")
    var location: Location? = null,

    @Json(name = "current")
    var current: Current? = null
) {
    data class Location(
        @Json(name = "name")
        var name: String? = null,

        @Json(name = "region")
        var region: String? = null,

        @Json(name = "country")
        var country: String? = null,

        @Json(name = "lat")
        var lat: Double? = null,

        @Json(name = "lon")
        var lon: Double? = null,

        @Json(name = "tz_id")
        var tzId: String? = null,

        @Json(name = "localtime_epoch")
        var localtime_epoch: Int? = null,

        @Json(name = "localtime")
        var localtime: String? = null
    )

    data class Current(
        @Json(name = "last_updated_epoch")
        var lastUpdatedEpoch: Int? = null,

        @Json(name = "last_updated")
        var lastUpdated: String? = null,

        @Json(name = "temp_c")
        var temp_c: Double? = null,

        @Json(name = "temp_f")
        var temp_f: Double? = null,

        @Json(name = "is_day")
        var is_day: Int? = null,

        @Json(name = "condition")
        var condition: Condition? = null,

        @Json(name = "wind_kph")
        var wind_kph: Double? = null,

        @Json(name = "wind_dir")
        var windDir: String? = null,

        @Json(name = "pressure_in")
        var pressure_in: Double? = null,

        @Json(name = "precip_mm")
        var precip_mm: Double? = null,

        @Json(name = "precip_in")
        var precip_in: Double? = null,

        @Json(name = "humidity")
        var humidity: Int? = null,

        @Json(name = "cloud")
        var cloud: Int? = null,

        @Json(name = "feelslike_c")
        var feelslike_c: Double? = null,

        @Json(name = "windchill_c")
        var windchill_c: Double? = null,

        @Json(name = "heatindex_c")
        var heatindex_c: Double? = null,

        @Json(name = "dewpoint_c")
        var dewpoint_c: Double? = null,

        @Json(name = "vis_km")
        var visKm: Double? = null,

        @Json(name = "gust_kph")
        var gust_kph: Double? = null,

        @Json(name = "uv")
        var uv: Double? = null
    ) {
        class Condition {
            @Json(name = "text")
            var text: String? = null

            @Json(name = "icon")
            var icon: String? = null

            @Json(name = "code")
            var code: Double? = null
        }
    }
}
