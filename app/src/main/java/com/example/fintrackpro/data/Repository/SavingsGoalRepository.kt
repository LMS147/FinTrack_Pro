package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.SavingsGoalDao
import com.example.fintrackpro.data.entity.SavingsGoalEntity

class SavingsGoalRepository(private val savingsGoalDao: SavingsGoalDao) {

    fun getSavingsGoalsByUser(userId: String): LiveData<List<SavingsGoalEntity>> {
        return savingsGoalDao.getSavingsGoalsByUser(userId)
    }

    suspend fun insertSavingsGoal(goal: SavingsGoalEntity) =
        savingsGoalDao.insertSavingsGoal(goal)

    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) =
        savingsGoalDao.updateSavingsGoal(goal)

    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity) =
        savingsGoalDao.deleteSavingsGoal(goal)

    suspend fun getSavingsGoalById(goalId: String) =
        savingsGoalDao.getSavingsGoalById(goalId)

    fun getSavingsGoalByIdLive(goalId: String): LiveData<SavingsGoalEntity?> =
        savingsGoalDao.getSavingsGoalByIdLive(goalId)
}
