package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.CurrencyRateDao
import com.example.fintrackpro.data.entity.CurrencyRateEntity

class CurrencyRepository(private val currencyRateDao: CurrencyRateDao) {

    suspend fun getCurrencyRate(from: String, to: String): CurrencyRateEntity? {
        return currencyRateDao.getCurrencyRate(from, to)
    }

    suspend fun convertCurrency(amount: Double, from: String, to: String): Double {
        if (from == to) return amount
        
        val rate = currencyRateDao.getCurrencyRate(from, to)
        if (rate != null) return amount * rate.rate
        
        val inverseRate = currencyRateDao.getCurrencyRate(to, from)
        if (inverseRate != null) return amount / inverseRate.rate
        
        // If no direct or inverse rate, try through USD
        if (from != "USD" && to != "USD") {
            val fromToUsd = convertCurrency(amount, from, "USD")
            return convertCurrency(fromToUsd, "USD", to)
        }
        
        return amount
    }

    suspend fun getAllCurrencyRates() = currencyRateDao.getAllCurrencyRates()

    suspend fun insertCurrencyRate(rate: CurrencyRateEntity) =
        currencyRateDao.insertCurrencyRate(rate)
}
