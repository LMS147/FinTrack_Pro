package com.example.fintrackpro.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fintrackpro.data.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget)

    // Requirement 5: Retrieve min/max monthly goal for a user
    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear LIMIT 1")
    suspend fun getBudgetForMonth(userId: Int, monthYear: String): Budget?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear LIMIT 1")
    fun getBudgetForMonthFlow(userId: Int, monthYear: String): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY monthYear DESC")
    fun getAllBudgetsForUser(userId: Int): Flow<List<Budget>>
}