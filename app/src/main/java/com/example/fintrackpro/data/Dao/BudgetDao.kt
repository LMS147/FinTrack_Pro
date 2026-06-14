package com.example.fintrackpro.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fintrackpro.data.entity.BudgetEntity

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getBudgetsByUser(userId: String): LiveData<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE budgetId = :budgetId")
    suspend fun getBudgetById(budgetId: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE budgetId = :budgetId")
    fun getBudgetByIdLive(budgetId: String): LiveData<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId AND isActive = 1 AND endDate >= :currentTime LIMIT 1")
    suspend fun getActiveBudgetForCategory(userId: String, categoryId: String, currentTime: Long): BudgetEntity?

    @Query("UPDATE budgets SET spent = :spent, updatedAt = :timestamp WHERE budgetId = :budgetId")
    suspend fun updateBudgetSpent(budgetId: String, spent: Double, timestamp: Long)
}
