package com.example.fintrackpro.ui.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.BudgetEntity
import com.example.fintrackpro.data.entity.CategorySpendingSummary
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FinTrackApp
    private val transactionRepository = app.transactionRepository
    private val userRepository = app.userRepository
    private val budgetRepository = app.budgetRepository
    private val sessionManager = SessionManager(application)
    private val userId: String = sessionManager.getUserId() ?: ""

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
