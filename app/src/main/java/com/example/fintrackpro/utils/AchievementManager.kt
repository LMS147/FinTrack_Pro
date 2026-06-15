package com.example.fintrackpro.utils

import com.example.fintrackpro.data.Repository.AccountRepository
import com.example.fintrackpro.data.Repository.AchievementRepository
import com.example.fintrackpro.data.Repository.TransactionRepository
import com.example.fintrackpro.data.entity.AchievementEntity
import java.util.*

class AchievementManager(
    private val achievementRepository: AchievementRepository,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    suspend fun checkTransactionAchievements(userId: String, amount: Double, type: String) {
        val achievements = achievementRepository.getAchievementsByUserSync(userId)
        
        achievements.forEach { achievement: AchievementEntity ->
            if (achievement.isUnlocked) return@forEach

            when (achievement.title) {
                "First Steps" -> {
                    updateProgress(achievement, 1)
                }
                "Saver" -> {
                    if (type == "INCOME") {
                        updateProgress(achievement, achievement.progress + amount.toInt())
                    }
                }
                "Consistent Tracker" -> {
                    checkConsistency(userId, achievement)
                }
                "Wealth Builder" -> {
                    val totalBalance = accountRepository.getTotalBalanceSync(userId) ?: 0.0
                    updateProgress(achievement, totalBalance.toInt())
                }
                "Expense Critic" -> {
                    val count = transactionRepository.getExpenseCountSync(userId)
                    updateProgress(achievement, count)
                }
            }
        }
    }

    private suspend fun checkConsistency(userId: String, achievement: AchievementEntity) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -14)
        val transactions = transactionRepository.getRecentTransactionsSync(userId, calendar.timeInMillis)
        
        if (transactions.isEmpty()) return

        val dates = transactions.map { 
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }.distinct().sortedDescending()

        updateProgress(achievement, dates.size)
    }

    private suspend fun updateProgress(achievement: AchievementEntity, newProgress: Int) {
        var progress = newProgress
        var isUnlocked = achievement.isUnlocked
        var unlockedAt = achievement.unlockedAt

        if (progress >= achievement.target && !isUnlocked) {
            progress = achievement.target
            isUnlocked = true
            unlockedAt = System.currentTimeMillis()
        }

        achievementRepository.updateAchievement(achievement.copy(
            progress = progress,
            isUnlocked = isUnlocked,
            unlockedAt = unlockedAt
        ))
    }
}
