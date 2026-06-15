package com.example.fintrackpro.ui.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.SharedWalletEntity
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class SharedWalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FinTrackApp).sharedWalletRepository
    private val sessionManager = SessionManager(application)
    
    private val _userId = MutableLiveData<String>().apply {
        value = sessionManager.getUserId() ?: ""
    }

    val sharedWallets: LiveData<List<SharedWalletEntity>> = _userId.switchMap { id ->
        repository.getSharedWalletsByUser(id)
    }

    val appCurrency = MutableLiveData<String>().apply {
        value = sessionManager.getCurrency()
    }

    private val currencyRepository = (application as FinTrackApp).currencyRepository

    val convertedWallets: LiveData<List<SharedWalletEntity>> = androidx.lifecycle.MediatorLiveData<List<SharedWalletEntity>>().apply {
        var currentWallets: List<SharedWalletEntity>? = null
        var currentCurrency: String = sessionManager.getCurrency()

        fun update() {
            val wallets = currentWallets ?: return
            viewModelScope.launch {
                val converted = wallets.map { wallet ->
                    val convertedBalance = currencyRepository.convertCurrency(
                        wallet.totalBalance,
                        wallet.currency,
                        currentCurrency
                    )
                    wallet.copy(totalBalance = convertedBalance, currency = currentCurrency)
                }
                value = converted
            }
        }

        addSource(sharedWallets) {
            currentWallets = it
            update()
        }
        addSource(appCurrency) {
            currentCurrency = it
            update()
        }
    }

    fun refreshCurrency() {
        appCurrency.value = sessionManager.getCurrency()
    }

    fun createSharedWallet(name: String, description: String?) {
        val ownerId = _userId.value ?: return
        viewModelScope.launch {
            val wallet = SharedWalletEntity(
                ownerId = ownerId,
                name = name,
                description = description,
                memberIds = ownerId // Start with only the owner
            )
            repository.insertSharedWallet(wallet)
        }
    }

    fun joinSharedWallet(inviteCode: String) {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            val wallet = repository.getSharedWalletByInviteCode(inviteCode)
            if (wallet != null) {
                if (!wallet.memberIds.contains(userId)) {
                    val updatedMembers = "${wallet.memberIds},$userId"
                    repository.updateSharedWallet(wallet.copy(memberIds = updatedMembers))
                }
            }
        }
    }
}
