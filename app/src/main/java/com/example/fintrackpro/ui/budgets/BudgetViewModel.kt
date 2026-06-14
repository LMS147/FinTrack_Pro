package com.example.fintrackpro.ui.budgets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.BudgetEntity
import com.example.fintrackpro.data.entity.CategoryEntity
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.CategoryRepository
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetRepository: BudgetRepository = (application as FinTrackApp).budgetRepository
    private val categoryRepository: CategoryRepository = (application as FinTrackApp).categoryRepository
    private val sessionManager = SessionManager(application)

    private val userId: String = sessionManager.getUserId() ?: ""

    val budgets: LiveData<List<BudgetEntity>> = budgetRepository.getBudgetsByUser(userId)
    val expenseCategories: LiveData<List<CategoryEntity>> = categoryRepository.getCategoriesByType(userId, "EXPENSE")

    private val _saveState = MutableLiveData<SaveState>()
    val saveState: LiveData<SaveState> = _saveState

    fun addBudget(
        categoryId: String,
        categoryName: String,
        amount: Double,
        period: String,
        startDate: Long,
        endDate: Long,
        alertThreshold: Int
    ) {
        _saveState.value = SaveState.Loading
        viewModelScope.launch {
            try {
                val budget = BudgetEntity(
                    userId = userId,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    amount = amount,
                    period = period,
                    startDate = startDate,
                    endDate = endDate,
                    alertThreshold = alertThreshold
                )
                budgetRepository.insertBudget(budget)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to add budget")
            }
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budget)
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to delete budget")
            }
        }
    }
}

sealed class SaveState {
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}
