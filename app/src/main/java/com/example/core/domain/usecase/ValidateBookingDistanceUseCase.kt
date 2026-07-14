package com.example.core.domain.usecase

import javax.inject.Inject

/**
 * Business Rule: Minimum booking distance is 100 km.
 */
class ValidateBookingDistanceUseCase @Inject constructor() {
    
    /**
     * Returns true if the distance meets the strict 100 km minimum requirement.
     */
    operator fun invoke(distanceKm: Double): Boolean {
        return distanceKm >= 100.0
    }
}
