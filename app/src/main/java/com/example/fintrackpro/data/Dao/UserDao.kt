package com.example.fintrackpro.data.Dao

import androidx.room.*
import com.example.fintrackpro.data.entity.UserEntity
import androidx.lifecycle.LiveData

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserByIdLive(userId: String): LiveData<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserLive(): LiveData<UserEntity?>
}