package com.johnvazna.focusquest.feature.focussession.impl.domain.usecase

import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession
import com.johnvazna.focusquest.feature.focussession.impl.domain.repository.FocusSessionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StartFocusSessionTest {

    @Test
    fun `invalid duration is rejected without repository access`() = runTest {
        val repository = InMemoryFocusSessionRepository()

        val result = StartFocusSession(repository)(durationMinutes = 4)

        assertEquals(StartFocusSessionResult.InvalidDuration, result)
        assertEquals(0, repository.startAttempts)
    }

    @Test
    fun `only one session can be active`() = runTest {
        val repository = InMemoryFocusSessionRepository()
        val startFocusSession = StartFocusSession(repository)

        val firstResult = startFocusSession(durationMinutes = 25)
        val secondResult = startFocusSession(durationMinutes = 30)

        assertTrue(firstResult is StartFocusSessionResult.Started)
        assertEquals(StartFocusSessionResult.SessionAlreadyActive, secondResult)
    }

    private class InMemoryFocusSessionRepository : FocusSessionRepository {
        private var activeSession: FocusSession? = null
        var startAttempts: Int = 0
            private set

        override suspend fun startIfNoneActive(session: FocusSession): Boolean {
            startAttempts += 1
            if (activeSession != null) return false
            activeSession = session
            return true
        }
    }
}
