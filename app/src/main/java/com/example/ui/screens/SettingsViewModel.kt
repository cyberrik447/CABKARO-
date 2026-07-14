package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.designsystem.theme.CabkaroThemeMode
import com.example.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsState(
    val themeMode: CabkaroThemeMode = CabkaroThemeMode.SOFT_PREMIUM,
    val isNotificationsEnabled: Boolean = true
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getThemePreference().collectLatest { themeStr ->
                val mode = when (themeStr) {
                    "PURE_WHITE" -> CabkaroThemeMode.PURE_WHITE
                    "PURE_BLACK" -> CabkaroThemeMode.PURE_BLACK
                    else -> CabkaroThemeMode.SOFT_PREMIUM
                }
                _state.value = _state.value.copy(themeMode = mode)
            }
        }
        viewModelScope.launch {
            settingsRepository.isNotificationEnabled().collectLatest { enabled ->
                _state.value = _state.value.copy(isNotificationsEnabled = enabled)
            }
        }
    }

    fun setTheme(themeMode: CabkaroThemeMode) {
        viewModelScope.launch {
            val themeStr = themeMode.name
            settingsRepository.saveThemePreference(themeStr)
            _state.value = _state.value.copy(themeMode = themeMode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationEnabled(enabled)
            _state.value = _state.value.copy(isNotificationsEnabled = enabled)
        }
    }
}
