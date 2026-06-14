package com.example.fintrackpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "currency_rates",
    indices = [Index(value = ["fromCurrency", "toCurrency"], unique = true)]
)
data class CurrencyRateEntity(
    @PrimaryKey
    val rateId: String = UUID.randomUUID().toString(),
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

