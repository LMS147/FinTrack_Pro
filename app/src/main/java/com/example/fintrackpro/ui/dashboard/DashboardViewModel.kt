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

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val accountRepository: AccountRepository = (application as FinTrackApp).accountRepository
    private val transactionRepository: TransactionRepository = (application as FinTrackApp).transactionRepository
    private val sessionManager = SessionManager(application)

    private val _userId = MutableLiveData<String>().apply {
        value = sessionManager.getUserId() ?: ""
    }

    val accounts: LiveData<List<AccountEntity>> = _userId.switchMap { id ->
        accountRepository.getAccountsByUser(id)
    }

    val totalBalance: LiveData<Double?> = _userId.switchMap { id ->
        accountRepository.getTotalBalance(id)
    }

    val recentTransactions: LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>> = _userId.switchMap { id ->
        transactionRepository.getRecentTransactions(id, 10)
    }

    private val monthRange = FormatUtils.getMonthStartEnd()
    
    val monthlyIncome: LiveData<Double?> = _userId.switchMap { id ->
        transactionRepository.getTotalIncome(id, monthRange.first, monthRange.second)
    }

    val monthlyExpenses: LiveData<Double?> = _userId.switchMap { id ->
        transactionRepository.getTotalExpenses(id, monthRange.first, monthRange.second)
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
}
