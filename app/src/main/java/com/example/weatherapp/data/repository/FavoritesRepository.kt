package com.example.weatherapp.data.repository

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.weatherapp.data.firebase.FavoriteCityDto
import com.example.weatherapp.domain.model.FavoriteCity
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritesRepository {

    private val db: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://weather-f05cd-default-rtdb.europe-west1.firebasedatabase.app/")

    private fun favoritesRef(uid: String): DatabaseReference =
        db.getReference("users").child(uid).child("favorites")

    fun observeFavorites(uid: String): Flow<List<FavoriteCity>> = callbackFlow {
        val ref = favoritesRef(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    val dto = child.getValue(FavoriteCityDto::class.java) ?: return@mapNotNull null
                    FavoriteCity(
                        id = dto.id.orEmpty(),
                        cityName = dto.cityName.orEmpty(),
                        note = dto.note.orEmpty(),
                        createdAt = dto.createdAt ?: 0L,
                        createdBy = dto.createdBy.orEmpty()
                    )
                }.sortedByDescending { it.createdAt }

                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun addFavorite(uid: String, cityName: String, note: String) {
        val trimmed = cityName.trim()
        require(trimmed.isNotBlank()) { "EMPTY_CITY" }

        val ref = favoritesRef(uid)
        val id = ref.push().key ?: error("Failed to generate id")

        val item = FavoriteCity(
            id = id,
            cityName = trimmed,
            note = note.trim(),
            createdAt = System.currentTimeMillis(),
            createdBy = uid
        )

        ref.child(id).setValue(item).await()
    }

    suspend fun updateNote(uid: String, id: String, newNote: String) {
        favoritesRef(uid).child(id).updateChildren(mapOf("note" to newNote.trim())).await()
    }

    suspend fun deleteFavorite(uid: String, id: String) {
        favoritesRef(uid).child(id).removeValue().await()
    }
}