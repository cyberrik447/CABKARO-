package com.example.core.domain.repository

import com.example.core.domain.Resource
import com.example.core.model.AdminConfig
import com.example.core.model.VehicleType
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing the Admin Configuration layer controlled via Firebase.
 * Strict Business Rules:
 * - Admin controls pricing through Firebase.
 * - Pricing calculations remain hidden from users; only final fares are visible.
 */
interface AdminConfigRepository {

    /**
     * Retrieves the admin configuration parameters securely from Firebase.
     */
    fun getAdminConfig(): Flow<Resource<AdminConfig>>

    /**
     * Safely computes the final estimated fare based on dynamic database rates.
     * Keeps calculations hidden by returning only the final, styled, user-facing estimated fare amount.
     */
    fun calculateEstimatedFare(
        distanceKm: Double,
        vehicleType: VehicleType
    ): Flow<Resource<Double>>
}
