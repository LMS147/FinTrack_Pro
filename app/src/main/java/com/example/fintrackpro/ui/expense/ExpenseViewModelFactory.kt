package com.example.fintrackpro.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.TransactionRepository

class ExpenseViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(transactionRepository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
