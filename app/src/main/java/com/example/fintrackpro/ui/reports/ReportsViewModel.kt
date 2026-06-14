package com.example.fintrackpro.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.BudgetEntity
import com.example.fintrackpro.data.entity.CategorySpendingSummary
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ReportsViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val budgetRepository: BudgetRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _dateRange = MutableStateFlow(FormatUtils.getMonthStartEnd())

    init {
        observeReport()
    }

    private fun observeReport() {
        viewModelScope.launch {
            _dateRange.flatMapLatest { (start, end) ->
                combine(
                    transactionRepository.getTotalExpenses(userId, start, end).asFlow(),
                    budgetRepository.getBudgetsByUser(userId).asFlow(),
                    userRepository.getUserByIdLive(userId).asFlow()
                ) { totals, budgets, user ->
                    ReportsUiState(
                        totalSpent = totals ?: 0.0,
                        startDate = Date(start),
                        endDate = Date(end),
                        currency = user?.defaultCurrency ?: "ZAR",
                        budget = budgets.firstOrNull(),
                        isLoading = false
                    )
                }
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    data class ReportsUiState(
        val categoryTotals: List<CategorySpendingSummary> = emptyList(),
        val totalSpent: Double = 0.0,
        val startDate: Date? = null,
        val endDate: Date? = null,
        val currency: String = "ZAR",
        val budget: BudgetEntity? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
