package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing booking history details.
 */
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val bookingId: String,
    val userId: String,
    val pickupAddress: String,
    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val dropoffAddress: String,
    val dropoffLatitude: Double,
    val dropoffLongitude: Double,
    val distanceKm: Double,
    val vehicleType: String,
    val finalEstimatedFare: Double,
    val status: String,
    val timestamp: Long
)
