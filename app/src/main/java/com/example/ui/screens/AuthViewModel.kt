package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Resource
import com.example.core.domain.repository.AuthRepository
import com.example.core.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AuthState(
    val mobileNumber: String = "",
    val otpCode: String = "",
    val isOtpRequested: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null,
    val registrationName: String = ""
)

sealed interface AuthEvent {
    object NavigateToHome : AuthEvent
    data class NavigateToOtp(val mobileNumber: String) : AuthEvent
    data class ShowToast(val message: String) : AuthEvent
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun updateMobileNumber(number: String) {
        _state.value = _state.value.copy(mobileNumber = number)
    }

    fun updateOtpCode(code: String) {
        _state.value = _state.value.copy(otpCode = code)
    }

    fun updateRegistrationName(name: String) {
        _state.value = _state.value.copy(registrationName = name)
    }

    fun requestOtp() {
        val number = _state.value.mobileNumber
        if (number.length < 10) {
            _state.value = _state.value.copy(error = "Please enter a valid 10-digit mobile number")
            return
        }

        viewModelScope.launch {
            authRepository.requestOtp(number).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, isOtpRequested = true)
                        _events.emit(AuthEvent.NavigateToOtp(number))
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.exception.message)
                    }
                }
            }
        }
    }

    fun verifyOtp() {
        val number = _state.value.mobileNumber
        val code = _state.value.otpCode
        if (code.length < 4) {
            _state.value = _state.value.copy(error = "Please enter a valid 4-digit OTP code")
            return
        }

        viewModelScope.launch {
            authRepository.verifyOtpAndLogin(number, code).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        val user = resource.data
                        _state.value = _state.value.copy(isLoading = false, currentUser = user)
                        if (user.isRegistered) {
                            _events.emit(AuthEvent.NavigateToHome)
                        } else {
                            // Needs registration name first before account creation
                            _events.emit(AuthEvent.ShowToast("Please enter your name to complete registration"))
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.exception.message)
                    }
                }
            }
        }
    }

    fun registerUser() {
        val name = _state.value.registrationName
        val currentUser = _state.value.currentUser
        if (name.isBlank()) {
            _state.value = _state.value.copy(error = "Name cannot be blank")
            return
        }
        if (currentUser == null) {
            _state.value = _state.value.copy(error = "No active session. Please authenticate again.")
            return
        }

        // Strict business rule: OTP verification is required before account creation.
        if (!currentUser.isOtpVerified) {
            _state.value = _state.value.copy(error = "OTP must be verified before account creation.")
            return
        }

        viewModelScope.launch {
            authRepository.createAccount(currentUser.copy(name = name)).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, currentUser = resource.data)
                        _events.emit(AuthEvent.NavigateToHome)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.exception.message)
                    }
                }
            }
        }
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { resource ->
                if (resource is Resource.Success && resource.data != null) {
                    val user = resource.data
                    if (user.isRegistered) {
                        _state.value = _state.value.copy(currentUser = user)
                        _events.emit(AuthEvent.NavigateToHome)
                    }
                }
            }
        }
    }
}
