package com.example.fintrackpro.data.Dao

import androidx.room.*
import com.example.fintrackpro.data.entity.CurrencyRateEntity

@Dao
interface CurrencyRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRate(rate: CurrencyRateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRates(rates: List<CurrencyRateEntity>)

    @Query("SELECT * FROM currency_rates WHERE fromCurrency = :from AND toCurrency = :to LIMIT 1")
    suspend fun getCurrencyRate(from: String, to: String): CurrencyRateEntity?

    @Query("SELECT * FROM currency_rates")
    suspend fun getAllCurrencyRates(): List<CurrencyRateEntity>
}
