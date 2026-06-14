package com.example.fintrackpro.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.fintrackpro.data.entity.CategorySpendingSummary
import com.example.fintrackpro.data.entity.Transaction
import com.example.fintrackpro.data.entity.ExpensePhoto
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Transaction): Long

    @Update
    suspend fun updateExpense(expense: Transaction)

    @Delete
    suspend fun deleteExpense(expense: Transaction)

    // View expenses with user-selectable date range
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId AND date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, createdAt DESC
    """)
    fun getExpensesBetweenDates(
        userId: Int,
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>>

    //Total spending per category within a date range
    @Query("""
        SELECT c.name, SUM(e.amount) as total 
        FROM expenses e 
        INNER JOIN categories c ON e.categoryId = c.categoryId 
        WHERE e.userId = :userId AND e.date BETWEEN :startDate AND :endDate AND e.isIncome = 0
        GROUP BY e.categoryId 
        ORDER BY total DESC
    """)
    fun getCategorySpendingTotals(
        userId: Int,
        startDate: Date,
        endDate: Date
    ): Flow<List<CategorySpendingSummary>>

    // order
    @Query("""
        SELECT * FROM expenses 
        WHERE userId = :userId 
        ORDER BY date DESC, createdAt DESC 
        LIMIT :limit
    """)
    fun getRecentExpenses(userId: Int, limit: Int = 5): Flow<List<Transaction>>

    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId")
    suspend fun getExpenseById(expenseId: Int): Transaction?

    // For dashboard summary
    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND isIncome = 0")
    fun getTotalExpenses(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND isIncome = 1")
    fun getTotalIncome(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate AND isIncome = 0")
    suspend fun getTotalExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Double?

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date BETWEEN :startDate AND :endDate AND isIncome = 0")
    fun observeTotalExpensesForPeriod(userId: Int, startDate: Date, endDate: Date): Flow<Double?>

    // ExpensePhoto management
    @Insert
    suspend fun insertPhoto(photo: ExpensePhoto): Long

    @Query("SELECT * FROM photos WHERE expenseId = :expenseId LIMIT 1")
    suspend fun getPhotoForExpense(expenseId: Int): ExpensePhoto?

    @Delete
    suspend fun deletePhoto(photo: ExpensePhoto)

    @Query("""
        SELECT COUNT(*) FROM expenses 
        WHERE userId = :userId AND date BETWEEN :startDate AND :endDate
    """)
    suspend fun getExpensesCountBetweenDates(
        userId: Int,
        startDate: Date,
        endDate: Date
    ): Int
}