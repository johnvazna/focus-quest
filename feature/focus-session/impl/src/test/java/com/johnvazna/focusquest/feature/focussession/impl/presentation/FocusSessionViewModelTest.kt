package com.johnvazna.focusquest.feature.focussession.impl.presentation

import com.johnvazna.focusquest.feature.focussession.impl.domain.repository.FocusSessionRepository
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FocusSessionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state uses a twenty five minute session`() {
        val viewModel = createViewModel()

        assertEquals(FocusSessionUiState(), viewModel.state.value)
    }

    @Test
    fun `start publishes an active session`() = runTest {
        val viewModel = createViewModel()

        viewModel.onEvent(FocusSessionUiEvent.Start)
        runCurrent()

        assertEquals(FocusSessionUiStatus.ACTIVE, viewModel.state.value.status)
        assertEquals(25, viewModel.state.value.remainingMinutes)
    }

    @Test
    fun `invalid duration returns to idle and emits an effect`() = runTest {
        val viewModel = createViewModel()
        viewModel.onEvent(FocusSessionUiEvent.DurationChanged(minutes = 4))

        viewModel.onEvent(FocusSessionUiEvent.Start)
        runCurrent()

        assertEquals(FocusSessionUiStatus.IDLE, viewModel.state.value.status)
        assertEquals(FocusSessionUiEffect.InvalidDuration, viewModel.effects.first())
    }

    @Test
    fun `ticker reduces remaining time and completes the session`() = runTest {
        val ticker = FakeFocusSessionTicker()
        val viewModel = createViewModel(ticker = ticker)
        viewModel.onEvent(FocusSessionUiEvent.DurationChanged(minutes = 5))
        viewModel.onEvent(FocusSessionUiEvent.Start)
        runCurrent()

        repeat(5) {
            ticker.tick()
            runCurrent()
        }

        assertEquals(0, viewModel.state.value.remainingMinutes)
        assertEquals(FocusSessionUiStatus.COMPLETED, viewModel.state.value.status)
    }

    @Test
    fun `pause and resume control session progress`() = runTest {
        val ticker = FakeFocusSessionTicker()
        val viewModel = createViewModel(ticker = ticker)
        viewModel.onEvent(FocusSessionUiEvent.Start)
        runCurrent()

        viewModel.onEvent(FocusSessionUiEvent.Pause)
        ticker.tick()
        runCurrent()

        assertEquals(FocusSessionUiStatus.PAUSED, viewModel.state.value.status)
        assertEquals(1, viewModel.state.value.pauseCount)
        assertEquals(25, viewModel.state.value.remainingMinutes)

        viewModel.onEvent(FocusSessionUiEvent.Resume)
        runCurrent()
        ticker.tick()
        runCurrent()

        assertEquals(FocusSessionUiStatus.ACTIVE, viewModel.state.value.status)
        assertEquals(24, viewModel.state.value.remainingMinutes)
    }

    @Test
    fun `repository rejection emits already active effect`() = runTest {
        val viewModel = createViewModel(acceptSession = false)

        viewModel.onEvent(FocusSessionUiEvent.Start)
        runCurrent()

        assertEquals(FocusSessionUiEffect.SessionAlreadyActive, viewModel.effects.first())
        assertFalse(viewModel.state.value.status == FocusSessionUiStatus.STARTING)
    }

    private fun createViewModel(
        ticker: FocusSessionTicker = FakeFocusSessionTicker(),
        acceptSession: Boolean = true,
    ): FocusSessionViewModel {
        val repository = FocusSessionRepository { acceptSession }
        return FocusSessionViewModel(
            startFocusSession = StartFocusSession(repository),
            ticker = ticker,
        )
    }
}

private class FakeFocusSessionTicker : FocusSessionTicker {
    private val ticks = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun ticks() = ticks

    fun tick() {
        check(ticks.tryEmit(Unit))
    }
}
