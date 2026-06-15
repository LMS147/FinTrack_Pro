package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "savings_goals",
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
data class SavingsGoalEntity(
    @PrimaryKey
    val goalId: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val description: String? = null,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val currency: String = "ZAR",
    val targetDate: Long,
    val icon: String = "savings",
    val color: String = "#4CAF50",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

