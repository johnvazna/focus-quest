package com.johnvazna.focusquest.feature.focussession.impl.presentation

data class FocusSessionUiState(
    val selectedDurationMinutes: Int = 25,
    val remainingMinutes: Int = 25,
    val pauseCount: Int = 0,
    val status: FocusSessionUiStatus = FocusSessionUiStatus.IDLE,
)

enum class FocusSessionUiStatus { IDLE, STARTING, ACTIVE, PAUSED, COMPLETED, CANCELLED }

sealed interface FocusSessionUiEvent {
    data class DurationChanged(val minutes: Int) : FocusSessionUiEvent
    data object Start : FocusSessionUiEvent
    data object Pause : FocusSessionUiEvent
    data object Resume : FocusSessionUiEvent
    data object Cancel : FocusSessionUiEvent
}

sealed interface FocusSessionUiEffect {
    data object SessionAlreadyActive : FocusSessionUiEffect
    data object InvalidDuration : FocusSessionUiEffect
}
