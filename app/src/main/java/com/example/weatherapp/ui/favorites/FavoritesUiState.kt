package com.example.weatherapp.ui.favorites

import com.example.weatherapp.domain.model.FavoriteCity

data class FavoritesUiState(
    val uid: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val favorites: List<FavoriteCity> = emptyList()
)