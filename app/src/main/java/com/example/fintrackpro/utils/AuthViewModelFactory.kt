package com.example.fintrackpro.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.ui.auth.AuthViewModel

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val database = FinTrackDatabase.getDatabase(context)
            val repository = AuthRepository(database.userDao())
            val sessionManager = SessionManager(context)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}