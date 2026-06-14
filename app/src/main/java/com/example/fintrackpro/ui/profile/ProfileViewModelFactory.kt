package com.example.fintrackpro.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.AuthRepository

class ProfileViewModelFactory(
    private val context: Context,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val database = FinTrackDatabase.getDatabase(context)
            val authRepo = AuthRepository(database.userDao())
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(authRepo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
