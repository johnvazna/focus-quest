package com.johnvazna.focusquest.feature.focussession.impl.data.mapper

import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSession
import org.junit.Assert.assertEquals
import org.junit.Test

class FocusSessionMapperTest {
    @Test
    fun `round trip preserves domain state`() {
        val session = FocusSession.start(25).pause()

        val restoredSession = session.toEntity().toDomain()

        assertEquals(session, restoredSession)
    }
}
