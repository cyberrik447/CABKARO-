package com.example.core.domain.repository

import com.example.core.domain.Resource
import com.example.core.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing the Maps and Places integration layer.
 * Resolves locations and estimates routes.
 */
interface MapRepository {

    /**
     * Resolves textual queries into precise coordinate locations.
     */
    fun searchPlaces(query: String): Flow<Resource<List<Location>>>

    /**
     * Calculates the direct distance in kilometers between two locations.
     */
    fun getRouteDistanceKm(origin: Location, destination: Location): Flow<Resource<Double>>

    /**
     * Estimates route travel duration in minutes.
     */
    fun getRouteDurationMin(origin: Location, destination: Location): Flow<Resource<Int>>
}
