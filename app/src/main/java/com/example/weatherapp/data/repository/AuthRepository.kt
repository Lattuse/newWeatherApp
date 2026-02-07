package com.example.weatherapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun ensureSignedIn(): String {
        val current = auth.currentUser
        if (current != null) return current.uid

        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: error("Anonymous auth failed")
    }

    fun currentUidOrNull(): String? = auth.currentUser?.uid
}