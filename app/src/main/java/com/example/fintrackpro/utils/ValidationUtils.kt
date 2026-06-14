package com.example.fintrackpro.utils

import android.util.Patterns

object ValidationUtils {

    fun validateRegistration(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String
    ): String? {
        when {
            displayName.isBlank() -> return "Display name is required"
            username.isBlank() -> return "Username is required"
            username.length < 3 -> return "Username must be at least 3 characters"
            email.isBlank() -> return "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> return "Invalid email format"
            password.isBlank() -> return "Password is required"
            password.length < 6 -> return "Password must be at least 6 characters"
            password != confirmPassword -> return "Passwords do not match"
        }
        return null
    }
}