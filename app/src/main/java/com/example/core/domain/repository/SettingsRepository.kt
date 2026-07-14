package com.example.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface managing local preferences such as theme selections.
 */
interface SettingsRepository {

    /**
     * Retrieves the selected theme preference.
     * Supported themes: Soft Premium, Pure White, Pure Black.
     */
    fun getThemePreference(): Flow<String>

    /**
     * Saves the theme preference.
     */
    suspend fun saveThemePreference(theme: String)

    /**
     * Retrieves whether push notifications are enabled.
     */
    fun isNotificationEnabled(): Flow<Boolean>

    /**
     * Saves whether push notifications are enabled.
     */
    suspend fun setNotificationEnabled(enabled: Boolean)
}
