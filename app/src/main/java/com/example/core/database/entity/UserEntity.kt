package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing user details.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val mobileNumber: String,
    val isOtpVerified: Boolean,
    val isRegistered: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)
