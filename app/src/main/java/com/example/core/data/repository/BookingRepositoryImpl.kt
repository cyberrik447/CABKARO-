package com.example.core.data.repository

import com.example.core.database.dao.BookingDao
import com.example.core.database.entity.BookingEntity
import com.example.core.domain.Resource
import com.example.core.domain.repository.BookingRepository
import com.example.core.model.Booking
import com.example.core.model.BookingStatus
import com.example.core.model.Location
import com.example.core.model.VehicleType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val bookingDao: BookingDao
) : BookingRepository {

    override fun createBooking(
        pickup: Location,
        dropoff: Location,
        vehicleType: VehicleType,
        distanceKm: Double,
        finalEstimatedFare: Double
    ): Flow<Resource<Booking>> = flow {
        emit(Resource.Loading)
        try {
            // Strict Business Rule check: minimum 100km
            if (distanceKm < 100.0) {
                throw IllegalArgumentException("Cabkaro Business Rule: Minimum booking distance is 100 km.")
            }

            // Generate a unique Booking ID (Unique booking ID rule)
            val bookingId = "BK-${UUID.randomUUID().toString().take(8).uppercase()}"
            val booking = Booking(
                bookingId = bookingId,
                userId = "current_user_id", // This would fetch actual authenticated userId
                pickup = pickup,
                dropoff = dropoff,
                distanceKm = distanceKm,
                vehicleType = vehicleType,
                finalEstimatedFare = finalEstimatedFare,
                status = BookingStatus.PENDING
            )

            // Cache to local Room database
            bookingDao.insertBooking(
                BookingEntity(
                    bookingId = booking.bookingId,
                    userId = booking.userId,
                    pickupAddress = booking.pickup.address,
                    pickupLatitude = booking.pickup.coordinates.latitude,
                    pickupLongitude = booking.pickup.coordinates.longitude,
                    dropoffAddress = booking.dropoff.address,
                    dropoffLatitude = booking.dropoff.coordinates.latitude,
                    dropoffLongitude = booking.dropoff.coordinates.longitude,
                    distanceKm = booking.distanceKm,
                    vehicleType = booking.vehicleType.name,
                    finalEstimatedFare = booking.finalEstimatedFare,
                    status = booking.status.name,
                    timestamp = booking.timestamp
                )
            )

            // Write to Remote Cloud Firestore database
            // Note: firestore.collection().document().set() can be performed asynchronously or synchronously.
            // We use a simplified remote save placeholder.
            firestore.collection("bookings")
                .document(bookingId)
                .set(booking)

            emit(Resource.Success(booking))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getBookingById(bookingId: String): Flow<Resource<Booking?>> = flow {
        emit(Resource.Loading)
        try {
            // Retrieve from Firestore or local fallback
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getActiveBooking(): Flow<Resource<Booking?>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(null))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun cancelBooking(bookingId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}
