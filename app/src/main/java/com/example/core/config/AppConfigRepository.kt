package com.example.core.config

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing App Configuration, Pricing, Policies, and Routes.
 * Abstracts local Room caching and Firebase dynamic retrieval.
 */
interface AppConfigRepository {

    /**
     * Reactively observes the App Configuration, falling back to defaults if not yet cached.
     */
    fun observeAppConfiguration(): Flow<AppConfiguration>

    /**
     * Reactively observes current Pricing configurations.
     */
    fun observePricing(): Flow<PricingConfig>

    /**
     * Reactively observes current Booking Policies.
     */
    fun observeBookingPolicies(): Flow<BookingPolicies>

    /**
     * Reactively observes the active Popular Routes.
     */
    fun observePopularRoutes(): Flow<List<PopularRoute>>

    /**
     * Synchronizes local cache with the remote Firebase Firestore database.
     * Continues working with cached defaults in case of network/Firebase errors.
     */
    suspend fun refreshConfiguration()
}
