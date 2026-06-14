package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val categoryId: String = UUID.randomUUID().toString(),
    val userId: String?, // null for default categories
    val name: String,
    val type: String, // EXPENSE or INCOME
    val icon: String,
    val color: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)