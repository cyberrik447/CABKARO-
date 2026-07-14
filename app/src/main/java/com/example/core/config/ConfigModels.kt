package com.example.core.config

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// ==========================================
// 1. Domain Models (Exposed to UI/ViewModels)
// ==========================================

@Serializable
data class AppConfiguration(
    val appVersion: String = "1.0.0",
    val isMaintenanceMode: Boolean = false,
    val isForceUpdate: Boolean = false,
    val supportContact: String = "+1-800-CABKARO",
    val whatsappBookingNumber: String = "+1-800-CAB-WAPP"
)

@Serializable
data class PricingConfig(
    val sedanPricePerKm: Double = 15.0,
    val suvPricePerKm: Double = 22.0,
    val minimumBookingDistanceKm: Double = 100.0,
    val nightSurcharge: Double = 0.0,
    val holidaySurcharge: Double = 0.0,
    val waitingCharge: Double = 0.0,
    val tollPolicy: String = "Tolls are extra where applicable"
)

@Serializable
data class BookingPolicies(
    val cancellationFee: Double = 50.0,
    val cancellationPolicyText: String = "Free cancellation within 5 minutes of booking.",
    val bookingNoticeTitle: String = "Important Ride Information",
    val bookingNoticeBody: String = "Please verify your rider PIN with the driver before starting the trip.",
    val additionalRiderPolicies: List<String> = emptyList()
)

@Serializable
data class PopularRoute(
    val routeId: String,
    val routeName: String,
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffAddress: String,
    val dropoffLat: Double,
    val dropoffLng: Double,
    val estimatedDistance: Double,
    val estimatedDuration: String,
    val fixedPromotionalFare: Double,
    val supportedVehicleTypes: List<String>,
    val displayPriority: Int = 0,
    val isActive: Boolean = true
)

// ==========================================
// 2. Room Cache Entities (Saved in Database)
// ==========================================

@Entity(tableName = "app_config_cache")
data class AppConfigEntity(
    @PrimaryKey val id: String = "singleton",
    val appVersion: String,
    val isMaintenanceMode: Boolean,
    val isForceUpdate: Boolean,
    val supportContact: String,
    val whatsappBookingNumber: String
)

@Entity(tableName = "pricing_config_cache")
data class PricingConfigEntity(
    @PrimaryKey val id: String = "singleton",
    val sedanPricePerKm: Double,
    val suvPricePerKm: Double,
    val minimumBookingDistanceKm: Double,
    val nightSurcharge: Double,
    val holidaySurcharge: Double,
    val waitingCharge: Double,
    val tollPolicy: String
)

@Entity(tableName = "booking_policies_cache")
data class BookingPoliciesEntity(
    @PrimaryKey val id: String = "singleton",
    val cancellationFee: Double,
    val cancellationPolicyText: String,
    val bookingNoticeTitle: String,
    val bookingNoticeBody: String,
    val additionalRiderPoliciesJoined: String
)

@Entity(tableName = "popular_routes_cache")
data class PopularRouteEntity(
    @PrimaryKey val routeId: String,
    val routeName: String,
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffAddress: String,
    val dropoffLat: Double,
    val dropoffLng: Double,
    val estimatedDistance: Double,
    val estimatedDuration: String,
    val fixedPromotionalFare: Double,
    val supportedVehicleTypesJoined: String,
    val displayPriority: Int,
    val isActive: Boolean
)
