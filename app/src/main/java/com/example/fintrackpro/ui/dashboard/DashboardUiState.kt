package com.example.fintrackpro.ui.dashboard

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val recentTransactions: List<com.example.fintrackpro.data.entity.Transaction> = emptyList(),
    val currency: String = "ZAR",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
