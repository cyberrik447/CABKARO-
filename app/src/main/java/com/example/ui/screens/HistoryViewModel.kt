package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Resource
import com.example.core.domain.repository.HistoryRepository
import com.example.core.model.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HistoryState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    fun loadHistory(userId: String = "current_user_id") {
        viewModelScope.launch {
            historyRepository.getUserBookingHistory(userId).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, bookings = resource.data)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.exception.message)
                    }
                }
            }
        }
    }
}
