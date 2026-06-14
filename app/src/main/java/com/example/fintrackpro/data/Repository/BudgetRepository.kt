package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.BudgetDao
import com.example.fintrackpro.data.entity.BudgetEntity

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getBudgetsByUser(userId: String): LiveData<List<BudgetEntity>> {
        return budgetDao.getBudgetsByUser(userId)
    }

    suspend fun insertBudget(budget: BudgetEntity) = budgetDao.insertBudget(budget)

    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.updateBudget(budget)

    suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.deleteBudget(budget)

    suspend fun getBudgetById(budgetId: String) = budgetDao.getBudgetById(budgetId)

    fun getBudgetByIdLive(budgetId: String): LiveData<BudgetEntity?> =
        budgetDao.getBudgetByIdLive(budgetId)

    suspend fun getActiveBudgetForCategory(
        userId: String,
        categoryId: String,
        currentTime: Long
    ) = budgetDao.getActiveBudgetForCategory(userId, categoryId, currentTime)

    suspend fun updateBudgetSpent(budgetId: String, spent: Double) {
        budgetDao.updateBudgetSpent(budgetId, spent, System.currentTimeMillis())
    }
}
