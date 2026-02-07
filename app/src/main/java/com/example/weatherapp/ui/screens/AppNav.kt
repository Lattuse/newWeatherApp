package com.example.weatherapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.favorites.FavoritesViewModel

@Composable
fun AppNav(
    weatherVm: WeatherViewModel,
    favoritesVm: FavoritesViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                vm = weatherVm,
                onGoWeather = { navController.navigate("weather") },
                onGoSettings = { navController.navigate("settings") },
                onGoFavorites = { navController.navigate("favorites") }
            )
        }
        composable("weather") {
            WeatherScreen(
                vm = weatherVm,
                onBack = { navController.popBackStack() },
                onGoSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                vm = weatherVm,
                onBack = { navController.popBackStack() }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                vm = favoritesVm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
