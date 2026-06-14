package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Transaction/Transaction record.
 *  date, start/end times, description, category.
 * Transaction Management.
 */
@Entity(tableName = "expenses")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,

    val userId: Int,                    // Owner of the expense
    val categoryId: Int,                // Foreign key to Category

    val amount: Double,                 // Positive for expense (or negative for income, if unified)
    val description: String,
    val date: Date,                     // Date of transaction

    val startTime: String? = null,      // Time string (HH:mm)
    val endTime: String? = null,        // Optional duration

    val isIncome: Boolean = false,      // Distinguish income vs expense

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)