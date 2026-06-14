package com.example.fintrackpro.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _dateRange = MutableStateFlow(FormatUtils.getMonthStartEnd())

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            _dateRange.flatMapLatest { (start, end) ->
                combine(
                    transactionRepository.getTransactionsByDateRange(userId, start, end).asFlow(),
                    userRepository.getUserByIdLive(userId).asFlow()
                ) { transactions, user ->
                    ExpenseUiState(
                        expenses = transactions,
                        currency = user?.defaultCurrency ?: "ZAR",
                        isLoading = false
                    )
                }
            }.catch { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun deleteExpense(expense: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(expense)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete: ${e.message}")
            }
        }
    }

    data class ExpenseUiState(
        val expenses: List<TransactionEntity> = emptyList(),
        val currency: String = "ZAR",
        val isLoading: Boolean = true,
        val error: String? = null
    )
}
