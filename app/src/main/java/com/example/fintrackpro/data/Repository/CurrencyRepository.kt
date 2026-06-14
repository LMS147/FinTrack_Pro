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
        return if (rate != null) {
            amount * rate.rate
        } else {
            amount
        }
    }

    suspend fun getAllCurrencyRates() = currencyRateDao.getAllCurrencyRates()

    suspend fun insertCurrencyRate(rate: CurrencyRateEntity) =
        currencyRateDao.insertCurrencyRate(rate)
}
