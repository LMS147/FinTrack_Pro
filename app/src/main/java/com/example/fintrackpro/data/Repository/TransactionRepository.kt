package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.TransactionDao
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.data.entity.ExpensePhoto
import com.example.fintrackpro.data.entity.TransactionWithCategory

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getTransactionsByUser(userId: String): LiveData<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsByUser(userId)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun getTransactionById(transactionId: String) =
        transactionDao.getTransactionById(transactionId)

    fun getTransactionByIdLive(transactionId: String): LiveData<TransactionEntity?> =
        transactionDao.getTransactionByIdLive(transactionId)

    fun getTransactionsByAccount(userId: String, accountId: String): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByAccount(userId, accountId)
    }

    fun getTransactionsByCategory(userId: String, categoryId: String): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategory(userId, categoryId)
    }

    fun getTransactionsByType(userId: String, type: String): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(userId, type)
    }

    fun getTransactionsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): LiveData<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRange(userId, startDate, endDate)
    }

    fun getTotalIncome(userId: String, startDate: Long, endDate: Long): LiveData<Double?> {
        return transactionDao.getTotalIncome(userId, startDate, endDate)
    }

    fun getTotalExpenses(userId: String, startDate: Long, endDate: Long): LiveData<Double?> {
        return transactionDao.getTotalExpenses(userId, startDate, endDate)
    }

    fun getExpensesByCategory(
        userId: String,
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): LiveData<Double?> {
        return transactionDao.getExpensesByCategory(userId, categoryId, startDate, endDate)
    }

    fun getRecentTransactions(userId: String, limit: Int = 10): LiveData<List<TransactionWithCategory>> {
        return transactionDao.getRecentTransactions(userId, limit)
    }

    suspend fun getRecentTransactionsSync(userId: String, startDate: Long): List<TransactionEntity> {
        return transactionDao.getRecentTransactionsSync(userId, startDate)
    }

    suspend fun getExpenseCountSync(userId: String): Int {
        return transactionDao.getExpenseCountSync(userId)
    }

    fun getCategorySpendingTotals(userId: String, startDate: Long, endDate: Long) =
        transactionDao.getCategorySpendingTotals(userId, startDate, endDate)

    // Photo support
    suspend fun insertPhoto(photo: ExpensePhoto) = transactionDao.insertPhoto(photo)
    suspend fun getPhotoForExpense(expenseId: String) = transactionDao.getPhotoForExpense(expenseId)
    suspend fun deletePhoto(photo: ExpensePhoto) = transactionDao.deletePhoto(photo)
}
