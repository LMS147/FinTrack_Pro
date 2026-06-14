package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "achievements",
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
data class AchievementEntity(
    @PrimaryKey
    val achievementId: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val category: String, // SAVINGS, BUDGETING, CONSISTENCY, MILESTONES
    val points: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val target: Int,
    val createdAt: Long = System.currentTimeMillis()
)

