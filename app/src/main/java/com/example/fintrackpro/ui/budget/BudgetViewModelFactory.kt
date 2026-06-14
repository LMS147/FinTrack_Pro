package com.example.fintrackpro.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.TransactionRepository

class BudgetViewModelFactory(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetRepository, transactionRepository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
