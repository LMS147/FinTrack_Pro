package com.example.fintrackpro.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fintrackpro.data.entity.SavingsGoalEntity

@Dao
interface SavingsGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)

    @Delete
    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity)

    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY isCompleted ASC, targetDate ASC")
    fun getSavingsGoalsByUser(userId: String): LiveData<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE goalId = :goalId")
    suspend fun getSavingsGoalById(goalId: String): SavingsGoalEntity?

    @Query("SELECT * FROM savings_goals WHERE goalId = :goalId")
    fun getSavingsGoalByIdLive(goalId: String): LiveData<SavingsGoalEntity?>
}
