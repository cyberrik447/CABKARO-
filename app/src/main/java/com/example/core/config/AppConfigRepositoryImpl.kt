package com.example.core.config

import com.example.core.config.ConfigMapper.toDomain
import com.example.core.config.ConfigMapper.toEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class AppConfigRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val configDao: ConfigDao
) : AppConfigRepository {

    override fun observeAppConfiguration(): Flow<AppConfiguration> {
        return configDao.observeAppConfig().map { entity ->
            entity?.toDomain() ?: AppConfiguration()
        }
    }

    override fun observePricing(): Flow<PricingConfig> {
        return configDao.observePricingConfig().map { entity ->
            entity?.toDomain() ?: PricingConfig()
        }
    }

    override fun observeBookingPolicies(): Flow<BookingPolicies> {
        return configDao.observeBookingPolicies().map { entity ->
            entity?.toDomain() ?: BookingPolicies()
        }
    }

    override fun observePopularRoutes(): Flow<List<PopularRoute>> {
        return configDao.observePopularRoutes().map { entities ->
            if (entities.isEmpty()) {
                getDefaultPopularRoutes()
            } else {
                entities.map { it.toDomain() }.filter { it.isActive }
            }
        }
    }

    override suspend fun refreshConfiguration() {
        Timber.d("Refreshing Remote Configuration from Firestore...")
        
        // 1. Refresh App Configuration
        try {
            val appConfigDoc = firestore.collection("config").document("app_config").get().awaitTask()
            if (appConfigDoc.exists()) {
                val appConfig = ConfigMapper.mapToAppConfig(appConfigDoc.data)
                configDao.insertAppConfig(appConfig.toEntity())
                Timber.d("Successfully refreshed App Config: $appConfig")
            } else {
                Timber.w("App Config document not found in Firestore. Caching fallback default.")
                configDao.insertAppConfig(AppConfiguration().toEntity())
            }
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing App Configuration from Firestore")
        }

        // 2. Refresh Pricing
        try {
            val pricingDoc = firestore.collection("config").document("pricing").get().awaitTask()
            if (pricingDoc.exists()) {
                val pricing = ConfigMapper.mapToPricingConfig(pricingDoc.data)
                configDao.insertPricingConfig(pricing.toEntity())
                Timber.d("Successfully refreshed Pricing Config: $pricing")
            } else {
                Timber.w("Pricing config document not found in Firestore. Caching fallback default.")
                configDao.insertPricingConfig(PricingConfig().toEntity())
            }
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing Pricing Config from Firestore")
        }

        // 3. Refresh Booking Policies
        try {
            val policiesDoc = firestore.collection("config").document("booking_policies").get().awaitTask()
            if (policiesDoc.exists()) {
                val policies = ConfigMapper.mapToBookingPolicies(policiesDoc.data)
                configDao.insertBookingPolicies(policies.toEntity())
                Timber.d("Successfully refreshed Booking Policies: $policies")
            } else {
                Timber.w("Booking policies document not found in Firestore. Caching fallback default.")
                configDao.insertBookingPolicies(BookingPolicies().toEntity())
            }
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing Booking Policies from Firestore")
        }

        // 4. Refresh Popular Routes
        try {
            val routesSnapshot = firestore.collection("popular_routes").get().awaitTask()
            if (!routesSnapshot.isEmpty) {
                val routes = routesSnapshot.documents.map { doc ->
                    ConfigMapper.mapToPopularRoute(doc.id, doc.data ?: emptyMap())
                }
                configDao.clearPopularRoutes()
                configDao.insertPopularRoutes(routes.map { it.toEntity() })
                Timber.d("Successfully refreshed ${routes.size} Popular Routes from Firestore.")
            } else {
                Timber.w("Popular routes collection is empty in Firestore. Pre-populating local defaults.")
                val currentLocalRoutes = configDao.getPopularRoutes()
                if (currentLocalRoutes.isEmpty()) {
                    configDao.insertPopularRoutes(getDefaultPopularRoutes().map { it.toEntity() })
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing Popular Routes from Firestore")
        }
    }

    private fun getDefaultPopularRoutes(): List<PopularRoute> {
        return listOf(
            PopularRoute(
                routeId = "route_delhi_agra",
                routeName = "Delhi to Agra (Taj Express)",
                pickupAddress = "Connaught Place, New Delhi",
                pickupLat = 28.6304,
                pickupLng = 77.2177,
                dropoffAddress = "Taj Mahal Parking, Agra, UP",
                dropoffLat = 27.1751,
                dropoffLng = 78.0421,
                estimatedDistance = 233.5,
                estimatedDuration = "4 hours 15 mins",
                fixedPromotionalFare = 3500.0,
                supportedVehicleTypes = listOf("SEDAN", "SUV"),
                displayPriority = 1,
                isActive = true
            ),
            PopularRoute(
                routeId = "route_mumbai_pune",
                routeName = "Mumbai to Pune Expressway",
                pickupAddress = "Gateway of India, Colaba, Mumbai",
                pickupLat = 18.9220,
                pickupLng = 72.8347,
                dropoffAddress = "Deccan Gymkhana, Pune, Maharashtra",
                dropoffLat = 18.5162,
                dropoffLng = 73.8405,
                estimatedDistance = 148.2,
                estimatedDuration = "3 hours 10 mins",
                fixedPromotionalFare = 2400.0,
                supportedVehicleTypes = listOf("SEDAN", "SUV"),
                displayPriority = 2,
                isActive = true
            ),
            PopularRoute(
                routeId = "route_bangalore_chennai",
                routeName = "Bangalore to Chennai Corridor",
                pickupAddress = "Kempegowda International Airport (BLR), Bengaluru",
                pickupLat = 13.1986,
                pickupLng = 77.7066,
                dropoffAddress = "Marina Beach, Triplicane, Chennai",
                dropoffLat = 13.0475,
                dropoffLng = 80.2824,
                estimatedDistance = 345.0,
                estimatedDuration = "6 hours 30 mins",
                fixedPromotionalFare = 5200.0,
                supportedVehicleTypes = listOf("SEDAN", "SUV"),
                displayPriority = 3,
                isActive = true
            )
        )
    }

    // Helper extension to await Firebase Tasks cleanly in Coroutines without external dependencies.
    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: RuntimeException("Firestore Task failed"))
            }
        }
    }
}
