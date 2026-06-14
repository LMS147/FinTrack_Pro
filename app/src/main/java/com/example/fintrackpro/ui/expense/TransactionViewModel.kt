package com.example.fintrackpro.ui.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.AccountEntity
import com.example.fintrackpro.data.entity.CategoryEntity
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.data.Repository.AccountRepository
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.data.Repository.CategoryRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FinTrackApp
    private val transactionRepository: TransactionRepository = app.transactionRepository
    private val accountRepository: AccountRepository = app.accountRepository
    private val categoryRepository: CategoryRepository = app.categoryRepository
    private val budgetRepository: BudgetRepository = app.budgetRepository
    private val sessionManager = SessionManager(application)

    private val userId: String = sessionManager.getUserId() ?: ""

    val transactions: LiveData<List<TransactionEntity>> = transactionRepository.getTransactionsByUser(userId)
    val accounts: LiveData<List<AccountEntity>> = accountRepository.getAccountsByUser(userId)
    val expenseCategories: LiveData<List<CategoryEntity>> = categoryRepository.getCategoriesByType(userId, "EXPENSE")
    val incomeCategories: LiveData<List<CategoryEntity>> = categoryRepository.getCategoriesByType(userId, "INCOME")

    private val _saveState = MutableLiveData<SaveState>()
    val saveState: LiveData<SaveState> = _saveState

    fun addTransaction(
        accountId: String,
        categoryId: String,
        type: String,
        amount: Double,
        title: String,
        description: String?,
        date: Long,
        receiptImagePath: String?
    ) {
        _saveState.value = SaveState.Loading
        viewModelScope.launch {
            try {
                val transaction = TransactionEntity(
                    userId = userId,
                    accountId = accountId,
                    categoryId = categoryId,
                    type = type,
                    amount = amount,
                    title = title,
                    description = description,
                    date = date,
                    receiptImagePath = receiptImagePath
                )
                transactionRepository.insertTransaction(transaction)

                // Update account balance
                val account = accountRepository.getAccountById(accountId)
                if (account != null) {
                    val newBalance = if (type == "INCOME") {
                        account.balance + amount
                    } else {
                        account.balance - amount
                    }
                    accountRepository.updateAccountBalance(accountId, newBalance)
                }

                // Update budget spent if expense
                if (type == "EXPENSE") {
                    updateBudgetSpent(categoryId, amount)
                }

                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to add transaction")
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)

                // Revert account balance
                val account = accountRepository.getAccountById(transaction.accountId)
                if (account != null) {
                    val newBalance = if (transaction.type == "INCOME") {
                        account.balance - transaction.amount
                    } else {
                        account.balance + transaction.amount
                    }
                    accountRepository.updateAccountBalance(transaction.accountId, newBalance)
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to delete transaction")
            }
        }
    }

    private suspend fun updateBudgetSpent(categoryId: String, amount: Double) {
        val budget = budgetRepository.getActiveBudgetForCategory(
            userId,
            categoryId,
            System.currentTimeMillis()
        )
        if (budget != null) {
            val newSpent = budget.spent + amount
            budgetRepository.updateBudgetSpent(budget.budgetId, newSpent)
        }
    }
}

sealed class SaveState {
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}
