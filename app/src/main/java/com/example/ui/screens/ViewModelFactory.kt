package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.core.domain.repository.*
import com.example.core.domain.usecase.GetFinalEstimatedFareUseCase
import com.example.core.domain.usecase.ValidateBookingDistanceUseCase

class ViewModelFactory(
    private val authRepository: AuthRepository? = null,
    private val bookingRepository: BookingRepository? = null,
    private val vehicleRepository: VehicleRepository? = null,
    private val getFinalEstimatedFareUseCase: GetFinalEstimatedFareUseCase? = null,
    private val validateDistanceUseCase: ValidateBookingDistanceUseCase? = null,
    private val historyRepository: HistoryRepository? = null,
    private val settingsRepository: SettingsRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository!!) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    bookingRepository!!,
                    vehicleRepository!!,
                    getFinalEstimatedFareUseCase!!,
                    validateDistanceUseCase!!
                ) as T
            }
            modelClass.isAssignableFrom(ActiveBookingViewModel::class.java) -> {
                ActiveBookingViewModel(bookingRepository!!) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(historyRepository!!) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
