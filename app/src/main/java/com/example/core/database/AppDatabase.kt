package com.example.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.database.dao.BookingDao
import com.example.core.database.dao.UserDao
import com.example.core.database.entity.BookingEntity
import com.example.core.database.entity.UserEntity
import com.example.core.config.AppConfigEntity
import com.example.core.config.PricingConfigEntity
import com.example.core.config.BookingPoliciesEntity
import com.example.core.config.PopularRouteEntity
import com.example.core.config.ConfigDao

@Database(
    entities = [
        UserEntity::class,
        BookingEntity::class,
        AppConfigEntity::class,
        PricingConfigEntity::class,
        BookingPoliciesEntity::class,
        PopularRouteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao
    abstract fun configDao(): ConfigDao
}

