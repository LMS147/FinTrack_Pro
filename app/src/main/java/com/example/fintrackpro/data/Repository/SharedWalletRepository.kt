package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.SharedWalletDao
import com.example.fintrackpro.data.entity.SharedWalletEntity

class SharedWalletRepository(private val sharedWalletDao: SharedWalletDao) {

    fun getSharedWalletsByUser(userId: String): LiveData<List<SharedWalletEntity>> {
        return sharedWalletDao.getSharedWalletsByUser(userId)
    }

    suspend fun insertSharedWallet(wallet: SharedWalletEntity) =
        sharedWalletDao.insertSharedWallet(wallet)

    suspend fun updateSharedWallet(wallet: SharedWalletEntity) =
        sharedWalletDao.updateSharedWallet(wallet)

    suspend fun deleteSharedWallet(wallet: SharedWalletEntity) =
        sharedWalletDao.deleteSharedWallet(wallet)

    suspend fun getSharedWalletById(walletId: String) =
        sharedWalletDao.getSharedWalletById(walletId)

    suspend fun getSharedWalletByInviteCode(inviteCode: String) =
        sharedWalletDao.getSharedWalletByInviteCode(inviteCode)
}
