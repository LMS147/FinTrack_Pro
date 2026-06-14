package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.BudgetDao
import com.example.fintrackpro.data.entity.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {


    suspend fun saveBudget(budget: Budget): Long {
        return budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    suspend fun getBudgetForMonth(userId: Int, monthYear: String): Budget? {
        return budgetDao.getBudgetForMonth(userId, monthYear)
    }

    fun observeBudgetForMonth(userId: Int, monthYear: String): Flow<Budget?> {
        return budgetDao.getBudgetForMonthFlow(userId, monthYear)
    }

    fun getAllBudgetsForUser(userId: Int): Flow<List<Budget>> {
        return budgetDao.getAllBudgetsForUser(userId)
    }

    suspend fun upsertBudget(userId: Int, monthYear: String, minGoal: Double?, maxGoal: Double) {
        val existing = getBudgetForMonth(userId, monthYear)
        if (existing == null) {
            val newBudget = Budget(
                userId = userId,
                monthYear = monthYear,
                minSpendingGoal = minGoal,
                maxSpendingGoal = maxGoal
            )
            saveBudget(newBudget)
        } else {
            val updated = existing.copy(
                minSpendingGoal = minGoal,
                maxSpendingGoal = maxGoal,
                updatedAt = System.currentTimeMillis()
            )
            updateBudget(updated)
        }
    }
}