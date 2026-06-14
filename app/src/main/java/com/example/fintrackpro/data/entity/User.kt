package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * User entity for authentication and profile management.
 *  User Account Management.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String = UUID.randomUUID().toString(),
    val email: String,
    val password: String, // Hashed password
    val fullName: String,
    val profileImageUrl: String? = null,
    val defaultCurrency: String = "ZAR",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
