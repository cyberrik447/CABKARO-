package com.example.core.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Singleton configuration manager providing a single, highly performant, thread-safe API for the entire app.
 * Automatically loads cached data on startup, subscribes to repository updates, and shares state.
 */
class ConfigManager private constructor(
    private val repository: AppConfigRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val appConfiguration: StateFlow<AppConfiguration> = repository.observeAppConfiguration()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = AppConfiguration()
        )

    val currentPricing: StateFlow<PricingConfig> = repository.observePricing()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = PricingConfig()
        )

    val bookingPolicies: StateFlow<BookingPolicies> = repository.observeBookingPolicies()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = BookingPolicies()
        )

    val popularRoutes: StateFlow<List<PopularRoute>> = repository.observePopularRoutes()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        // Trigger background refresh on initialization
        scope.launch {
            try {
                repository.refreshConfiguration()
            } catch (e: Exception) {
                Timber.e(e, "Error during initial ConfigManager refresh")
            }
        }
    }

    /**
     * Manually triggers a background synchronization with Firestore.
     */
    fun forceRefresh() {
        scope.launch {
            try {
                repository.refreshConfiguration()
            } catch (e: Exception) {
                Timber.e(e, "Error during manual ConfigManager forceRefresh")
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ConfigManager? = null

        fun initialize(repository: AppConfigRepository): ConfigManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ConfigManager(repository).also { INSTANCE = it }
            }
        }

        fun getInstance(): ConfigManager {
            return INSTANCE ?: throw IllegalStateException(
                "ConfigManager is not initialized. Please call initialize(repository) first in Application."
            )
        }
    }
}
