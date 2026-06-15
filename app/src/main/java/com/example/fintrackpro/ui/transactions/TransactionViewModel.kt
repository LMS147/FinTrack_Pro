package com.example.fintrackpro.ui.transactions

import android.app.Application
import androidx.lifecycle.*
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
    private val currencyRepository = app.currencyRepository
    private val achievementManager = app.achievementManager
    private val sessionManager = SessionManager(application)

    private val _userId = MutableLiveData<String>().apply {
        value = sessionManager.getUserId() ?: ""
    }

    val transactions: LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>> = _userId.switchMap { id ->
        transactionRepository.getTransactionsByUser(id)
    }

    val convertedTransactions: LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>> = MediatorLiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>>().apply {
        addSource(transactions) { list ->
            viewModelScope.launch {
                val targetCurrency = sessionManager.getCurrency()
                val convertedList = list.map { item ->
                    val convertedAmount = currencyRepository.convertCurrency(
                        item.transaction.amount,
                        item.account.currency,
                        targetCurrency
                    )
                    item.copy(transaction = item.transaction.copy(amount = convertedAmount))
                }
                value = convertedList
            }
        }
    }

    val accounts: LiveData<List<AccountEntity>> = _userId.switchMap { id ->
        accountRepository.getAccountsByUser(id)
    }

    val expenseCategories: LiveData<List<CategoryEntity>> = _userId.switchMap { id ->
        categoryRepository.getCategoriesByType(id, "EXPENSE")
    }

    val incomeCategories: LiveData<List<CategoryEntity>> = _userId.switchMap { id ->
        categoryRepository.getCategoriesByType(id, "INCOME")
    }

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
        val currentUserId = _userId.value ?: return
        _saveState.value = SaveState.Loading
        viewModelScope.launch {
            try {
                val transaction = TransactionEntity(
                    userId = currentUserId,
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

                val account = accountRepository.getAccountById(accountId)
                if (account != null) {
                    val newBalance = if (type == "INCOME") {
                        account.balance + amount
                    } else {
                        account.balance - amount
                    }
                    accountRepository.updateAccountBalance(accountId, newBalance)
                }

                if (type == "EXPENSE") {
                    updateBudgetSpent(currentUserId, categoryId, amount)
                }

                achievementManager.checkTransactionAchievements(currentUserId, amount, type)

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

    private suspend fun updateBudgetSpent(userId: String, categoryId: String, amount: Double) {
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
