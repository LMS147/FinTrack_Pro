package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.AchievementDao
import com.example.fintrackpro.data.entity.AchievementEntity

class AchievementRepository(private val achievementDao: AchievementDao) {

    fun getAchievementsByUser(userId: String): LiveData<List<AchievementEntity>> {
        return achievementDao.getAchievementsByUser(userId)
    }

    suspend fun getAchievementsByUserSync(userId: String): List<AchievementEntity> {
        return achievementDao.getAchievementsByUserSync(userId)
    }

    fun getUnlockedAchievements(userId: String): LiveData<List<AchievementEntity>> {
        return achievementDao.getUnlockedAchievements(userId)
    }

    fun getTotalPoints(userId: String): LiveData<Int?> {
        return achievementDao.getTotalPoints(userId)
    }

    suspend fun getAchievementCount(userId: String): Int {
        return achievementDao.getAchievementCount(userId)
    }

    suspend fun insertAchievement(achievement: AchievementEntity) =
        achievementDao.insertAchievement(achievement)

    suspend fun insertAchievements(achievements: List<AchievementEntity>) =
        achievementDao.insertAchievements(achievements)

    suspend fun updateAchievement(achievement: AchievementEntity) =
        achievementDao.updateAchievement(achievement)

    suspend fun initializeAchievementsForUser(userId: String) {
        val defaultAchievements = listOf(
            AchievementEntity(
                userId = userId,
                title = "First Steps",
                description = "Add your first transaction",
                badgeIcon = "ic_achievement_first_steps",
                category = "MILESTONES",
                points = 10,
                target = 1
            ),
            AchievementEntity(
                userId = userId,
                title = "Saver",
                description = "Save $1000 in total",
                badgeIcon = "ic_achievement_saver",
                category = "SAVINGS",
                points = 100,
                target = 1000
            ),
            AchievementEntity(
                userId = userId,
                title = "Budget Master",
                description = "Keep all budgets within limits for a month",
                badgeIcon = "ic_achievement_budget",
                category = "BUDGETING",
                points = 200,
                target = 1
            ),
            AchievementEntity(
                userId = userId,
                title = "Consistent Tracker",
                description = "Log transactions for 7 consecutive days",
                badgeIcon = "ic_achievement_consistency",
                category = "CONSISTENCY",
                points = 50,
                target = 7
            ),
            AchievementEntity(
                userId = userId,
                title = "Wealth Builder",
                description = "Have a total balance of $5000",
                badgeIcon = "ic_achievement_wealth",
                category = "MILESTONES",
                points = 500,
                target = 5000
            ),
            AchievementEntity(
                userId = userId,
                title = "Expense Critic",
                description = "Log 50 expenses",
                badgeIcon = "ic_achievement_expense",
                category = "CONSISTENCY",
                points = 150,
                target = 50
            )
        )
        achievementDao.insertAchievements(defaultAchievements)
    }
}
