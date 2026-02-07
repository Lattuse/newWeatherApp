package com.example.weatherapp.data.firebase

data class FavoriteCityDto(
    var id: String? = null,
    var cityName: String? = null,
    var note: String? = null,
    var createdAt: Long? = null,
    var createdBy: String? = null
)