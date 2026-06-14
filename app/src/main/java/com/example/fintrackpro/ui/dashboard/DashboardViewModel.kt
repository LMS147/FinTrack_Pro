package com.example.fintrackpro.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val (start, end) = FormatUtils.getMonthStartEnd()
            
            combine(
                transactionRepository.getTotalIncome(userId, start, end).asFlow(),
                transactionRepository.getTotalExpenses(userId, start, end).asFlow(),
                transactionRepository.getRecentTransactions(userId, 5).asFlow(),
                userRepository.getUserByIdLive(userId).asFlow()
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
