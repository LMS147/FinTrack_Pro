package com.example.fintrackpro.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.AccountEntity
import com.example.fintrackpro.data.Repository.AccountRepository
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val accountRepository: AccountRepository = (application as FinTrackApp).accountRepository
    private val sessionManager = SessionManager(application)

    private val userId: String = sessionManager.getUserId() ?: ""

    val accounts: LiveData<List<AccountEntity>> = accountRepository.getAccountsByUser(userId)

    private val _saveState = MutableLiveData<SaveState>()
    val saveState: LiveData<SaveState> = _saveState

    fun addAccount(
        accountName: String,
        accountType: String,
        balance: Double,
        currency: String,
        color: String,
        icon: String
    ) {
        _saveState.value = SaveState.Loading
        viewModelScope.launch {
            try {
                val account = AccountEntity(
                    userId = userId,
                    accountName = accountName,
                    accountType = accountType,
                    balance = balance,
                    currency = currency,
                    color = color,
                    icon = icon
                )
                accountRepository.insertAccount(account)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to add account")
            }
        }
    }

    fun updateAccount(account: AccountEntity) {
        viewModelScope.launch {
            try {
                accountRepository.updateAccount(account)
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to update account")
            }
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            try {
                accountRepository.deleteAccount(account)
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to delete account")
            }
        }
    }
}

sealed class SaveState {
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}
