package com.example.fintrackpro.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.BudgetEntity
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val (startDate, endDate) = FormatUtils.getMonthStartEnd()
            
            combine(
                budgetRepository.getBudgetsByUser(userId).asFlow(),
                transactionRepository.getTotalExpenses(userId, startDate, endDate).asFlow(),
                userRepository.getUserByIdLive(userId).asFlow()
            ) { budgets, totalSpent, user ->
                BudgetUiState(
                    budget = budgets.firstOrNull(), // Simplified for now
                    totalSpent = totalSpent ?: 0.0,
                    currency = user?.defaultCurrency ?: "ZAR",
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    data class BudgetUiState(
        val budget: BudgetEntity? = null,
        val totalSpent: Double = 0.0,
        val currency: String = "ZAR",
        val isLoading: Boolean = true
    )
}
