package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.R
import com.example.fintrackpro.data.Dao.CategoryDao
import com.example.fintrackpro.data.entity.Category
import kotlinx.coroutines.flow.Flow


class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun insertDefaultCategories(userId: Int) {
        val existing = categoryDao.getCategoryListOnce(userId)
        if (existing.isEmpty()) {
            val defaults = listOf(
                Category(userId = userId, name = "Food", isDefault = true, iconResId = R.drawable.ic_camera),   // use any icon, or 0
                Category(userId = userId, name = "Transport", isDefault = true),
                Category(userId = userId, name = "Entertainment", isDefault = true),
                Category(userId = userId, name = "Bills", isDefault = true),
                Category(userId = userId, name = "Shopping", isDefault = true),
                Category(userId = userId, name = "Other", isDefault = true)
            )
            defaults.forEach { categoryDao.insertCategory(it) }
        }
    }

    suspend fun createCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    fun getCategoriesForUser(userId: Int): Flow<List<Category>> {
        return categoryDao.getCategoriesForUser(userId)
    }

    suspend fun getCategoryList(userId: Int): List<Category> {
        return categoryDao.getCategoryListOnce(userId)
    }

    suspend fun getCategoryById(categoryId: Int): Category? {
        return categoryDao.getCategoryById(categoryId)
    }
}