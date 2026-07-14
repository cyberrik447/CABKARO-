package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Resource
import com.example.core.domain.repository.BookingRepository
import com.example.core.model.Booking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ActiveBookingState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val error: String? = null
)

sealed interface ActiveBookingEvent {
    object NavigateToHome : ActiveBookingEvent
    data class ShowToast(val message: String) : ActiveBookingEvent
}

class ActiveBookingViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ActiveBookingState())
    val state: StateFlow<ActiveBookingState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ActiveBookingEvent>()
    val events: SharedFlow<ActiveBookingEvent> = _events.asSharedFlow()

    fun loadBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.getBookingById(bookingId).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, booking = resource.data)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.exception.message)
                    }
                }
            }
        }
    }

    fun loadActiveBooking() {
        viewModelScope.launch {
            bookingRepository.getActiveBooking().collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, booking = resource.data)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.exception.message)
                    }
                }
            }
        }
    }

    fun cancelActiveBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isCancelling = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isCancelling = false, booking = null)
                        _events.emit(ActiveBookingEvent.ShowToast("Booking cancelled successfully."))
                        _events.emit(ActiveBookingEvent.NavigateToHome)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isCancelling = false, error = resource.exception.message)
                    }
                }
            }
        }
    }
}
