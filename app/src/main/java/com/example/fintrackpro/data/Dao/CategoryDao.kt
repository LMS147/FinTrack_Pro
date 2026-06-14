package com.example.fintrackpro.data.Dao

import androidx.room.*
import com.example.fintrackpro.data.entity.CategoryEntity
import androidx.lifecycle.LiveData

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE type = :type AND (userId = :userId OR isDefault = 1) ORDER BY name ASC")
    fun getCategoriesByType(userId: String, type: String): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE (userId = :userId OR isDefault = 1) ORDER BY type, name")
    fun getAllCategories(userId: String): LiveData<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories WHERE isDefault = 1")
    suspend fun getDefaultCategoriesCount(): Int
}
