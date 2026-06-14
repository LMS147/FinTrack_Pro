package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class AccountEntity(
    @PrimaryKey
    val accountId: String = UUID.randomUUID().toString(),
    val userId: String,
    val accountName: String,
    val accountType: String, // BANK, CASH, CREDIT_CARD, INVESTMENT
    val balance: Double = 0.0,
    val currency: String = "USD",
    val color: String = "#2196F3",
    val icon: String = "account_balance",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
