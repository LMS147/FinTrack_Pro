package com.example.fintrackpro.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository

class DashboardViewModelFactory(
    private val context: Context,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            val database = FinTrackDatabase.getDatabase(context)
            val expenseRepository = ExpenseRepository(database.expenseDao())
            val authRepository = AuthRepository(database.userDao())
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(expenseRepository, authRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}