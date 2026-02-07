package com.example.weatherapp.domain.model

data class FavoriteCity(
    val id: String = "",
    val cityName: String = "",
    val note: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = ""
)