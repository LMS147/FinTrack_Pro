package com.example.fintrackpro.ui.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.AccountEntity
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.data.Repository.AccountRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val accountRepository: AccountRepository = (application as FinTrackApp).accountRepository
    private val transactionRepository: TransactionRepository = (application as FinTrackApp).transactionRepository
    private val currencyRepository = (application as FinTrackApp).currencyRepository
    private val sessionManager = SessionManager(application)

    private val _userId = MutableLiveData<String>().apply {
        value = sessionManager.getUserId() ?: ""
    }

    val accounts: LiveData<List<AccountEntity>> = _userId.switchMap { id ->
        accountRepository.getAccountsByUser(id)
    }

    val appCurrency = MutableLiveData<String>().apply {
        value = sessionManager.getCurrency()
    }

    val totalBalance: LiveData<Double> = MediatorLiveData<Double>().apply {
        var currentAccounts: List<AccountEntity>? = null
        var currentCurrency: String = sessionManager.getCurrency()

        fun update() {
            val accs = currentAccounts ?: return
            viewModelScope.launch {
                var total = 0.0
                for (account in accs) {
                    total += currencyRepository.convertCurrency(account.balance, account.currency, currentCurrency)
                }
                value = total
            }
        }

        addSource(accounts) { 
            currentAccounts = it
            update()
        }
        addSource(appCurrency) {
            currentCurrency = it
            update()
        }
    }

    val recentTransactions: LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>> = _userId.switchMap { id ->
        transactionRepository.getRecentTransactions(id, 10)
    }

    val convertedRecentTransactions: LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>> = MediatorLiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>>().apply {
        addSource(recentTransactions) { list ->
            viewModelScope.launch {
                val targetCurrency = sessionManager.getCurrency()
                val convertedList = list.map { item ->
                    val convertedAmount = currencyRepository.convertCurrency(
                        item.transaction.amount,
                        item.account.currency,
                        targetCurrency
                    )
                    // We'll use a trick: update the amount in the copied object for display
                    item.copy(transaction = item.transaction.copy(amount = convertedAmount))
                }
                value = convertedList
            }
        }
    }

    private val monthRange = FormatUtils.getMonthStartEnd()
    
    val transactions: LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>> = _userId.switchMap { id ->
        transactionRepository.getTransactionsByUser(id)
    }

    val monthlyIncome: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(transactions) { transList ->
            viewModelScope.launch {
                val targetCurrency = sessionManager.getCurrency()
                var sum = 0.0
                for (t in transList) {
                    if (t.transaction.type == "INCOME" && t.transaction.date in monthRange.first..monthRange.second) {
                        val currency = t.account.currency
                        sum += currencyRepository.convertCurrency(t.transaction.amount, currency, targetCurrency)
                    }
                }
                value = sum
            }
        }
    }

    val monthlyExpenses: LiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(transactions) { transList ->
            viewModelScope.launch {
                val targetCurrency = sessionManager.getCurrency()
                var sum = 0.0
                for (t in transList) {
                    if (t.transaction.type == "EXPENSE" && t.transaction.date in monthRange.first..monthRange.second) {
                        val currency = t.account.currency
                        sum += currencyRepository.convertCurrency(t.transaction.amount, currency, targetCurrency)
                    }
                }
                value = sum
            }
        }
    }

    val netWorth: MediatorLiveData<Double> = MediatorLiveData<Double>().apply {
        addSource(totalBalance) { balance ->
            value = balance ?: 0.0
        }
    }

    val monthlySavings: MediatorLiveData<Double> = MediatorLiveData<Double>().apply {
        var income = 0.0
        var expenses = 0.0

        addSource(monthlyIncome) { 
            income = it ?: 0.0
            value = income - expenses
        }

        addSource(monthlyExpenses) { 
            expenses = it ?: 0.0
            value = income - expenses
        }
    }

    fun refreshCurrency() {
        appCurrency.value = sessionManager.getCurrency()
    }
}
