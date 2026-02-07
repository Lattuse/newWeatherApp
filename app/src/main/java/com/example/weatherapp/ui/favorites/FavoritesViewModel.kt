package com.example.weatherapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.weatherapp.data.repository.AuthRepository
import com.example.weatherapp.data.repository.FavoritesRepository
import android.util.Log
import kotlinx.coroutines.withTimeout

class FavoritesViewModel(
    private val authRepo: AuthRepository,
    private val favoritesRepo: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState(isLoading = true))
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    private var observingJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            try {
                val uid = authRepo.ensureSignedIn()
                _state.update { it.copy(uid = uid, isLoading = false) }
                observe(uid)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Auth failed: ${e.message}") }
            }
        }
    }

    private fun observe(uid: String) {
        observingJob?.cancel()
        observingJob = viewModelScope.launch {
            favoritesRepo.observeFavorites(uid)
                .catch { e -> _state.update { it.copy(error = "DB listener error: ${e.message}") } }
                .collect { list ->
                    _state.update { it.copy(favorites = list, error = null) }
                }
        }
    }

    suspend fun addAndReturnSuccess(city: String, note: String): Boolean {
        Log.d("FAV_ADD", "Clicked Save. city='$city' note='$note' uid=${state.value.uid}")

        val uid = state.value.uid
        if (uid == null) {
            _state.update { it.copy(error = "Not signed in yet (uid is null). Wait 1-2 seconds and try again.") }
            Log.e("FAV_ADD", "uid is null -> cannot write")
            return false
        }

        return try {
            withTimeout(8000) {
                favoritesRepo.addFavorite(uid, city, note)
            }
            Log.d("FAV_ADD", "addFavorite SUCCESS")
            true
        } catch (e: Exception) {
            Log.e("FAV_ADD", "addFavorite FAILED", e)
            val msg = when (e.message) {
                "EMPTY_CITY" -> "City name is empty."
                else -> "Add failed: ${e.message ?: e.javaClass.simpleName}"
            }
            _state.update { it.copy(error = msg) }
            false
        }
    }

    fun editNote(id: String, note: String) {
        val uid = state.value.uid ?: return
        viewModelScope.launch {
            try {
                favoritesRepo.updateNote(uid, id, note)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Update failed: ${e.message}") }
            }
        }
    }

    fun delete(id: String) {
        val uid = state.value.uid ?: return
        viewModelScope.launch {
            try {
                favoritesRepo.deleteFavorite(uid, id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Delete failed: ${e.message}") }
            }
        }
    }
}