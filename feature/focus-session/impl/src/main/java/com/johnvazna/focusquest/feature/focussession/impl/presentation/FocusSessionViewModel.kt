package com.johnvazna.focusquest.feature.focussession.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSessionPolicy
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSessionStatus
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSession
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSessionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusSessionViewModel(
    private val startFocusSession: StartFocusSession,
    private val ticker: FocusSessionTicker,
) : ViewModel() {
    private val _state = MutableStateFlow(FocusSessionUiState())
    val state: StateFlow<FocusSessionUiState> = _state.asStateFlow()

    private val effectsChannel = Channel<FocusSessionUiEffect>(Channel.BUFFERED)
    val effects: Flow<FocusSessionUiEffect> = effectsChannel.receiveAsFlow()

    private var session: FocusSession? = null
    private var tickerJob: Job? = null

    fun onEvent(event: FocusSessionUiEvent) {
        when (event) {
            is FocusSessionUiEvent.DurationChanged -> changeDuration(event.minutes)
            FocusSessionUiEvent.Start -> start()
            FocusSessionUiEvent.Pause -> pause()
            FocusSessionUiEvent.Resume -> resume()
            FocusSessionUiEvent.Cancel -> cancel()
        }
    }

    private fun changeDuration(minutes: Int) {
        if (_state.value.status != FocusSessionUiStatus.IDLE) return
        _state.update { it.copy(selectedDurationMinutes = minutes, remainingMinutes = minutes) }
    }

    private fun start() {
        if (_state.value.status != FocusSessionUiStatus.IDLE) return
        _state.update { it.copy(status = FocusSessionUiStatus.STARTING) }
        viewModelScope.launch {
            when (val result = startFocusSession(_state.value.selectedDurationMinutes)) {
                is StartFocusSessionResult.Started -> {
                    session = result.session
                    publishSession(result.session)
                    startTicker()
                }
                StartFocusSessionResult.InvalidDuration -> rejectStart(FocusSessionUiEffect.InvalidDuration)
                StartFocusSessionResult.SessionAlreadyActive -> rejectStart(FocusSessionUiEffect.SessionAlreadyActive)
            }
        }
    }

    private fun pause() {
        if (_state.value.status != FocusSessionUiStatus.ACTIVE) return
        updateSession { it.pause() }
    }

    private fun resume() {
        if (_state.value.status != FocusSessionUiStatus.PAUSED) return
        updateSession { it.resume() }
        startTicker()
    }

    private fun cancel() {
        if (_state.value.status !in cancellableStatuses) return
        tickerJob?.cancel()
        updateSession { it.cancel() }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            ticker.ticks().collect {
                updateSession { current -> current.advance(1) }
                if (session?.status == FocusSessionStatus.COMPLETED) tickerJob?.cancel()
            }
        }
    }

    private fun updateSession(transform: (FocusSession) -> FocusSession) {
        val updated = session?.let(transform) ?: return
        session = updated
        publishSession(updated)
        if (updated.status == FocusSessionStatus.PAUSED) tickerJob?.cancel()
    }

    private fun publishSession(value: FocusSession) {
        _state.value = _state.value.copy(
            remainingMinutes = value.remainingDurationMinutes,
            pauseCount = value.pauseCount,
            status = value.status.toUiStatus(),
        )
    }

    private suspend fun rejectStart(effect: FocusSessionUiEffect) {
        _state.update { it.copy(status = FocusSessionUiStatus.IDLE) }
        effectsChannel.send(effect)
    }

    private fun FocusSessionStatus.toUiStatus(): FocusSessionUiStatus = when (this) {
        FocusSessionStatus.ACTIVE -> FocusSessionUiStatus.ACTIVE
        FocusSessionStatus.PAUSED -> FocusSessionUiStatus.PAUSED
        FocusSessionStatus.COMPLETED -> FocusSessionUiStatus.COMPLETED
        FocusSessionStatus.CANCELLED -> FocusSessionUiStatus.CANCELLED
    }

    private companion object {
        val cancellableStatuses = setOf(FocusSessionUiStatus.ACTIVE, FocusSessionUiStatus.PAUSED)
    }
}
