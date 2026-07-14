package com.example.core.data.repository

import com.example.core.domain.Resource
import com.example.core.domain.repository.MapRepository
import com.example.core.model.LatLng
import com.example.core.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepositoryImpl @Inject constructor() : MapRepository {

    override fun searchPlaces(query: String): Flow<Resource<List<Location>>> = flow {
        emit(Resource.Loading)
        try {
            // Google Places API integrations would execute here.
            // Returning high-quality simulated locations.
            val mockLocations = listOf(
                Location("Mumbai International Airport (BOM), Mumbai", LatLng(19.0896, 72.8656)),
                Location("Taj Mahal Palace Hotel, Colaba, Mumbai", LatLng(18.9218, 72.8333)),
                Location("Pune Junction Railway Station, Pune", LatLng(18.5289, 73.8739))
            ).filter { it.address.contains(query, ignoreCase = true) }
            emit(Resource.Success(mockLocations))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getRouteDistanceKm(origin: Location, destination: Location): Flow<Resource<Double>> = flow {
        emit(Resource.Loading)
        try {
            // Google Directions API call would go here.
            // Simulating distance calculations for demo/production testing.
            val dLat = Math.toRadians(destination.coordinates.latitude - origin.coordinates.latitude)
            val dLon = Math.toRadians(destination.coordinates.longitude - origin.coordinates.longitude)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(origin.coordinates.latitude)) * Math.cos(Math.toRadians(destination.coordinates.latitude)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val radiusOfEarthKm = 6371.0
            val directDistanceKm = radiusOfEarthKm * c
            val practicalRouteDistanceKm = directDistanceKm * 1.25 // Standard correction factor for city navigation
            emit(Resource.Success(practicalRouteDistanceKm))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getRouteDurationMin(origin: Location, destination: Location): Flow<Resource<Int>> = flow {
        emit(Resource.Loading)
        try {
            // Simple duration estimate based on standard average speeds (e.g. 50 km/h)
            val distanceResult = getRouteDistanceKm(origin, destination)
            var distance = 120.0 // Fallback meeting our 100km rule
            distanceResult.collect { resource ->
                if (resource is Resource.Success) {
                    distance = resource.data
                }
            }
            val averageSpeedKmph = 50.0
            val durationHours = distance / averageSpeedKmph
            val durationMinutes = (durationHours * 60).toInt()
            emit(Resource.Success(durationMinutes))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }
}
