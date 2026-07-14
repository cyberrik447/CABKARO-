package com.example.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation route definitions.
 */
sealed interface Screen {

    @Serializable
    object Splash : Screen

    @Serializable
    object Authentication : Screen

    @Serializable
    data class OtpVerification(val mobileNumber: String) : Screen

    @Serializable
    object Home : Screen

    @Serializable
    data class BookingConfirmation(
        val pickupAddress: String,
        val pickupLat: Double,
        val pickupLng: Double,
        val dropoffAddress: String,
        val dropoffLat: Double,
        val dropoffLng: Double,
        val distanceKm: Double
    ) : Screen

    @Serializable
    data class ActiveBooking(val bookingId: String) : Screen

    @Serializable
    object History : Screen

    @Serializable
    object Settings : Screen
}
