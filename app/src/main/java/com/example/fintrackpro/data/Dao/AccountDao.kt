package com.example.fintrackpro.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fintrackpro.data.entity.AccountEntity

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getAccountsByUser(userId: String): LiveData<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    suspend fun getAccountById(accountId: String): AccountEntity?

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccountByIdLive(accountId: String): LiveData<AccountEntity?>

    @Query("SELECT SUM(balance) FROM accounts WHERE userId = :userId AND isActive = 1")
    fun getTotalBalance(userId: String): LiveData<Double?>

    @Query("UPDATE accounts SET balance = :newBalance, updatedAt = :timestamp WHERE accountId = :accountId")
    suspend fun updateAccountBalance(accountId: String, newBalance: Double, timestamp: Long)
}