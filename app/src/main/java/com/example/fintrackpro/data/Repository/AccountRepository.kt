package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.AccountDao
import com.example.fintrackpro.data.entity.AccountEntity

class AccountRepository(private val accountDao: AccountDao) {

    fun getAccountsByUser(userId: String): LiveData<List<AccountEntity>> {
        return accountDao.getAccountsByUser(userId)
    }

    suspend fun insertAccount(account: AccountEntity) = accountDao.insertAccount(account)

    suspend fun updateAccount(account: AccountEntity) = accountDao.updateAccount(account)

    suspend fun deleteAccount(account: AccountEntity) = accountDao.deleteAccount(account)

    suspend fun getAccountById(accountId: String) = accountDao.getAccountById(accountId)

    fun getAccountByIdLive(accountId: String): LiveData<AccountEntity?> =
        accountDao.getAccountByIdLive(accountId)

    fun getTotalBalance(userId: String): LiveData<Double?> = accountDao.getTotalBalance(userId)

    suspend fun updateAccountBalance(accountId: String, newBalance: Double) {
        accountDao.updateAccountBalance(accountId, newBalance, System.currentTimeMillis())
    }
}
