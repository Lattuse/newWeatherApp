package com.example.weatherapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    vm: WeatherViewModel,
    onGoWeather: () -> Unit,
    onGoSettings: () -> Unit,
    onGoFavorites: () -> Unit
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Search") },
                actions = {
                    TextButton(onClick = onGoFavorites) { Text("Favorites") }
                    TextButton(onClick = onGoSettings) { Text("Settings") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.search()
                    onGoWeather()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Search")
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (state.history.isNotEmpty()) {
                Text("History", style = MaterialTheme.typography.titleMedium)

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.history) { city ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                vm.onQueryChange(city)
                                vm.search(cityOverride = city)
                                onGoWeather()
                            }
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(city)
                                Text("Tap", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}