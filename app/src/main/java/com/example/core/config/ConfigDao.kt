package com.example.core.config

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    @Query("SELECT * FROM app_config_cache WHERE id = 'singleton' LIMIT 1")
    fun observeAppConfig(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config_cache WHERE id = 'singleton' LIMIT 1")
    suspend fun getAppConfig(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppConfig(config: AppConfigEntity)


    @Query("SELECT * FROM pricing_config_cache WHERE id = 'singleton' LIMIT 1")
    fun observePricingConfig(): Flow<PricingConfigEntity?>

    @Query("SELECT * FROM pricing_config_cache WHERE id = 'singleton' LIMIT 1")
    suspend fun getPricingConfig(): PricingConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPricingConfig(pricing: PricingConfigEntity)


    @Query("SELECT * FROM booking_policies_cache WHERE id = 'singleton' LIMIT 1")
    fun observeBookingPolicies(): Flow<BookingPoliciesEntity?>

    @Query("SELECT * FROM booking_policies_cache WHERE id = 'singleton' LIMIT 1")
    suspend fun getBookingPolicies(): BookingPoliciesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingPolicies(policies: BookingPoliciesEntity)


    @Query("SELECT * FROM popular_routes_cache ORDER BY displayPriority ASC")
    fun observePopularRoutes(): Flow<List<PopularRouteEntity>>

    @Query("SELECT * FROM popular_routes_cache ORDER BY displayPriority ASC")
    suspend fun getPopularRoutes(): List<PopularRouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopularRoutes(routes: List<PopularRouteEntity>)

    @Query("DELETE FROM popular_routes_cache")
    suspend fun clearPopularRoutes()
}
