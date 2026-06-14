package com.example.fintrackpro.ui.budget

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository

class BudgetViewModelFactory(
    private val context: Context,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            val database = FinTrackDatabase.getDatabase(context)
            val budgetRepo = BudgetRepository(database.budgetDao())
            val expenseRepo = ExpenseRepository(database.expenseDao())
            val authRepo = AuthRepository(database.userDao())
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetRepo, expenseRepo, authRepo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}