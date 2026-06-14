package com.example.fintrackpro.ui.dashboard

import com.example.fintrackpro.data.entity.TransactionEntity

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val currency: String = "USD",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
