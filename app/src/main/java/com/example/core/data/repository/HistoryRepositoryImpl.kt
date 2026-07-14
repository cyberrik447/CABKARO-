package com.example.core.data.repository

import com.example.core.database.dao.BookingDao
import com.example.core.domain.Resource
import com.example.core.domain.repository.HistoryRepository
import com.example.core.model.Booking
import com.example.core.model.BookingStatus
import com.example.core.model.LatLng
import com.example.core.model.Location
import com.example.core.model.VehicleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val bookingDao: BookingDao
) : HistoryRepository {

    override fun getUserBookingHistory(userId: String): Flow<Resource<List<Booking>>> = flow {
        emit(Resource.Loading)
        try {
            // Flow from local SQLite cache via Dao
            bookingDao.getAllBookings().collect { entityList ->
                val bookings = entityList.map { entity ->
                    Booking(
                        bookingId = entity.bookingId,
                        userId = entity.userId,
                        pickup = Location(
                            entity.pickupAddress,
                            LatLng(entity.pickupLatitude, entity.pickupLongitude)
                        ),
                        dropoff = Location(
                            entity.dropoffAddress,
                            LatLng(entity.dropoffLatitude, entity.dropoffLongitude)
                        ),
                        distanceKm = entity.distanceKm,
                        vehicleType = VehicleType.valueOf(entity.vehicleType),
                        finalEstimatedFare = entity.finalEstimatedFare,
                        status = BookingStatus.valueOf(entity.status),
                        timestamp = entity.timestamp
                    )
                }
                emit(Resource.Success(bookings))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}
