package com.example.core.data.repository

import com.example.core.domain.Resource
import com.example.core.domain.repository.AdminConfigRepository
import com.example.core.model.AdminConfig
import com.example.core.model.VehicleType
import com.example.core.config.ConfigManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminConfigRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminConfigRepository {

    override fun getAdminConfig(): Flow<Resource<AdminConfig>> = flow {
        emit(Resource.Loading)
        try {
            val pricing = try {
                ConfigManager.getInstance().currentPricing.value
            } catch (e: Exception) {
                null
            }
            val adminConfig = AdminConfig(
                sedanBasePricePerKm = pricing?.sedanPricePerKm ?: 15.0,
                suvBasePricePerKm = pricing?.suvPricePerKm ?: 22.0,
                minimumBookingDistanceKm = pricing?.minimumBookingDistanceKm ?: 100.0
            )
            emit(Resource.Success(adminConfig))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun calculateEstimatedFare(
        distanceKm: Double,
        vehicleType: VehicleType
    ): Flow<Resource<Double>> = flow {
        emit(Resource.Loading)
        try {
            val pricing = try {
                ConfigManager.getInstance().currentPricing.value
            } catch (e: Exception) {
                null
            }
            
            // Extract prices
            val basePrice = when (vehicleType) {
                VehicleType.SEDAN -> pricing?.sedanPricePerKm ?: 15.0
                VehicleType.SUV -> pricing?.suvPricePerKm ?: 22.0
            }
            
            // Add dynamic remote pricing rules
            val nightSurcharge = pricing?.nightSurcharge ?: 0.0
            val holidaySurcharge = pricing?.holidaySurcharge ?: 0.0
            
            // Calculate final hidden fare
            val finalFare = (distanceKm * basePrice) + nightSurcharge + holidaySurcharge
            emit(Resource.Success(finalFare))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}

