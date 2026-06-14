package com.example.fintrackpro.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user sessions using SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "fintrack_pro_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Saves user ID and sets logged-in status to true.
     */
    fun saveSession(userId: Int) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Clears user session.
     */
    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    /**
     * Returns true if user is logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Returns the currently logged-in user ID, or -1 if not found.
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }
}
