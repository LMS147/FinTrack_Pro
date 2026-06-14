package com.example.fintrackpro.data.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fintrackpro.data.entity.SharedWalletEntity

@Dao
interface SharedWalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedWallet(wallet: SharedWalletEntity): Long

    @Update
    suspend fun updateSharedWallet(wallet: SharedWalletEntity)

    @Delete
    suspend fun deleteSharedWallet(wallet: SharedWalletEntity)

    @Query("SELECT * FROM shared_wallets WHERE ownerId = :userId OR memberIds LIKE '%' || :userId || '%' ORDER BY createdAt DESC")
    fun getSharedWalletsByUser(userId: String): LiveData<List<SharedWalletEntity>>

    @Query("SELECT * FROM shared_wallets WHERE walletId = :walletId")
    suspend fun getSharedWalletById(walletId: String): SharedWalletEntity?

    @Query("SELECT * FROM shared_wallets WHERE inviteCode = :inviteCode LIMIT 1")
    suspend fun getSharedWalletByInviteCode(inviteCode: String): SharedWalletEntity?
}
