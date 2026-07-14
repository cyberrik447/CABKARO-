package com.example.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base representation of the screen state.
 */
interface UiState

/**
 * Single-fire events sent from ViewModel to UI (e.g. navigation, displaying a toast).
 */
interface UiEvent

/**
 * Direct user intents or actions sent from UI to ViewModel (e.g. clicking a button, input change).
 */
interface UiAction

/**
 * Abstract class acting as a solid foundation for all Cabkaro ViewModels.
 * Mandates Unidirectional Data Flow.
 */
abstract class BaseViewModel<State : UiState, Event : UiEvent, Action : UiAction>(
    initialState: State
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<Event>()
    val uiEvent: SharedFlow<Event> = _uiEvent.asSharedFlow()

    /**
     * Updates the UI state in a thread-safe atomic manner.
     */
    protected fun updateState(reducer: State.() -> State) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * Emits a single-fire event to the UI safely within the coroutine context.
     */
    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    /**
     * Handles incoming actions dispatched by the user.
     */
    abstract fun onAction(action: Action)
}
