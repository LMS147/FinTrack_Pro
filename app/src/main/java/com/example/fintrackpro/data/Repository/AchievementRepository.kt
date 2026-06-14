package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.AchievementDao
import com.example.fintrackpro.data.entity.AchievementEntity

class AchievementRepository(private val achievementDao: AchievementDao) {

    fun getAchievementsByUser(userId: String): LiveData<List<AchievementEntity>> {
        return achievementDao.getAchievementsByUser(userId)
    }

    fun getUnlockedAchievements(userId: String): LiveData<List<AchievementEntity>> {
        return achievementDao.getUnlockedAchievements(userId)
    }

    fun getTotalPoints(userId: String): LiveData<Int?> {
        return achievementDao.getTotalPoints(userId)
    }

    suspend fun insertAchievement(achievement: AchievementEntity) =
        achievementDao.insertAchievement(achievement)

    suspend fun updateAchievement(achievement: AchievementEntity) =
        achievementDao.updateAchievement(achievement)
}
