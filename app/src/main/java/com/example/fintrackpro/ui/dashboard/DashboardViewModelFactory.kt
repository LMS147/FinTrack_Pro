package com.example.fintrackpro.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.TransactionRepository

class DashboardViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(transactionRepository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
