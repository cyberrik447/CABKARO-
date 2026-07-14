package com.example.core.domain.repository

import com.example.core.model.Vehicle
import kotlinx.coroutines.flow.Flow

/**
 * Interface managing vehicles.
 * Strict Business Rule: Only Sedan and SUV are supported.
 */
interface VehicleRepository {
    
    /**
     * Retrieves the supported vehicle categories.
     */
    fun getSupportedVehicles(): Flow<List<Vehicle>>
}
