package com.example.core.model

import kotlinx.serialization.Serializable

/**
 * User account model.
 */
@Serializable
data class User(
    val uid: String,
    val name: String,
    val mobileNumber: String,
    val isOtpVerified: Boolean = false,
    val isRegistered: Boolean = false
)

/**
 * Supported ride categories.
 * Strict Business Rule: Only Sedan and SUV are supported.
 */
enum class VehicleType {
    SEDAN,
    SUV
}

/**
 * Vehicle details display model.
 */
@Serializable
data class Vehicle(
    val type: VehicleType,
    val name: String,
    val description: String,
    val capacity: Int,
    val iconUrl: String? = null
)

/**
 * Geographic coordinate representation.
 */
@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
)

/**
 * Location coordinate paired with a textual address.
 */
@Serializable
data class Location(
    val address: String,
    val coordinates: LatLng
)

/**
 * Ride booking states.
 */
enum class BookingStatus {
    PENDING,
    ACCEPTED,
    COMPLETED,
    CANCELLED
}

/**
 * Booking model representing a ride.
 * Strict Business Rule:
 * 1. Booking IDs must be unique.
 * 2. Minimum booking distance is 100 km.
 * 3. Users only see the final estimated fare. Price calculation belongs to backend/admin.
 */
@Serializable
data class Booking(
    val bookingId: String,
    val userId: String,
    val pickup: Location,
    val dropoff: Location,
    val distanceKm: Double,
    val vehicleType: VehicleType,
    val finalEstimatedFare: Double, // Hidden details, only final fare shown
    val status: BookingStatus = BookingStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
) {
    init {
        require(distanceKm >= 100.0) {
            "Cabkaro Business Rule: Minimum booking distance must be 100 km."
        }
    }
}

/**
 * Admin configurations retrieved from Firebase.
 * Admin controls pricing through Firebase.
 */
@Serializable
data class AdminConfig(
    val sedanBasePricePerKm: Double = 15.0,
    val suvBasePricePerKm: Double = 22.0,
    val minimumBookingDistanceKm: Double = 100.0
)
