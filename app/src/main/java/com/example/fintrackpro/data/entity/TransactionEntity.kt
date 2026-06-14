package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String = UUID.randomUUID().toString(),
    val userId: String,
    val accountId: String,
    val categoryId: String,
    val amount: Double,
    val description: String,
    val type: String, // INCOME or EXPENSE
    val date: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
