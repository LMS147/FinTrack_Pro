package com.example.fintrackpro.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.Budget
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val monthYear: String = getCurrentMonthYear()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Get date range for spending
            val (startDate, endDate) = getMonthDateRange()
            
            // Observe budget, spending, and user changes
            combine(
                budgetRepository.observeBudgetForMonth(userId, monthYear),
                expenseRepository.observeTotalExpensesForPeriod(userId, startDate, endDate),
                authRepository.getUserFlow(userId)
            ) { budget, totalSpent, user ->
                BudgetUiState(
                    budget = budget,
                    totalSpent = totalSpent,
                    monthYear = monthYear,
                    currency = user?.defaultCurrency ?: "ZAR",
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun saveBudget(min: Double, max: Double) {
        viewModelScope.launch {
            budgetRepository.upsertBudget(userId, monthYear, min, max)
        }
    }

    private fun getCurrentMonthYear(): String {
        val cal = Calendar.getInstance()
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
    }

    private fun getMonthDateRange(): Pair<Date, Date> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.time

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.time
        return Pair(start, end)
    }

    data class BudgetUiState(
        val budget: Budget? = null,
        val totalSpent: Double = 0.0,
        val monthYear: String = "",
        val currency: String = "ZAR",
        val isLoading: Boolean = true
    )
}