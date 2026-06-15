package com.example.fintrackpro.ui.gamification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.AchievementEntity
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FinTrackApp).achievementRepository
    private val sessionManager = SessionManager(application)
    
    private val _userId = MutableLiveData<String>().apply {
        value = sessionManager.getUserId() ?: ""
    }

    val achievements: LiveData<List<AchievementEntity>> = _userId.switchMap { id ->
        repository.getAchievementsByUser(id)
    }

    val totalPoints: LiveData<Int?> = _userId.switchMap { id ->
        repository.getTotalPoints(id)
    }

    init {
        checkAndInitializeAchievements()
    }

    private fun checkAndInitializeAchievements() {
        val userId = _userId.value ?: return
        if (userId.isEmpty()) return

        viewModelScope.launch {
            val count = repository.getAchievementCount(userId)
            if (count == 0) {
                repository.initializeAchievementsForUser(userId)
            }
        }
    }
}
