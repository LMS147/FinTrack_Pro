package com.example.fintrackpro.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fintrackpro.data.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Transaction
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, createdAt DESC")
    fun getTransactionsByUser(userId: String): LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>>

    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId")
    fun getTransactionByIdLive(transactionId: String): LiveData<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND accountId = :accountId ORDER BY date DESC")
    fun getTransactionsByAccount(userId: String, accountId: String): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(userId: String, categoryId: String): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY date DESC")
    fun getTransactionsByType(userId: String, type: String): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): LiveData<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    fun getTotalIncome(userId: String, startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpenses(userId: String, startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND categoryId = :categoryId AND type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    fun getExpensesByCategory(userId: String, categoryId: String, startDate: Long, endDate: Long): LiveData<Double?>

    @Transaction
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(userId: String, limit: Int): LiveData<List<com.example.fintrackpro.data.entity.TransactionWithCategory>>

    @Query("""
        SELECT c.name as name, SUM(t.amount) as total 
        FROM transactions t 
        INNER JOIN categories c ON t.categoryId = c.categoryId 
        WHERE t.userId = :userId AND t.type = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate 
        GROUP BY t.categoryId
    """)
    fun getCategorySpendingTotals(userId: String, startDate: Long, endDate: Long): LiveData<List<com.example.fintrackpro.data.entity.CategorySpendingSummary>>

    // ExpensePhoto management
    @Insert
    suspend fun insertPhoto(photo: com.example.fintrackpro.data.entity.ExpensePhoto): Long

    @Query("SELECT * FROM photos WHERE expenseId = :expenseId LIMIT 1")
    suspend fun getPhotoForExpense(expenseId: String): com.example.fintrackpro.data.entity.ExpensePhoto?

    @Delete
    suspend fun deletePhoto(photo: com.example.fintrackpro.data.entity.ExpensePhoto)
    @Query("SELECT * FROM transactions WHERE userId = :userId AND date >= :startDate ORDER BY date DESC")
    suspend fun getRecentTransactionsSync(userId: String, startDate: Long): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId AND type = 'EXPENSE'")
    suspend fun getExpenseCountSync(userId: String): Int
}
