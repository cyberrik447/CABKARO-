package com.example.core.domain.repository

import com.example.core.domain.Resource
import com.example.core.model.Booking
import com.example.core.model.Location
import com.example.core.model.VehicleType
import kotlinx.coroutines.flow.Flow

/**
 * Interface handling ride booking workflows.
 * Strict Business Rules:
 * - Minimum booking distance is 100 km.
 * - Only Sedan and SUV are supported.
 * - Users only see final estimated fare.
 * - Booking IDs must be unique.
 */
interface BookingRepository {

    /**
     * Creates a new booking in Firebase.
     * Generates a unique booking ID and verifies that the distance meets the minimum requirement (100km).
     */
    fun createBooking(
        pickup: Location,
        dropoff: Location,
        vehicleType: VehicleType,
        distanceKm: Double,
        finalEstimatedFare: Double
    ): Flow<Resource<Booking>>

    /**
     * Gets booking details by its unique booking ID.
     */
    fun getBookingById(bookingId: String): Flow<Resource<Booking?>>

    /**
     * Fetches current active booking (if any).
     */
    fun getActiveBooking(): Flow<Resource<Booking?>>

    /**
     * Cancels an active booking.
     */
    fun cancelBooking(bookingId: String): Flow<Resource<Unit>>
}
