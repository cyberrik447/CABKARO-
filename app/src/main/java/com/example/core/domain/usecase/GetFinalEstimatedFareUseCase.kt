package com.example.core.domain.usecase

import com.example.core.domain.Resource
import com.example.core.domain.repository.AdminConfigRepository
import com.example.core.model.VehicleType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase to compute the final estimated ride fare.
 * Strict Business Rule: Pricing calculation details remain hidden from users. Only the final fare is shown.
 */
class GetFinalEstimatedFareUseCase @Inject constructor(
    private val adminConfigRepository: AdminConfigRepository,
    private val validateDistance: ValidateBookingDistanceUseCase
) {
    operator fun invoke(distanceKm: Double, vehicleType: VehicleType): Flow<Resource<Double>> {
        if (!validateDistance(distanceKm)) {
            return kotlinx.coroutines.flow.flowOf(
                Resource.Error(IllegalArgumentException("Distance must be at least 100 km."))
            )
        }
        return adminConfigRepository.calculateEstimatedFare(distanceKm, vehicleType)
    }
}
