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

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, createdAt DESC")
    fun getTransactionsByUser(userId: String): LiveData<List<TransactionEntity>>

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

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(userId: String, limit: Int): LiveData<List<TransactionEntity>>

    // ExpensePhoto management
    @Insert
    suspend fun insertPhoto(photo: com.example.fintrackpro.data.entity.ExpensePhoto): Long

    @Query("SELECT * FROM photos WHERE expenseId = :expenseId LIMIT 1")
    suspend fun getPhotoForExpense(expenseId: String): com.example.fintrackpro.data.entity.ExpensePhoto?

    @Delete
    suspend fun deletePhoto(photo: com.example.fintrackpro.data.entity.ExpensePhoto)
}
