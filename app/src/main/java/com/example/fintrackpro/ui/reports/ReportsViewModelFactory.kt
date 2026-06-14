package com.example.fintrackpro.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.TransactionRepository

class ReportsViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val budgetRepository: BudgetRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(transactionRepository, userRepository, budgetRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
