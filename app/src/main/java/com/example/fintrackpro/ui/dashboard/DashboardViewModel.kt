package com.example.fintrackpro.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private val userId: String = sessionManager.getUserId() ?: ""

    val accounts: LiveData<List<AccountEntity>> = accountRepository.getAccountsByUser(userId)
    val totalBalance: LiveData<Double?> = accountRepository.getTotalBalance(userId)
    val recentTransactions: LiveData<List<TransactionEntity>> = transactionRepository.getRecentTransactions(userId, 10)

    private val monthRange = FormatUtils.getMonthStartEnd()
    val monthlyIncome: LiveData<Double?> = transactionRepository.getTotalIncome(userId, monthRange.first, monthRange.second)
    val monthlyExpenses: LiveData<Double?> = transactionRepository.getTotalExpenses(userId, monthRange.first, monthRange.second)

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
