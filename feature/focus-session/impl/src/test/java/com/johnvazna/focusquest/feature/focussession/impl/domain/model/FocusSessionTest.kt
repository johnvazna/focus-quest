package com.johnvazna.focusquest.feature.focussession.impl.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FocusSessionTest {

    @Test
    fun `start rejects duration below minimum`() {
        assertThrows(IllegalArgumentException::class.java) {
            FocusSession.start(FocusSessionPolicy.MIN_DURATION_MINUTES - 1)
        }
    }

    @Test
    fun `completion awards one experience point per planned minute`() {
        val completedSession = FocusSession.start(durationMinutes = 25).advance(elapsedMinutes = 25)

        assertEquals(FocusSessionStatus.COMPLETED, completedSession.status)
        assertEquals(25, completedSession.earnedExperiencePoints)
        assertEquals(0, completedSession.remainingDurationMinutes)
    }

    @Test
    fun `cancellation awards no experience`() {
        val cancelledSession = FocusSession.start(durationMinutes = 25).cancel()

        assertEquals(FocusSessionStatus.CANCELLED, cancelledSession.status)
        assertEquals(0, cancelledSession.earnedExperiencePoints)
    }

    @Test
    fun `session cannot exceed pause limit`() {
        var session = FocusSession.start(durationMinutes = 25)
        repeat(FocusSessionPolicy.MAX_PAUSE_COUNT) {
            session = session.pause().resume()
        }

        assertThrows(IllegalStateException::class.java) {
            session.pause()
        }
    }
}
