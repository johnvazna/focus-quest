package com.johnvazna.focusquest.feature.focussession.impl

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.johnvazna.focusquest.feature.focussession.impl.data.local.FocusQuestDatabase
import com.johnvazna.focusquest.feature.focussession.impl.data.repository.RoomFocusSessionRepository
import com.johnvazna.focusquest.feature.focussession.impl.domain.usecase.StartFocusSession
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionTicker
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionUiEffect
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionUiEvent
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionUiStatus
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusSessionIntegrationTest {
    private lateinit var database: FocusQuestDatabase
    private lateinit var repository: RoomFocusSessionRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FocusQuestDatabase::class.java,
        ).build()
        repository = RoomFocusSessionRepository(database.focusSessionDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun startingFromViewModelPersistsSessionAndPreventsAnotherActiveSession() = runBlocking {
        val firstViewModel = createViewModel()
        firstViewModel.onEvent(FocusSessionUiEvent.DurationChanged(minutes = 45))
        firstViewModel.onEvent(FocusSessionUiEvent.Start)

        val activeState = withTimeout(TEST_TIMEOUT_MILLIS) {
            firstViewModel.state.first { it.status == FocusSessionUiStatus.ACTIVE }
        }
        val persistedSession = database.focusSessionDao().getActiveSession()

        assertEquals(45, activeState.remainingMinutes)
        assertEquals(45, persistedSession?.plannedDurationMinutes)
        assertEquals("ACTIVE", persistedSession?.status)

        val secondViewModel = createViewModel()
        secondViewModel.onEvent(FocusSessionUiEvent.Start)

        val effect = withTimeout(TEST_TIMEOUT_MILLIS) {
            secondViewModel.effects.first()
        }
        assertEquals(FocusSessionUiEffect.SessionAlreadyActive, effect)
        assertEquals(FocusSessionUiStatus.IDLE, secondViewModel.state.value.status)
    }

    private fun createViewModel() = FocusSessionViewModel(
        startFocusSession = StartFocusSession(repository),
        ticker = FocusSessionTicker { emptyFlow() },
    )

    private companion object {
        const val TEST_TIMEOUT_MILLIS = 5_000L
    }
}
