package com.example.fintrackpro.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fintrackpro.data.entity.AchievementEntity

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY isUnlocked DESC, points DESC")
    fun getAchievementsByUser(userId: String): LiveData<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE userId = :userId AND isUnlocked = 1")
    fun getUnlockedAchievements(userId: String): LiveData<List<AchievementEntity>>

    @Query("SELECT SUM(points) FROM achievements WHERE userId = :userId AND isUnlocked = 1")
    fun getTotalPoints(userId: String): LiveData<Int?>
}
