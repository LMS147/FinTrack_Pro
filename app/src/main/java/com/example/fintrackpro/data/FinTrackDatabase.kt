package com.example.fintrackpro.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.fintrackpro.data.entity.*
import com.example.fintrackpro.data.Dao.*

/**
 * Main Room database class for FinTrack Pro.
 * Implements offline data storage.
 *
 * Database versioning strategy: Increment version when schema changes.
 */
@Database(
    entities = [
        User::class,
        Category::class,
        Transaction::class,
        Budget::class,
        ExpensePhoto::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinTrackDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: FinTrackDatabase? = null

        fun getDatabase(context: Context): FinTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinTrackDatabase::class.java,
                    "fintrack_pro.db"
                )
                    .fallbackToDestructiveMigration() // Only for prototype; use migrations in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}