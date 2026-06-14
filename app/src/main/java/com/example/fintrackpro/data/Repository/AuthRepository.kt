package com.example.fintrackpro.data.Repository

import com.example.fintrackpro.data.Dao.UserDao
import com.example.fintrackpro.data.entity.User
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val userDao: UserDao) {

    suspend fun login(username: String, passwordHash: String): User? {
        return userDao.login(username, passwordHash)
    }

    suspend fun register(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateLastLogin(userId: Int) {
        userDao.updateLastLogin(userId, System.currentTimeMillis())
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    fun getUserFlow(userId: Int): Flow<User?> {
        return userDao.getUserByIdFlow(userId)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}