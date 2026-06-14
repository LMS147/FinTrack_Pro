package com.example.fintrackpro.data.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores references to receipt photos attached to expenses.
 */
@Entity(tableName = "photos")
data class ExpensePhoto(
    @PrimaryKey(autoGenerate = true)
    val photoId: Int = 0,

    val expenseId: String,                 // Foreign key to TransactionEntity
    val photoUri: Uri,                  // Local URI of the image file
    val caption: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)