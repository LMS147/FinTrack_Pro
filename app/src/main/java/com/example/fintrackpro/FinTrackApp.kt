package com.example.fintrackpro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.fintrackpro.data.FinTrackDatabase
import com.example.fintrackpro.data.Repository.*

class FinTrackApp : Application() {

    val database by lazy { FinTrackDatabase.getDatabase(this) }
    
    val userRepository by lazy { UserRepository(database.userDao()) }
    val accountRepository by lazy { AccountRepository(database.accountDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }
    val sharedWalletRepository by lazy { SharedWalletRepository(database.sharedWalletDao()) }
    val achievementRepository by lazy { AchievementRepository(database.achievementDao()) }
    val savingsGoalRepository by lazy { SavingsGoalRepository(database.savingsGoalDao()) }
    val currencyRepository by lazy { CurrencyRepository(database.currencyRateDao()) }
    val achievementManager by lazy { com.example.fintrackpro.utils.AchievementManager(achievementRepository, transactionRepository, accountRepository) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val budgetChannel = NotificationChannel(
                BUDGET_CHANNEL_ID,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for budget limits and alerts"
            }

            val transactionChannel = NotificationChannel(
                TRANSACTION_CHANNEL_ID,
                "Transaction Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for transaction updates"
            }

            val achievementChannel = NotificationChannel(
                ACHIEVEMENT_CHANNEL_ID,
                "Achievements",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for unlocked achievements"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(budgetChannel)
            notificationManager.createNotificationChannel(transactionChannel)
            notificationManager.createNotificationChannel(achievementChannel)
        }
    }

    companion object {
        const val BUDGET_CHANNEL_ID = "budget_alerts"
        const val TRANSACTION_CHANNEL_ID = "transaction_updates"
        const val ACHIEVEMENT_CHANNEL_ID = "achievements"
    }
}
