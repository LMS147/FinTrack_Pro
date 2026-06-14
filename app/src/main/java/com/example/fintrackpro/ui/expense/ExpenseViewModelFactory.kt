package com.example.fintrackpro.ui.expense

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository

class ExpenseViewModelFactory(
    private val context: Context,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            val database = FinTrackDatabase.getDatabase(context)
            val repo = ExpenseRepository(database.expenseDao())
            val authRepo = AuthRepository(database.userDao())
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repo, authRepo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}