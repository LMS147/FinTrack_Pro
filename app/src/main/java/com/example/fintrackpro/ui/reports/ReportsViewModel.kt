package com.example.fintrackpro.ui.reports

import android.app.Application
import androidx.lifecycle.*
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
    
    private val _userId = MutableLiveData<String>().apply {
        value = sessionManager.getUserId() ?: ""
    }

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _dateRange = MutableStateFlow(FormatUtils.getMonthStartEnd())

    init {
        observeReport()
    }

    private fun observeReport() {
        viewModelScope.launch {
            combine(
                _userId.asFlow(),
                _dateRange
            ) { id, range -> Pair(id, range) }.flatMapLatest { (id, range) ->
                val (start, end) = range
                combine(
                    transactionRepository.getTotalIncome(id, start, end).asFlow(),
                    transactionRepository.getTotalExpenses(id, start, end).asFlow(),
                    transactionRepository.getCategorySpendingTotals(id, start, end).asFlow(),
                    budgetRepository.getBudgetsByUser(id).asFlow(),
                    userRepository.getUserByIdLive(id).asFlow()
                ) { income, expenses, categoryTotals, budgets, user ->
                    ReportsUiState(
                        totalIncome = income ?: 0.0,
                        totalSpent = expenses ?: 0.0,
                        categoryTotals = categoryTotals ?: emptyList(),
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

    fun setDateRange(start: Long, end: Long) {
        _dateRange.value = Pair(start, end)
    }

    data class ReportsUiState(
        val totalIncome: Double = 0.0,
        val totalSpent: Double = 0.0,
        val categoryTotals: List<CategorySpendingSummary> = emptyList(),
        val startDate: Date? = null,
        val endDate: Date? = null,
        val currency: String = "ZAR",
        val budget: BudgetEntity? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )
}
