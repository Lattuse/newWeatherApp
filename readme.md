# Weather App (Android)

## Overview
This project is a simple **Weather Application for Android**, developed as part of **Assignment 7 – Data & Networking (API Integration)**.  
The app allows users to search for a city and view current weather information and a short forecast, fetched from a public REST API.  
It also supports **offline mode** by displaying cached data when the network is unavailable.

The application is written in **Kotlin**, uses **Jetpack Compose** for UI, and follows **MVVM architecture** with a repository pattern.

---

## Features
- Search weather by city name
- Display current weather:
    - City name
    - Temperature
    - Weather condition
    - Min / Max temperature
    - Humidity
    - Wind speed
    - Last update time
- Weather forecast for **at least 3 days**
- Offline mode with cached data and clear **OFFLINE** label
- Search history for recently searched cities
- Settings screen to switch units:
    - Celsius (°C)
    - Fahrenheit (°F)
- Graceful error handling (empty input, city not found, no internet, API errors)

---

## Used API
**Open-Meteo API** (no API key required)

### 1. Geocoding API
Used to convert a city name into geographic coordinates.

Endpoint: https://geocoding-api.open-meteo.com/v1/search

Parameters:
- `name` – city name
- `count` – number of results
- `language` – response language
- `format` – response format (json)

---

### 2. Forecast API
Used to fetch current weather and daily forecast.

Endpoint: https://api.open-meteo.com/v1/forecast

Parameters:
- `latitude`
- `longitude`
- `timezone=auto`
- `temperature_unit` (celsius or fahrenheit)
- `current` – temperature, humidity, wind speed, weather code
- `daily` – min/max temperature, weather code

---

## Architecture
The app follows **MVVM (Model–View–ViewModel)** with a **Repository** layer.

### Layers:
- **UI layer**  
  Jetpack Compose screens (Search, Weather, Settings)

- **ViewModel layer**  
  Manages UI state using `StateFlow` and handles user actions

- **Repository layer**  
  Single source of truth for data  
  Decides whether to load data from network or local cache

- **Data layer**
    - Remote: Retrofit + OkHttp
    - Local: DataStore (Preferences)

---

## Networking
- **Retrofit** – HTTP client
- **OkHttp** – networking and logging
- **kotlinx.serialization** – JSON parsing

All network requests are executed using Kotlin Coroutines.

---

## Local Caching (Offline Support)
- The **last successful weather response** is serialized to JSON and saved locally using **DataStore Preferences**
- Cached data includes:
    - Weather response
    - City name
    - Timestamp
- When the network is unavailable or a request fails:
    - The app attempts to load cached data
    - Cached data is displayed with an **OFFLINE (cached)** label

---

## Error Handling
The app handles the following cases gracefully:
- Empty city input
- City not found
- No internet connection
- Network timeout
- API or parsing errors

If cached data is available, it is shown instead of failing completely.

---

## UI & UX
- Built with **Jetpack Compose**
- Clear layout and spacing
- Loading indicators during network requests
- Readable text and contrast
- Simple navigation between screens
- Accessible and stable UI

---

## How to Run the App
1. Open the project in **Android Studio**
2. Sync Gradle dependencies
3. Run the app on an emulator or physical device
4. Enter a city name and press **Search**
5. View current weather and forecast
6. Turn off internet to test offline mode

---

## Project Requirements Checklist
- [x] Public REST API integration
- [x] Correct HTTP requests and JSON parsing
- [x] MVVM + Repository architecture
- [x] Jetpack Compose UI
- [x] 3-day weather forecast
- [x] Offline cache with DataStore
- [x] Error handling
- [x] Settings (Celsius / Fahrenheit)
- [x] Search history
- [x] Clean and readable code

---

## Known Limitations
- Weather condition descriptions are simplified based on weather codes
- Forecast is limited to the first 3 daily entries
- City name for cached data is simplified

---

## Attribution
Weather data provided by **Open-Meteo**  
https://open-meteo.com/