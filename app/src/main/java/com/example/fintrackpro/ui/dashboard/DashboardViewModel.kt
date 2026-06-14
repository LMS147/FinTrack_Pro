package com.example.fintrackpro.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val userId: Int  // Obtain from logged-in user session
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Combine the flows of income, expenses, recent transactions, and user (for currency)
            combine(
                expenseRepository.getTotalIncome(userId),
                expenseRepository.getTotalExpenses(userId),
                expenseRepository.getRecentExpenses(userId),
                authRepository.getUserFlow(userId)
            ) { income, expenses, transactions, user ->
                val incomeValue = income ?: 0.0
                val expenseValue = expenses ?: 0.0
                DashboardUiState(
                    totalBalance = incomeValue - expenseValue,
                    totalIncome = incomeValue,
                    totalExpenses = expenseValue,
                    recentTransactions = transactions,
                    currency = user?.defaultCurrency ?: "ZAR",
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = DashboardUiState(
                    isLoading = false,
                    errorMessage = "Failed to load dashboard: ${e.message}"
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        _uiState.value = DashboardUiState(isLoading = true)
        loadDashboardData()
    }
}