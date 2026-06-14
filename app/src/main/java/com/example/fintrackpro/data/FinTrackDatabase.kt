package com.example.fintrackpro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fintrackpro.data.Dao.*
import com.example.fintrackpro.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        SharedWalletEntity::class,
        AchievementEntity::class,
        SavingsGoalEntity::class,
        CurrencyRateEntity::class,
        ExpensePhoto::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinTrackDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun sharedWalletDao(): SharedWalletDao
    abstract fun achievementDao(): AchievementDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun currencyRateDao(): CurrencyRateDao

    companion object {
        @Volatile
        private var INSTANCE: FinTrackDatabase? = null

        fun getDatabase(context: Context): FinTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinTrackDatabase::class.java,
                    "fintrack_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultCategories(database.categoryDao())
                        populateDefaultCurrencyRates(database.currencyRateDao())
                    }
                }
            }
        }

        private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                // Expense Categories
                CategoryEntity(name = "Food & Dining", type = "EXPENSE", icon = "restaurant", color = "#FF5722", isDefault = true, userId = null),
                CategoryEntity(name = "Transportation", type = "EXPENSE", icon = "directions_car", color = "#2196F3", isDefault = true, userId = null),
                CategoryEntity(name = "Shopping", type = "EXPENSE", icon = "shopping_cart", color = "#9C27B0", isDefault = true, userId = null),
                CategoryEntity(name = "Entertainment", type = "EXPENSE", icon = "movie", color = "#E91E63", isDefault = true, userId = null),
                CategoryEntity(name = "Bills & Utilities", type = "EXPENSE", icon = "receipt_long", color = "#FF9800", isDefault = true, userId = null),
                CategoryEntity(name = "Healthcare", type = "EXPENSE", icon = "local_hospital", color = "#F44336", isDefault = true, userId = null),
                CategoryEntity(name = "Education", type = "EXPENSE", icon = "school", color = "#3F51B5", isDefault = true, userId = null),
                CategoryEntity(name = "Travel", type = "EXPENSE", icon = "flight", color = "#00BCD4", isDefault = true, userId = null),
                CategoryEntity(name = "Personal Care", type = "EXPENSE", icon = "spa", color = "#E91E63", isDefault = true, userId = null),
                CategoryEntity(name = "Groceries", type = "EXPENSE", icon = "local_grocery_store", color = "#4CAF50", isDefault = true, userId = null),
                CategoryEntity(name = "Investments", type = "EXPENSE", icon = "trending_up", color = "#009688", isDefault = true, userId = null),
                CategoryEntity(name = "Other Expense", type = "EXPENSE", icon = "more_horiz", color = "#9E9E9E", isDefault = true, userId = null),
                
                // Income Categories
                CategoryEntity(name = "Salary", type = "INCOME", icon = "payments", color = "#4CAF50", isDefault = true, userId = null),
                CategoryEntity(name = "Business", type = "INCOME", icon = "business_center", color = "#2196F3", isDefault = true, userId = null),
                CategoryEntity(name = "Investment Income", type = "INCOME", icon = "account_balance", color = "#009688", isDefault = true, userId = null),
                CategoryEntity(name = "Gifts", type = "INCOME", icon = "card_giftcard", color = "#E91E63", isDefault = true, userId = null),
                CategoryEntity(name = "Refunds", type = "INCOME", icon = "replay", color = "#00BCD4", isDefault = true, userId = null),
                CategoryEntity(name = "Other Income", type = "INCOME", icon = "attach_money", color = "#8BC34A", isDefault = true, userId = null)
            )
            categoryDao.insertCategories(defaultCategories)
        }

        private suspend fun populateDefaultCurrencyRates(currencyRateDao: CurrencyRateDao) {
            val defaultRates = listOf(
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "EUR", rate = 0.92),
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "GBP", rate = 0.79),
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "INR", rate = 83.12),
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "JPY", rate = 149.50),
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "CNY", rate = 7.24),
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "AUD", rate = 1.52),
                CurrencyRateEntity(fromCurrency = "USD", toCurrency = "CAD", rate = 1.36),
                CurrencyRateEntity(fromCurrency = "EUR", toCurrency = "USD", rate = 1.09),
                CurrencyRateEntity(fromCurrency = "GBP", toCurrency = "USD", rate = 1.27),
                CurrencyRateEntity(fromCurrency = "INR", toCurrency = "USD", rate = 0.012)
            )
            currencyRateDao.insertCurrencyRates(defaultRates)
        }
    }
}
