package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.CategoryDao
import com.example.fintrackpro.data.entity.CategoryEntity

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getCategoriesByType(userId: String, type: String): LiveData<List<CategoryEntity>> {
        return categoryDao.getCategoriesByType(userId, type)
    }

    fun getAllCategories(userId: String): LiveData<List<CategoryEntity>> {
        return categoryDao.getAllCategories(userId)
    }

    suspend fun insertCategory(category: CategoryEntity) = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun getCategoryById(categoryId: String) = categoryDao.getCategoryById(categoryId)

    suspend fun getDefaultCategoriesCount() = categoryDao.getDefaultCategoriesCount()
}
