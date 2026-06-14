package com.example.fintrackpro.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.Transaction
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // Filter parameters
    private val _startDate = MutableStateFlow(getDefaultStartDate())
    private val _endDate = MutableStateFlow(getDefaultEndDate())

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        // Combine start/end dates, expenses flow, and user flow
        viewModelScope.launch {
            combine(
                combine(_startDate, _endDate) { start, end -> Pair(start, end) }
                    .flatMapLatest { (start, end) ->
                        repository.getExpensesForPeriod(userId, start, end)
                    },
                authRepository.getUserFlow(userId)
            ) { expenses, user ->
                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    currency = user?.defaultCurrency ?: "ZAR",
                    isLoading = false,
                    error = null
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }.collect()
        }
    }

    fun setDateFilter(startDate: Date, endDate: Date) {
        _startDate.value = startDate
        _endDate.value = endDate
    }

    fun refresh() {
        // Only auto-update if it was set to default (today)
        // For simplicity, always update the end date to catch new entries if it's near 'now'
        _endDate.value = getDefaultEndDate()
    }

    fun deleteExpense(expense: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete: ${e.message}")
            }
        }
    }

    private fun getDefaultStartDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun getDefaultEndDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    data class ExpenseUiState(
        val expenses: List<Transaction> = emptyList(),
        val currency: String = "ZAR",
        val isLoading: Boolean = true,
        val error: String? = null
    )
}