package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("categoryId")]
)
data class BudgetEntity(
    @PrimaryKey
    val budgetId: String = UUID.randomUUID().toString(),
    val userId: String,
    val categoryId: String,
    val categoryName: String,
    val amount: Double,
    val period: String, // DAILY, WEEKLY, MONTHLY
    val startDate: Long,
    val endDate: Long,
    val spent: Double = 0.0,
    val alertThreshold: Int = 80, // Alert when 80% spent
    val alertEnabled: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)