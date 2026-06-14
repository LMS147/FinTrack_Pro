package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "shared_wallets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerId")]
)
data class SharedWalletEntity(
    @PrimaryKey
    val walletId: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val name: String,
    val description: String? = null,
    val totalBalance: Double = 0.0,
    val currency: String = "USD",
    val memberIds: String, // Comma-separated user IDs
    val inviteCode: String = UUID.randomUUID().toString().substring(0, 8),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

