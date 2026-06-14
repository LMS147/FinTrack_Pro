package com.example.fintrackpro.data.Repository

import androidx.lifecycle.LiveData
import com.example.fintrackpro.data.Dao.UserDao
import com.example.fintrackpro.data.entity.UserEntity
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(email: String, password: String, fullName: String): Result<UserEntity> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception("Email already registered"))
            } else {
                val hashedPassword = hashPassword(password)
                val newUser = UserEntity(
                    email = email,
                    password = hashedPassword,
                    fullName = fullName
                )
                userDao.insertUser(newUser)
                Result.success(newUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user != null && verifyPassword(password, user.password)) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun getUserById(userId: String) = userDao.getUserById(userId)

    fun getUserByIdLive(userId: String): LiveData<UserEntity?> = userDao.getUserByIdLive(userId)

    suspend fun getCurrentUser() = userDao.getCurrentUser()

    fun getCurrentUserLive(): LiveData<UserEntity?> = userDao.getCurrentUserLive()

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return hashPassword(password) == hashedPassword
    }
}
