package com.example.core.data.repository

import com.example.core.domain.repository.VehicleRepository
import com.example.core.model.Vehicle
import com.example.core.model.VehicleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepositoryImpl @Inject constructor() : VehicleRepository {
    
    // Strict Business Rule: Only Sedan and SUV are supported.
    private val vehicles = listOf(
        Vehicle(
            type = VehicleType.SEDAN,
            name = "Cabkaro Sedan",
            description = "Comfortable everyday sedan for 1 to 4 passengers.",
            capacity = 4
        ),
        Vehicle(
            type = VehicleType.SUV,
            name = "Cabkaro SUV",
            description = "Spacious premium sport utility vehicle ideal for larger groups or extra luggage.",
            capacity = 6
        )
    )

    override fun getSupportedVehicles(): Flow<List<Vehicle>> {
        return flowOf(vehicles)
    }
}
