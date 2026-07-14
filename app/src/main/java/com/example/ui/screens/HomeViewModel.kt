package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Resource
import com.example.core.domain.repository.BookingRepository
import com.example.core.domain.repository.VehicleRepository
import com.example.core.domain.usecase.GetFinalEstimatedFareUseCase
import com.example.core.domain.usecase.ValidateBookingDistanceUseCase
import com.example.core.model.Booking
import com.example.core.model.Location
import com.example.core.model.Vehicle
import com.example.core.model.VehicleType
import com.example.core.config.PopularRoute
import com.example.core.config.ConfigManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HomeState(
    val pickupAddress: String = "",
    val dropoffAddress: String = "",
    val distanceKm: Double = 0.0,
    val selectedVehicleType: VehicleType? = null,
    val estimatedFare: Double? = null,
    val isFareLoading: Boolean = false,
    val error: String? = null,
    val supportedVehicles: List<Vehicle> = emptyList(),
    val activeBooking: Booking? = null,
    val isBookingLoading: Boolean = false,
    val popularRoutes: List<PopularRoute> = emptyList()
)

sealed interface HomeEvent {
    data class NavigateToConfirmation(
        val pickupAddress: String,
        val pickupLat: Double,
        val pickupLng: Double,
        val dropoffAddress: String,
        val dropoffLat: Double,
        val dropoffLng: Double,
        val distanceKm: Double
    ) : HomeEvent
    data class NavigateToActiveBooking(val bookingId: String) : HomeEvent
    data class ShowToast(val message: String) : HomeEvent
}

class HomeViewModel(
    private val bookingRepository: BookingRepository,
    private val vehicleRepository: VehicleRepository,
    private val getFinalEstimatedFareUseCase: GetFinalEstimatedFareUseCase,
    private val validateDistanceUseCase: ValidateBookingDistanceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        loadSupportedVehicles()
        observePopularRoutes()
    }

    private fun loadSupportedVehicles() {
        viewModelScope.launch {
            vehicleRepository.getSupportedVehicles().collectLatest { vehicles ->
                _state.value = _state.value.copy(supportedVehicles = vehicles)
            }
        }
    }

    private fun observePopularRoutes() {
        viewModelScope.launch {
            try {
                ConfigManager.getInstance().popularRoutes.collectLatest { routes ->
                    _state.value = _state.value.copy(popularRoutes = routes)
                }
            } catch (e: Exception) {
                // Safe check for tests or non-initialized platform state
            }
        }
    }

    fun selectPopularRoute(route: PopularRoute) {
        _state.value = _state.value.copy(
            pickupAddress = route.pickupAddress,
            dropoffAddress = route.dropoffAddress,
            distanceKm = route.estimatedDistance,
            selectedVehicleType = null,
            estimatedFare = null,
            error = null
        )
    }

    fun updatePickupAddress(address: String) {
        _state.value = _state.value.copy(pickupAddress = address)
    }

    fun updateDropoffAddress(address: String) {
        _state.value = _state.value.copy(dropoffAddress = address)
    }

    fun calculateDistanceAndSet() {
        val pickup = _state.value.pickupAddress
        val dropoff = _state.value.dropoffAddress
        if (pickup.isBlank() || dropoff.isBlank()) {
            _state.value = _state.value.copy(error = "Pickup and Dropoff addresses are required.")
            return
        }

        // Generate a mock but realistic deterministic distance based on the length of both strings.
        // This keeps it predictable and within the 100km requirement or lets us simulate it easily.
        val baseDistance = 100.0 + (pickup.length + dropoff.length) * 2.5
        _state.value = _state.value.copy(distanceKm = baseDistance, error = null)
        
        // Clear previous estimation
        _state.value = _state.value.copy(estimatedFare = null, selectedVehicleType = null)
    }


    fun selectVehicleType(vehicleType: VehicleType) {
        _state.value = _state.value.copy(selectedVehicleType = vehicleType)
        val distance = _state.value.distanceKm

        // Strict Business Rule check: Minimum booking distance is 100 km
        if (!validateDistanceUseCase(distance)) {
            _state.value = _state.value.copy(
                error = "Cabkaro Business Rule: Minimum booking distance is 100 km."
            )
            return
        }

        // Calculate estimated fare
        viewModelScope.launch {
            getFinalEstimatedFareUseCase(distance, vehicleType).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isFareLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        // Strict Business Rule: Users only see the final estimated fare. Details are hidden.
                        _state.value = _state.value.copy(
                            isFareLoading = false,
                            estimatedFare = resource.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isFareLoading = false,
                            error = resource.exception.message
                        )
                    }
                }
            }
        }
    }

    fun proceedToConfirmation() {
        val distance = _state.value.distanceKm
        val pickup = _state.value.pickupAddress
        val dropoff = _state.value.dropoffAddress

        if (pickup.isBlank() || dropoff.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter pickup and dropoff locations.")
            return
        }

        if (!validateDistanceUseCase(distance)) {
            _state.value = _state.value.copy(
                error = "Cabkaro Business Rule: Minimum booking distance is 100 km. Currently: ${String.format("%.1f", distance)} km."
            )
            return
        }

        viewModelScope.launch {
            _events.emit(
                HomeEvent.NavigateToConfirmation(
                    pickupAddress = pickup,
                    pickupLat = 28.6139, // Mock New Delhi coords
                    pickupLng = 77.2090,
                    dropoffAddress = dropoff,
                    dropoffLat = 19.0760, // Mock Mumbai coords
                    dropoffLng = 72.8777,
                    distanceKm = distance
                )
            )
        }
    }

    fun confirmBooking(
        pickupAddress: String,
        dropoffAddress: String,
        distanceKm: Double,
        vehicleType: VehicleType,
        fare: Double
    ) {
        viewModelScope.launch {
            bookingRepository.createBooking(
                pickup = Location(pickupAddress, com.example.core.model.LatLng(28.6139, 77.2090)),
                dropoff = Location(dropoffAddress, com.example.core.model.LatLng(19.0760, 72.8777)),
                vehicleType = vehicleType,
                distanceKm = distanceKm,
                finalEstimatedFare = fare
            ).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isBookingLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isBookingLoading = false,
                            activeBooking = resource.data
                        )
                        _events.emit(HomeEvent.NavigateToActiveBooking(resource.data.bookingId))
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isBookingLoading = false,
                            error = resource.exception.message
                        )
                    }
                }
            }
        }
    }
}
