package com.example.core.domain.repository

import com.example.core.domain.Resource
import com.example.core.model.Booking
import kotlinx.coroutines.flow.Flow

/**
 * Interface managing historical bookings.
 */
interface HistoryRepository {

    /**
     * Gets past rides for a user.
     */
    fun getUserBookingHistory(userId: String): Flow<Resource<List<Booking>>>
}
