package com.example.fintrackpro.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.entity.Budget
import com.example.fintrackpro.data.entity.CategorySpendingSummary
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val budgetRepository: BudgetRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _startDate = MutableStateFlow(getDefaultStartDate())
    private val _endDate = MutableStateFlow(getDefaultEndDate())

    init {
        observeReport()
    }

    fun setDateRange(start: Date, end: Date) {
        _startDate.value = normalizeStartDate(start)
        _endDate.value = normalizeEndDate(end)
    }

    fun refresh() {
        // Trigger a refresh of the flows
        _endDate.value = normalizeEndDate(Calendar.getInstance().time)
    }

    private fun normalizeStartDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun normalizeEndDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    private fun observeReport() {
        viewModelScope.launch {
            combine(
                _startDate,
                _endDate,
                authRepository.getUserFlow(userId)
            ) { start, end, user ->
                Triple(start, end, user)
            }.flatMapLatest { (start, end, user) ->
                // Also fetch budget for the selected end date's month
                val monthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(end)
                
                combine(
                    expenseRepository.getCategorySpendingTotals(userId, start, end),
                    budgetRepository.observeBudgetForMonth(userId, monthYear)
                ) { totals, budget ->
                    ReportsUiState(
                        categoryTotals = totals,
                        totalSpent = totals.sumByDouble { summary -> summary.total },
                        startDate = start,
                        endDate = end,
                        currency = user?.defaultCurrency ?: "ZAR",
                        budget = budget,
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

    private fun getDefaultStartDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return normalizeStartDate(cal.time)
    }

    private fun getDefaultEndDate(): Date {
        return normalizeEndDate(Calendar.getInstance().time)
    }

    data class ReportsUiState(
        val categoryTotals: List<CategorySpendingSummary> = emptyList(),
        val totalSpent: Double = 0.0,
        val startDate: Date? = null,
        val endDate: Date? = null,
        val currency: String = "ZAR",
        val budget: Budget? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}