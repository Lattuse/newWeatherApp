package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.local.AppDataStore
import com.example.weatherapp.data.remote.RetrofitProvider
import com.example.weatherapp.data.repository.AuthRepository
import com.example.weatherapp.data.repository.FavoritesRepository
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.favorites.FavoritesViewModel
import com.example.weatherapp.ui.screens.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Assignment 7
        val dataStore = AppDataStore(this)
        val weatherRepo = WeatherRepository(
            geocodingApi = RetrofitProvider.geocodingApi(),
            forecastApi = RetrofitProvider.forecastApi(),
            dataStore = dataStore
        )

        // Assignment 8
        val authRepo = AuthRepository()
        val favoritesRepo = FavoritesRepository()

        setContent {
            val weatherVm: WeatherViewModel = viewModel(
                factory = SimpleVmFactory { WeatherViewModel(weatherRepo, dataStore) }
            )

            val favoritesVm: FavoritesViewModel = viewModel(
                factory = SimpleVmFactory { FavoritesViewModel(authRepo, favoritesRepo) }
            )

            AppNav(
                weatherVm = weatherVm,
                favoritesVm = favoritesVm
            )
        }
    }
}