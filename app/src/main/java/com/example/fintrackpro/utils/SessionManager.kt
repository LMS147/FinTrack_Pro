package com.example.fintrackpro.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun saveCurrency(currencyCode: String) {
        prefs.edit().putString(KEY_CURRENCY, currencyCode).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(KEY_CURRENCY, "ZAR") ?: "ZAR"
    }

    fun isLoggedIn(): Boolean {
        return getUserId() != null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "FinTrackSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_CURRENCY = "currency_code"
    }
}
